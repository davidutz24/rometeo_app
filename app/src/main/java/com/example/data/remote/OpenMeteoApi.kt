package com.example.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getModelForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m,wind_direction_10m,surface_pressure,is_day",
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m,wind_direction_10m,surface_pressure",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max,surface_pressure_mean",
        @Query("models") model: String,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): ForecastResponse

    @GET
    suspend fun getCustomUrlForecast(
        @Url url: String
    ): ForecastResponse

    @GET
    suspend fun getRawMultiModel(
        @Url url: String
    ): ResponseBody

    @GET
    suspend fun searchLocation(
        @Url url: String
    ): GeocodingResponse
}
