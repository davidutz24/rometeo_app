package com.example.data.repository

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.SavedLocationEntity
import com.example.data.local.WeatherCacheEntity
import com.example.data.remote.ForecastResponse
import com.example.data.remote.NetworkClient
import com.example.model.CurrentWeather
import com.example.model.DailyPoint
import com.example.model.HourlyPoint
import com.example.model.LocationItem
import com.example.model.ModelComparisonHour
import com.example.model.ModelForecastValue
import com.example.model.WeatherCondition
import com.example.model.WeatherForecastResult
import com.example.model.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository(
    private val database: AppDatabase
) {
    private val api = NetworkClient.api
    private val dao = database.weatherDao()
    private val moshi = NetworkClient.moshi

    fun getSavedLocations(): Flow<List<LocationItem>> {
        return dao.getAllLocations().map { list ->
            if (list.isEmpty()) {
                LocationItem.DEFAULT_LOCATIONS.take(3)
            } else {
                list.map { it.toDomain() }
            }
        }
    }

    suspend fun saveLocation(item: LocationItem) = withContext(Dispatchers.IO) {
        dao.insertLocation(SavedLocationEntity.fromDomain(item))
    }

    suspend fun deleteLocation(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteLocationById(id)
    }

    fun getWeatherForecast(
        location: LocationItem,
        model: WeatherModel,
        forceRefresh: Boolean = false
    ): Flow<Result<WeatherForecastResult>> = flow {
        val cacheKey = "${String.format(Locale.US, "%.2f_%.2f", location.latitude, location.longitude)}_${model.id}"

        // 1. Check local cache
        val cached = withContext(Dispatchers.IO) {
            dao.getCachedWeatherOnce(cacheKey)
        }

        if (cached != null) {
            try {
                val parsedCached = parseCachedResult(location, model, cached)
                if (parsedCached != null) {
                    emit(Result.success(parsedCached))
                }
            } catch (e: Exception) {
                Log.e("WeatherRepo", "Failed parsing cache", e)
            }
        }

        // 2. Fetch fresh data from network if needed or on launch
        try {
            val (forecast, comparison) = withContext(Dispatchers.IO) {
                val forecastDeferred = when (model) {
                    WeatherModel.WEATHERNEXT_3_SHORT -> {
                        val url = "https://ensemble-api.open-meteo.com/v1/ensemble?latitude=${location.latitude}&longitude=${location.longitude}&models=google_weathernext2_ensemble&hourly=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m,wind_direction_10m,surface_pressure&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max,surface_pressure_mean,wind_direction_10m_dominant&forecast_days=3&timezone=auto"
                        api.getCustomUrlForecast(url)
                    }
                    WeatherModel.WEATHERNEXT_3_MEDIUM -> {
                        val url = "https://ensemble-api.open-meteo.com/v1/ensemble?latitude=${location.latitude}&longitude=${location.longitude}&models=google_weathernext2_ensemble&hourly=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m,wind_direction_10m,surface_pressure&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max,surface_pressure_mean,wind_direction_10m_dominant&forecast_days=15&timezone=auto"
                        api.getCustomUrlForecast(url)
                    }
                    WeatherModel.ECMWF_EXTENDED -> {
                        val url = "https://seasonal-api.open-meteo.com/v1/seasonal?latitude=${location.latitude}&longitude=${location.longitude}&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max,surface_pressure_mean&hourly=temperature_2m,precipitation,weather_code,wind_speed_10m&forecast_days=46&timezone=auto"
                        api.getCustomUrlForecast(url)
                    }
                    else -> {
                        api.getModelForecast(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            model = model.apiKey,
                            forecastDays = if (model == WeatherModel.ICON_EU_FLASH) 3 else 7
                        )
                    }
                }
                val compData = try {
                    fetchMultiModelComparison(location.latitude, location.longitude)
                } catch (e: Exception) {
                    emptyList()
                }
                Pair(forecastDeferred, compData)
            }

            val domainResult = mapToDomain(location, model, forecast, comparison, System.currentTimeMillis(), false)

            // Save to Room Cache
            withContext(Dispatchers.IO) {
                saveToCache(cacheKey, location, model, domainResult)
            }

            emit(Result.success(domainResult))
        } catch (e: Exception) {
            Log.w("WeatherRepo", "Network fetch failed: ${e.message}")
            if (cached != null) {
                val parsedCached = parseCachedResult(location, model, cached)
                if (parsedCached != null) {
                    emit(Result.success(parsedCached.copy(isOfflineCache = true)))
                } else {
                    emit(Result.failure(e))
                }
            } else {
                emit(Result.failure(e))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun searchLocations(query: String): List<LocationItem> = withContext(Dispatchers.IO) {
        if (query.trim().length < 2) return@withContext emptyList()
        try {
            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val url = "https://geocoding-api.open-meteo.com/v1/search?name=$encoded&count=8&format=json"
            val response = api.searchLocation(url)
            response.results?.map { dto ->
                LocationItem(
                    id = dto.id,
                    name = dto.name,
                    country = dto.country ?: "",
                    admin1 = dto.admin1,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    timezone = dto.timezone ?: "auto",
                    isFavorite = false
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Search error", e)
            emptyList()
        }
    }

    private suspend fun fetchMultiModelComparison(lat: Double, lon: Double): List<ModelComparisonHour> {
        val modelsParam = "ecmwf_ifs025,ecmwf_aifs025,icon_global,icon_eu,meteofrance_arpege_europe,gfs_global"
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&hourly=temperature_2m,precipitation&models=$modelsParam&forecast_days=3"
        val responseBody = api.getRawMultiModel(url)
        val jsonString = responseBody.string()
        val root = JSONObject(jsonString)
        val hourly = root.optJSONObject("hourly") ?: return emptyList()

        val timeArray = hourly.optJSONArray("time") ?: return emptyList()
        val count = minOf(timeArray.length(), 48) // next 48 hours
        val result = mutableListOf<ModelComparisonHour>()

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val tempIFS = hourly.optJSONArray("temperature_2m_ecmwf_ifs025")
        val tempAIFS = hourly.optJSONArray("temperature_2m_ecmwf_aifs025")
        val tempICON = hourly.optJSONArray("temperature_2m_icon_global")
        val tempICONEU = hourly.optJSONArray("temperature_2m_icon_eu")
        val tempARPEGE = hourly.optJSONArray("temperature_2m_meteofrance_arpege_europe")
        val tempGFS = hourly.optJSONArray("temperature_2m_gfs_global")

        val precipIFS = hourly.optJSONArray("precipitation_ecmwf_ifs025")
        val precipAIFS = hourly.optJSONArray("precipitation_ecmwf_aifs025")
        val precipICON = hourly.optJSONArray("precipitation_icon_global")
        val precipICONEU = hourly.optJSONArray("precipitation_icon_eu")
        val precipARPEGE = hourly.optJSONArray("precipitation_meteofrance_arpege_europe")
        val precipGFS = hourly.optJSONArray("precipitation_gfs_global")

        for (i in 0 until count) {
            val iso = timeArray.optString(i)
            val displayHour = try {
                val date = isoFormat.parse(iso)
                if (date != null) hourFormat.format(date) else iso.takeLast(5)
            } catch (e: Exception) {
                iso.takeLast(5)
            }

            val values = listOf(
                ModelForecastValue(WeatherModel.WEATHERNEXT_3_SHORT, tempAIFS?.optDouble(i)?.takeIf { !it.isNaN() } ?: tempIFS?.optDouble(i)?.takeIf { !it.isNaN() }, precipAIFS?.optDouble(i)),
                ModelForecastValue(WeatherModel.ECMWF_IFS, tempIFS?.optDouble(i)?.takeIf { !it.isNaN() }, precipIFS?.optDouble(i)),
                ModelForecastValue(WeatherModel.ECMWF_AIFS, tempAIFS?.optDouble(i)?.takeIf { !it.isNaN() }, precipAIFS?.optDouble(i)),
                ModelForecastValue(WeatherModel.ICON, tempICON?.optDouble(i)?.takeIf { !it.isNaN() }, precipICON?.optDouble(i)),
                ModelForecastValue(WeatherModel.ICON_EU, tempICONEU?.optDouble(i)?.takeIf { !it.isNaN() }, precipICONEU?.optDouble(i)),
                ModelForecastValue(WeatherModel.ICON_EU_FLASH, tempICONEU?.optDouble(i)?.takeIf { !it.isNaN() }, precipICONEU?.optDouble(i)),
                ModelForecastValue(WeatherModel.ARPEGE, tempARPEGE?.optDouble(i)?.takeIf { !it.isNaN() }, precipARPEGE?.optDouble(i)),
                ModelForecastValue(WeatherModel.GFS, tempGFS?.optDouble(i)?.takeIf { !it.isNaN() }, precipGFS?.optDouble(i))
            )

            result.add(ModelComparisonHour(timeIso = iso, displayHour = displayHour, values = values))
        }
        return result
    }

    private fun mapToDomain(
        location: LocationItem,
        model: WeatherModel,
        dto: ForecastResponse,
        comparison: List<ModelComparisonHour>,
        timestamp: Long,
        isOffline: Boolean
    ): WeatherForecastResult {
        val currentDto = dto.current
        val hourlyDto = dto.hourly
        val dailyDto = dto.daily

        val isDay = currentDto?.isDay == 1 || (currentDto?.isDay == null && true)
        val curWeatherCode = currentDto?.weatherCode ?: hourlyDto?.weatherCode?.firstOrNull() ?: 0
        val condition = WeatherCondition.fromWmoCode(curWeatherCode, isDay)

        val current = CurrentWeather(
            time = currentDto?.time ?: SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            temperature = currentDto?.temperature2m ?: hourlyDto?.temperature2m?.firstOrNull() ?: 20.0,
            apparentTemperature = currentDto?.apparentTemperature ?: currentDto?.temperature2m ?: 20.0,
            relativeHumidity = currentDto?.relativeHumidity2m?.toInt() ?: hourlyDto?.relativeHumidity2m?.firstOrNull()?.toInt() ?: 50,
            precipitation = currentDto?.precipitation ?: 0.0,
            weatherCondition = condition,
            windSpeed = currentDto?.windSpeed10m ?: 10.0,
            windDirection = currentDto?.windDirection10m?.toInt() ?: 0,
            surfacePressure = currentDto?.surfacePressure ?: 1013.25,
            isDay = isDay
        )

        // Hourly
        val hourlyList = mutableListOf<HourlyPoint>()
        val timeList = hourlyDto?.time ?: emptyList()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val maxHours = minOf(timeList.size, 36) // next 36 hours
        for (i in 0 until maxHours) {
            val iso = timeList[i]
            val displayHour = try {
                val d = isoFormat.parse(iso)
                if (d != null) hourFormat.format(d) else iso.takeLast(5)
            } catch (e: Exception) {
                iso.takeLast(5)
            }
            val code = hourlyDto?.weatherCode?.getOrNull(i) ?: 0
            val isDayHour = try {
                val hourInt = iso.substringAfter('T').take(2).toInt()
                hourInt in 6..20
            } catch (e: Exception) {
                true
            }

            hourlyList.add(
                HourlyPoint(
                    timeIso = iso,
                    displayHour = displayHour,
                    temperature = hourlyDto?.temperature2m?.getOrNull(i) ?: current.temperature,
                    relativeHumidity = hourlyDto?.relativeHumidity2m?.getOrNull(i)?.toInt() ?: 50,
                    precipitation = hourlyDto?.precipitation?.getOrNull(i) ?: 0.0,
                    weatherCondition = WeatherCondition.fromWmoCode(code, isDayHour),
                    windSpeed = hourlyDto?.windSpeed10m?.getOrNull(i) ?: 0.0,
                    windDirection = hourlyDto?.windDirection10m?.getOrNull(i)?.toInt() ?: 0,
                    surfacePressure = hourlyDto?.surfacePressure?.getOrNull(i) ?: 1013.25
                )
            )
        }

        // Daily
        val dailyList = mutableListOf<DailyPoint>()
        val extendedDailyList = mutableListOf<DailyPoint>()
        val dayTimes = dailyDto?.time ?: emptyList()
        val dayFormatIn = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayFormatOut = SimpleDateFormat("EEE d", Locale.getDefault())

        val dayCount = minOf(dayTimes.size, if (model == WeatherModel.ECMWF_EXTENDED) 46 else if (model == WeatherModel.WEATHERNEXT_3_MEDIUM) 15 else 7)
        for (i in 0 until dayCount) {
            val dIso = dayTimes[i]
            val displayDay = try {
                val d = dayFormatIn.parse(dIso)
                if (d != null) dayFormatOut.format(d) else dIso
            } catch (e: Exception) {
                dIso
            }
            val code = dailyDto?.weatherCode?.getOrNull(i) ?: 0
            val pressure = dailyDto?.surfacePressureMean?.getOrNull(i) ?: 1013.25
            val windDir = dailyDto?.windDirection10mDominant?.getOrNull(i)?.toInt() ?: 0
            val point = DailyPoint(
                dateIso = dIso,
                displayDay = displayDay,
                weatherCondition = WeatherCondition.fromWmoCode(code, true),
                maxTemperature = dailyDto?.temperature2mMax?.getOrNull(i) ?: (current.temperature + 4),
                minTemperature = dailyDto?.temperature2mMin?.getOrNull(i) ?: (current.temperature - 4),
                precipitationSum = dailyDto?.precipitationSum?.getOrNull(i) ?: 0.0,
                maxWindSpeed = dailyDto?.windSpeed10mMax?.getOrNull(i) ?: 15.0,
                surfacePressure = pressure,
                windDirection = windDir
            )
            if (i < 7) {
                dailyList.add(point)
            }
            extendedDailyList.add(point)
        }

        return WeatherForecastResult(
            location = location,
            model = model,
            current = current,
            hourly = hourlyList,
            daily = dailyList,
            comparison = comparison,
            fetchedAtTimestamp = timestamp,
            isOfflineCache = isOffline,
            extendedDaily = extendedDailyList
        )
    }

    private suspend fun saveToCache(
        cacheKey: String,
        location: LocationItem,
        model: WeatherModel,
        result: WeatherForecastResult
    ) {
        val currentJson = JSONObject().apply {
            put("time", result.current.time)
            put("temp", result.current.temperature)
            put("apparent", result.current.apparentTemperature)
            put("humidity", result.current.relativeHumidity)
            put("precipitation", result.current.precipitation)
            put("weatherCode", result.current.weatherCondition.code)
            put("windSpeed", result.current.windSpeed)
            put("windDir", result.current.windDirection)
            put("pressure", result.current.surfacePressure)
            put("isDay", result.current.isDay)
        }.toString()

        val hourlyJson = org.json.JSONArray().apply {
            result.hourly.forEach { h ->
                put(JSONObject().apply {
                    put("iso", h.timeIso)
                    put("displayHour", h.displayHour)
                    put("temp", h.temperature)
                    put("humidity", h.relativeHumidity)
                    put("precip", h.precipitation)
                    put("code", h.weatherCondition.code)
                    put("windSpeed", h.windSpeed)
                    put("windDir", h.windDirection)
                    put("pressure", h.surfacePressure)
                })
            }
        }.toString()

        val dailyJson = org.json.JSONArray().apply {
            result.daily.forEach { d ->
                put(JSONObject().apply {
                    put("iso", d.dateIso)
                    put("displayDay", d.displayDay)
                    put("code", d.weatherCondition.code)
                    put("maxTemp", d.maxTemperature)
                    put("minTemp", d.minTemperature)
                    put("precipSum", d.precipitationSum)
                    put("windMax", d.maxWindSpeed)
                    put("pressure", d.surfacePressure)
                    put("windDir", d.windDirection)
                })
            }
        }.toString()

        val compJson = org.json.JSONArray().apply {
            result.comparison.forEach { c ->
                put(JSONObject().apply {
                    put("iso", c.timeIso)
                    put("hour", c.displayHour)
                    val valuesObj = JSONObject()
                    c.values.forEach { v ->
                        valuesObj.put(v.model.id, v.temperature ?: JSONObject.NULL)
                    }
                    put("values", valuesObj)
                })
            }
        }.toString()

        val entity = WeatherCacheEntity(
            cacheKey = cacheKey,
            latitude = location.latitude,
            longitude = location.longitude,
            modelId = model.id,
            cityName = location.name,
            country = location.country,
            fetchedAt = result.fetchedAtTimestamp,
            currentWeatherJson = currentJson,
            hourlyForecastJson = hourlyJson,
            dailyForecastJson = dailyJson,
            comparisonJson = compJson
        )
        dao.insertCachedWeather(entity)
    }

    private fun parseCachedResult(
        location: LocationItem,
        model: WeatherModel,
        cache: WeatherCacheEntity
    ): WeatherForecastResult? {
        return try {
            val cObj = JSONObject(cache.currentWeatherJson)
            val isDay = cObj.optBoolean("isDay", true)
            val code = cObj.optInt("weatherCode", 0)
            val condition = WeatherCondition.fromWmoCode(code, isDay)
            val current = CurrentWeather(
                time = cObj.optString("time", ""),
                temperature = cObj.optDouble("temp", 20.0),
                apparentTemperature = cObj.optDouble("apparent", 20.0),
                relativeHumidity = cObj.optInt("humidity", 50),
                precipitation = cObj.optDouble("precipitation", 0.0),
                weatherCondition = condition,
                windSpeed = cObj.optDouble("windSpeed", 10.0),
                windDirection = cObj.optInt("windDir", 0),
                surfacePressure = cObj.optDouble("pressure", 1013.25),
                isDay = isDay
            )

            val hArray = org.json.JSONArray(cache.hourlyForecastJson)
            val hourly = mutableListOf<HourlyPoint>()
            for (i in 0 until hArray.length()) {
                val o = hArray.getJSONObject(i)
                val hCode = o.optInt("code", 0)
                val isDayHour = try {
                    val hourInt = o.optString("iso").substringAfter('T').take(2).toInt()
                    hourInt in 6..20
                } catch (e: Exception) {
                    true
                }
                hourly.add(
                    HourlyPoint(
                        timeIso = o.optString("iso"),
                        displayHour = o.optString("displayHour"),
                        temperature = o.optDouble("temp", 20.0),
                        relativeHumidity = o.optInt("humidity", 50),
                        precipitation = o.optDouble("precip", 0.0),
                        weatherCondition = WeatherCondition.fromWmoCode(hCode, isDayHour),
                        windSpeed = o.optDouble("windSpeed", 0.0),
                        windDirection = o.optInt("windDir", 0),
                        surfacePressure = o.optDouble("pressure", 1013.25)
                    )
                )
            }

            val dArray = org.json.JSONArray(cache.dailyForecastJson)
            val daily = mutableListOf<DailyPoint>()
            for (i in 0 until dArray.length()) {
                val o = dArray.getJSONObject(i)
                val dCode = o.optInt("code", 0)
                daily.add(
                    DailyPoint(
                        dateIso = o.optString("iso"),
                        displayDay = o.optString("displayDay"),
                        weatherCondition = WeatherCondition.fromWmoCode(dCode, true),
                        maxTemperature = o.optDouble("maxTemp", 24.0),
                        minTemperature = o.optDouble("minTemp", 16.0),
                        precipitationSum = o.optDouble("precipSum", 0.0),
                        maxWindSpeed = o.optDouble("windMax", 15.0),
                        surfacePressure = o.optDouble("pressure", 1013.25),
                        windDirection = o.optInt("windDir", 0)
                    )
                )
            }

            val compList = mutableListOf<ModelComparisonHour>()
            if (!cache.comparisonJson.isNullOrEmpty()) {
                val cArray = org.json.JSONArray(cache.comparisonJson)
                for (i in 0 until cArray.length()) {
                    val o = cArray.getJSONObject(i)
                    val valuesObj = o.optJSONObject("values")
                    val values = WeatherModel.entries.map { m ->
                        val t = if (valuesObj != null && !valuesObj.isNull(m.id)) valuesObj.optDouble(m.id) else null
                        ModelForecastValue(m, t, null)
                    }
                    compList.add(
                        ModelComparisonHour(
                            timeIso = o.optString("iso"),
                            displayHour = o.optString("hour"),
                            values = values
                        )
                    )
                }
            }

            WeatherForecastResult(
                location = location,
                model = model,
                current = current,
                hourly = hourly,
                daily = daily,
                comparison = compList,
                fetchedAtTimestamp = cache.fetchedAt,
                isOfflineCache = true
            )
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Error parsing cached data", e)
            null
        }
    }
}
