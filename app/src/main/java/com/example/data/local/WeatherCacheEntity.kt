package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val cacheKey: String, // e.g. "44.43_26.10_ecmwf_ifs"
    val latitude: Double,
    val longitude: Double,
    val modelId: String,
    val cityName: String,
    val country: String,
    val fetchedAt: Long,
    val currentWeatherJson: String,
    val hourlyForecastJson: String,
    val dailyForecastJson: String,
    val comparisonJson: String? = null
)
