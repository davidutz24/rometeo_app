package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

enum class WeatherConditionType {
    CLEAR_DAY,
    CLEAR_NIGHT,
    MAINLY_CLEAR_DAY,
    MAINLY_CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,
    OVERCAST,
    FOG,
    DRIZZLE,
    RAIN,
    HEAVY_RAIN,
    SNOW,
    THUNDERSTORM
}

data class WeatherCondition(
    val code: Int,
    val type: WeatherConditionType,
    val icon: ImageVector,
    val isDay: Boolean = true
) {
    fun getDescription(lang: AppLanguage): String = when (code) {
        0 -> when (lang) {
            AppLanguage.ENGLISH -> if (isDay) "Clear Sky" else "Clear Night"
            AppLanguage.ROMANIAN -> if (isDay) "Cer Senin" else "Noapte Senină"
            AppLanguage.HUNGARIAN -> if (isDay) "Tiszta Égbolt" else "Tiszta Éjszaka"
        }
        1 -> when (lang) {
            AppLanguage.ENGLISH -> "Mainly Clear"
            AppLanguage.ROMANIAN -> "Predominant Senin"
            AppLanguage.HUNGARIAN -> "Túlnyomóan Tiszta"
        }
        2 -> when (lang) {
            AppLanguage.ENGLISH -> "Partly Cloudy"
            AppLanguage.ROMANIAN -> "Parțial Înnorat"
            AppLanguage.HUNGARIAN -> "Részben Felhős"
        }
        3 -> when (lang) {
            AppLanguage.ENGLISH -> "Overcast"
            AppLanguage.ROMANIAN -> "Cer Acoperit"
            AppLanguage.HUNGARIAN -> "Borult"
        }
        45, 48 -> when (lang) {
            AppLanguage.ENGLISH -> "Fog & Mist"
            AppLanguage.ROMANIAN -> "Ceață și Negură"
            AppLanguage.HUNGARIAN -> "Köd és Pára"
        }
        51, 53, 55 -> when (lang) {
            AppLanguage.ENGLISH -> "Drizzle"
            AppLanguage.ROMANIAN -> "Burniță"
            AppLanguage.HUNGARIAN -> "Szitáló Eső"
        }
        56, 57 -> when (lang) {
            AppLanguage.ENGLISH -> "Freezing Drizzle"
            AppLanguage.ROMANIAN -> "Burniță Înghețată"
            AppLanguage.HUNGARIAN -> "Ónos Szitálás"
        }
        61, 63 -> when (lang) {
            AppLanguage.ENGLISH -> "Rain"
            AppLanguage.ROMANIAN -> "Ploaie"
            AppLanguage.HUNGARIAN -> "Eső"
        }
        65 -> when (lang) {
            AppLanguage.ENGLISH -> "Heavy Rain"
            AppLanguage.ROMANIAN -> "Ploaie Torențială"
            AppLanguage.HUNGARIAN -> "Heves Eső"
        }
        66, 67 -> when (lang) {
            AppLanguage.ENGLISH -> "Freezing Rain"
            AppLanguage.ROMANIAN -> "Ploaie Înghețată"
            AppLanguage.HUNGARIAN -> "Ónos Eső"
        }
        71, 73, 75, 77 -> when (lang) {
            AppLanguage.ENGLISH -> "Snow Fall"
            AppLanguage.ROMANIAN -> "Ninsoare"
            AppLanguage.HUNGARIAN -> "Havazás"
        }
        80, 81, 82 -> when (lang) {
            AppLanguage.ENGLISH -> "Rain Showers"
            AppLanguage.ROMANIAN -> "Averse de Ploaie"
            AppLanguage.HUNGARIAN -> "Záporeső"
        }
        85, 86 -> when (lang) {
            AppLanguage.ENGLISH -> "Snow Showers"
            AppLanguage.ROMANIAN -> "Averse de Ninsoare"
            AppLanguage.HUNGARIAN -> "Hózápor"
        }
        95 -> when (lang) {
            AppLanguage.ENGLISH -> "Thunderstorm"
            AppLanguage.ROMANIAN -> "Furtună cu Descărcări"
            AppLanguage.HUNGARIAN -> "Zivatar"
        }
        96, 99 -> when (lang) {
            AppLanguage.ENGLISH -> "Severe Thunderstorm with Hail"
            AppLanguage.ROMANIAN -> "Furtună Violentă cu Grindină"
            AppLanguage.HUNGARIAN -> "Heves Zivatar Jégesővel"
        }
        else -> when (lang) {
            AppLanguage.ENGLISH -> "Fair"
            AppLanguage.ROMANIAN -> "Stabil"
            AppLanguage.HUNGARIAN -> "Változékony"
        }
    }

    companion object {
        fun fromWmoCode(code: Int, isDay: Boolean = true): WeatherCondition {
            val type = when (code) {
                0 -> if (isDay) WeatherConditionType.CLEAR_DAY else WeatherConditionType.CLEAR_NIGHT
                1 -> if (isDay) WeatherConditionType.MAINLY_CLEAR_DAY else WeatherConditionType.MAINLY_CLEAR_NIGHT
                2 -> if (isDay) WeatherConditionType.PARTLY_CLOUDY_DAY else WeatherConditionType.PARTLY_CLOUDY_NIGHT
                3 -> WeatherConditionType.OVERCAST
                45, 48 -> WeatherConditionType.FOG
                51, 53, 55, 56, 57 -> WeatherConditionType.DRIZZLE
                61, 63, 65, 66, 67, 80, 81, 82 -> if (code == 65 || code == 82) WeatherConditionType.HEAVY_RAIN else WeatherConditionType.RAIN
                71, 73, 75, 77, 85, 86 -> WeatherConditionType.SNOW
                95, 96, 99 -> WeatherConditionType.THUNDERSTORM
                else -> if (isDay) WeatherConditionType.CLEAR_DAY else WeatherConditionType.CLEAR_NIGHT
            }

            val icon = when (type) {
                WeatherConditionType.CLEAR_DAY,
                WeatherConditionType.CLEAR_NIGHT,
                WeatherConditionType.MAINLY_CLEAR_DAY,
                WeatherConditionType.MAINLY_CLEAR_NIGHT -> Icons.Rounded.WbSunny
                WeatherConditionType.PARTLY_CLOUDY_DAY,
                WeatherConditionType.PARTLY_CLOUDY_NIGHT,
                WeatherConditionType.OVERCAST -> Icons.Rounded.Cloud
                WeatherConditionType.FOG -> Icons.Rounded.Air
                WeatherConditionType.DRIZZLE,
                WeatherConditionType.RAIN,
                WeatherConditionType.HEAVY_RAIN -> Icons.Rounded.WaterDrop
                WeatherConditionType.SNOW -> Icons.Rounded.Grain
                WeatherConditionType.THUNDERSTORM -> Icons.Rounded.Thunderstorm
            }

            return WeatherCondition(code = code, type = type, icon = icon, isDay = isDay)
        }
    }
}
