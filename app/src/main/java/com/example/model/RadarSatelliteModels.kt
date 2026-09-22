package com.example.model

enum class RadarSatelliteSubTab(val id: String) {
    NATIONAL_RADAR("national_radar"),
    RADAR_STATIONS("radar_stations"),
    RAINVIEWER_RADAR("rainviewer_radar"),
    METEOSAT("meteosat")
}

data class AnmRadarFrame(
    val index: Int,
    val imageUrl: String,
    val timeString: String
)

data class RadarStationInfo(
    val code: String,
    val name: String,
    val county: String,
    val locationName: String,
    val coordinates: String,
    val elevationMeters: Int,
    val radarModel: String,
    val band: String,
    val coverageRadiusKm: Int,
    val openDataUrl: String,
    val parameters: List<String>,
    val descriptionRo: String,
    val descriptionEn: String
)

data class RadarStationFileItem(
    val filename: String,
    val productType: String,
    val timestampStr: String,
    val fileSizeStr: String,
    val downloadUrl: String
)

data class MeteosatChannelInfo(
    val tipNumber: Int,
    val channelId: String,
    val titleRo: String,
    val titleEn: String,
    val descriptionRo: String,
    val descriptionEn: String,
    val resolution: String,
    val spectrum: String
)

data class MeteosatFrame(
    val imageUrl: String,
    val timeUtc: String,
    val formattedTime: String
)

data class RainViewerRadarFrame(
    val timeUnix: Long,
    val path: String,
    val formattedTime: String,
    val isNowcast: Boolean
)
