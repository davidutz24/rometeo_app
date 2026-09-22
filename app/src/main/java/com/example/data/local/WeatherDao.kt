package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM saved_locations ORDER BY createdAt ASC")
    fun getAllLocations(): Flow<List<SavedLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: SavedLocationEntity): Long

    @Query("DELETE FROM saved_locations WHERE id = :id")
    suspend fun deleteLocationById(id: Long)

    @Update
    suspend fun updateLocation(location: SavedLocationEntity)

    @Query("SELECT * FROM weather_cache WHERE cacheKey = :key LIMIT 1")
    fun getCachedWeather(key: String): Flow<WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache WHERE cacheKey = :key LIMIT 1")
    suspend fun getCachedWeatherOnce(key: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedWeather(cache: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE fetchedAt < :olderThanMillis")
    suspend fun clearOldCache(olderThanMillis: Long)
}
