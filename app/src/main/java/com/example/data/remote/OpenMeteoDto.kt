package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "timezone") val timezone: String?,
    @Json(name = "current") val current: CurrentDto?,
    @Json(name = "hourly") val hourly: HourlyDto?,
    @Json(name = "daily") val daily: DailyDto?
)

@JsonClass(generateAdapter = true)
data class CurrentDto(
    @Json(name = "time") val time: String?,
    @Json(name = "temperature_2m") val temperature2m: Double?,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Double?,
    @Json(name = "apparent_temperature") val apparentTemperature: Double?,
    @Json(name = "precipitation") val precipitation: Double?,
    @Json(name = "weather_code") val weatherCode: Int?,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double?,
    @Json(name = "wind_direction_10m") val windDirection10m: Double?,
    @Json(name = "surface_pressure") val surfacePressure: Double?,
    @Json(name = "is_day") val isDay: Int?
)

@JsonClass(generateAdapter = true)
data class HourlyDto(
    @Json(name = "time") val time: List<String>?,
    @Json(name = "temperature_2m") val temperature2m: List<Double?>?,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: List<Double?>?,
    @Json(name = "precipitation") val precipitation: List<Double?>?,
    @Json(name = "weather_code") val weatherCode: List<Int?>?,
    @Json(name = "wind_speed_10m") val windSpeed10m: List<Double?>?,
    @Json(name = "wind_direction_10m") val windDirection10m: List<Double?>?,
    @Json(name = "surface_pressure") val surfacePressure: List<Double?>?
)

@JsonClass(generateAdapter = true)
data class DailyDto(
    @Json(name = "time") val time: List<String>?,
    @Json(name = "weather_code") val weatherCode: List<Int?>?,
    @Json(name = "temperature_2m_max") val temperature2mMax: List<Double?>?,
    @Json(name = "temperature_2m_min") val temperature2mMin: List<Double?>?,
    @Json(name = "precipitation_sum") val precipitationSum: List<Double?>?,
    @Json(name = "wind_speed_10m_max") val windSpeed10mMax: List<Double?>?,
    @Json(name = "surface_pressure_mean") val surfacePressureMean: List<Double?>?,
    @Json(name = "wind_direction_10m_dominant") val windDirection10mDominant: List<Double?>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @Json(name = "results") val results: List<GeocodingResultDto>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "country") val country: String?,
    @Json(name = "admin1") val admin1: String?,
    @Json(name = "timezone") val timezone: String?
)
