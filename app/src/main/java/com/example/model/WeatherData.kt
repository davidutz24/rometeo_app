package com.example.model

data class CurrentWeather(
    val time: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val relativeHumidity: Int,
    val precipitation: Double,
    val weatherCondition: WeatherCondition,
    val windSpeed: Double,
    val windDirection: Int,
    val surfacePressure: Double,
    val isDay: Boolean = true
)

data class HourlyPoint(
    val timeIso: String,
    val displayHour: String,
    val temperature: Double,
    val relativeHumidity: Int,
    val precipitation: Double,
    val weatherCondition: WeatherCondition,
    val windSpeed: Double,
    val windDirection: Int,
    val surfacePressure: Double
)

data class DailyPoint(
    val dateIso: String,
    val displayDay: String,
    val weatherCondition: WeatherCondition,
    val maxTemperature: Double,
    val minTemperature: Double,
    val precipitationSum: Double,
    val maxWindSpeed: Double,
    val surfacePressure: Double = 1013.25,
    val windDirection: Int = 0
)

data class ModelForecastValue(
    val model: WeatherModel,
    val temperature: Double?,
    val precipitation: Double?
)

data class ModelComparisonHour(
    val timeIso: String,
    val displayHour: String,
    val values: List<ModelForecastValue>
) {
    val tempMin: Double?
        get() = values.mapNotNull { it.temperature }.minOrNull()

    val tempMax: Double?
        get() = values.mapNotNull { it.temperature }.maxOrNull()

    val tempSpread: Double?
        get() = if (tempMin != null && tempMax != null) tempMax!! - tempMin!! else null
}

data class WeatherForecastResult(
    val location: LocationItem,
    val model: WeatherModel,
    val current: CurrentWeather,
    val hourly: List<HourlyPoint>,
    val daily: List<DailyPoint>,
    val comparison: List<ModelComparisonHour>,
    val fetchedAtTimestamp: Long,
    val isOfflineCache: Boolean = false,
    val extendedDaily: List<DailyPoint> = emptyList()
)
