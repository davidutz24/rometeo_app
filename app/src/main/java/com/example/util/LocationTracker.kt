package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.model.LocationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.function.Consumer
import kotlin.coroutines.resume

class LocationTracker(private val context: Context) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationItem? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission() || locationManager == null) {
            return@withContext null
        }

        val rawLocation: Location? = try {
            fetchDeviceLocation()
        } catch (e: Exception) {
            Log.e("LocationTracker", "Error getting device coordinates", e)
            null
        }

        if (rawLocation == null) {
            return@withContext null
        }

        // Reverse-geocode coordinates to city & region name
        val locationItem = reverseGeocode(rawLocation.latitude, rawLocation.longitude)
        locationItem
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchDeviceLocation(): Location? {
        if (locationManager == null) return null

        // Try getting fresh location on API 30+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val freshLocation = withTimeoutOrNull(4000L) {
                suspendCancellableCoroutine<Location?> { cont ->
                    val signal = CancellationSignal()
                    cont.invokeOnCancellation { signal.cancel() }
                    try {
                        val provider = when {
                            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                            else -> LocationManager.PASSIVE_PROVIDER
                        }
                        locationManager.getCurrentLocation(
                            provider,
                            signal,
                            ContextCompat.getMainExecutor(context),
                            Consumer { location ->
                                if (cont.isActive) cont.resume(location)
                            }
                        )
                    } catch (e: Exception) {
                        if (cont.isActive) cont.resume(null)
                    }
                }
            }
            if (freshLocation != null) return freshLocation
        }

        // Fallback to best last known location
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLocation == null || loc.time > bestLocation.time) {
                            bestLocation = loc
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        return bestLocation
    }

    private suspend fun reverseGeocode(latitude: Double, longitude: Double): LocationItem =
        withContext(Dispatchers.IO) {
            // 1. Try Android built-in Geocoder
            if (Geocoder.isPresent()) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        suspendCancellableCoroutine { cont ->
                            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                                override fun onGeocode(result: MutableList<Address>) {
                                    cont.resume(result)
                                }
                                override fun onError(errorMessage: String?) {
                                    cont.resume(emptyList())
                                }
                            })
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
                    }

                    val first = addresses.firstOrNull()
                    if (first != null) {
                        val cityName = first.locality
                            ?: first.subAdminArea
                            ?: first.adminArea
                            ?: first.featureName
                        if (!cityName.isNullOrBlank()) {
                            val countryName = first.countryName ?: "România"
                            val adminName = first.adminArea ?: ""
                            return@withContext LocationItem(
                                id = (latitude * 10000 + longitude).toLong(),
                                name = cityName,
                                country = countryName,
                                latitude = latitude,
                                longitude = longitude,
                                admin1 = adminName,
                                isFavorite = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w("LocationTracker", "Android Geocoder lookup failed: ${e.message}")
                }
            }

            // 2. Fallback to OpenStreetMap Nominatim reverse geocoder
            try {
                val url = URL("https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude&zoom=12&addressdetails=1")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "ROmeteo-App/1.0 (Android; rometeotechnologies@gmail.com)")
                    connectTimeout = 4000
                    readTimeout = 4000
                }

                if (connection.responseCode == 200) {
                    val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(jsonStr)
                    val address = json.optJSONObject("address")
                    if (address != null) {
                        val cityName = address.optString("city", "")
                            .ifBlank { address.optString("town", "") }
                            .ifBlank { address.optString("village", "") }
                            .ifBlank { address.optString("municipality", "") }
                            .ifBlank { address.optString("county", "") }

                        val country = address.optString("country", "România")
                        val state = address.optString("state", "")

                        if (cityName.isNotBlank()) {
                            return@withContext LocationItem(
                                id = (latitude * 10000 + longitude).toLong(),
                                name = cityName,
                                country = country,
                                latitude = latitude,
                                longitude = longitude,
                                admin1 = state,
                                isFavorite = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("LocationTracker", "Nominatim reverse geocode failed: ${e.message}")
            }

            // 3. Last fallback: coordinates label
            val latShort = String.format(Locale.US, "%.2f", latitude)
            val lonShort = String.format(Locale.US, "%.2f", longitude)
            LocationItem(
                id = (latitude * 10000 + longitude).toLong(),
                name = "Locație GPS ($latShort, $lonShort)",
                country = "România",
                latitude = latitude,
                longitude = longitude,
                admin1 = "GPS",
                isFavorite = false
            )
        }
}
