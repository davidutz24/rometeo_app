package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppLanguage
import com.example.model.LocationItem
import com.example.viewmodel.ThemeMode

class UserPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLastLocation(location: LocationItem) {
        prefs.edit()
            .putLong(KEY_LOC_ID, location.id)
            .putString(KEY_LOC_NAME, location.name)
            .putString(KEY_LOC_COUNTRY, location.country)
            .putString(KEY_LOC_ADMIN, location.admin1)
            .putFloat(KEY_LOC_LAT, location.latitude.toFloat())
            .putFloat(KEY_LOC_LON, location.longitude.toFloat())
            .putBoolean(KEY_LOC_FAVORITE, location.isFavorite)
            .apply()
    }

    fun getLastLocation(): LocationItem? {
        val name = prefs.getString(KEY_LOC_NAME, null) ?: return null
        val lat = prefs.getFloat(KEY_LOC_LAT, Float.NaN)
        val lon = prefs.getFloat(KEY_LOC_LON, Float.NaN)
        if (lat.isNaN() || lon.isNaN()) return null

        val country = prefs.getString(KEY_LOC_COUNTRY, "România") ?: "România"
        val admin = prefs.getString(KEY_LOC_ADMIN, null)
        val id = prefs.getLong(KEY_LOC_ID, 0L)
        val isFavorite = prefs.getBoolean(KEY_LOC_FAVORITE, false)

        return LocationItem(
            id = id,
            name = name,
            country = country,
            admin1 = admin,
            latitude = lat.toDouble(),
            longitude = lon.toDouble(),
            isFavorite = isFavorite
        )
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANG, language.code).apply()
    }

    fun getLanguage(): AppLanguage {
        val code = prefs.getString(KEY_LANG, AppLanguage.ROMANIAN.code) ?: AppLanguage.ROMANIAN.code
        return AppLanguage.entries.find { it.code == code } ?: AppLanguage.ROMANIAN
    }

    fun saveThemeMode(themeMode: ThemeMode) {
        prefs.edit().putString(KEY_THEME, themeMode.name).apply()
    }

    fun getThemeMode(): ThemeMode {
        val name = prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    companion object {
        private const val PREFS_NAME = "rometeo_user_preferences"
        private const val KEY_LOC_ID = "pref_location_id"
        private const val KEY_LOC_NAME = "pref_location_name"
        private const val KEY_LOC_COUNTRY = "pref_location_country"
        private const val KEY_LOC_ADMIN = "pref_location_admin"
        private const val KEY_LOC_LAT = "pref_location_latitude"
        private const val KEY_LOC_LON = "pref_location_longitude"
        private const val KEY_LOC_FAVORITE = "pref_location_is_favorite"
        private const val KEY_LANG = "pref_language_code"
        private const val KEY_THEME = "pref_theme_mode"
    }
}
