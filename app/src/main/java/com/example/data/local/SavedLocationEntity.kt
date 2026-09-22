package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.LocationItem

@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val country: String,
    val admin1: String?,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val isFavorite: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): LocationItem = LocationItem(
        id = id,
        name = name,
        country = country,
        admin1 = admin1,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        isFavorite = isFavorite
    )

    companion object {
        fun fromDomain(item: LocationItem): SavedLocationEntity = SavedLocationEntity(
            id = item.id,
            name = item.name,
            country = item.country,
            admin1 = item.admin1,
            latitude = item.latitude,
            longitude = item.longitude,
            timezone = item.timezone,
            isFavorite = item.isFavorite
        )
    }
}
