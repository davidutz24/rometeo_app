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
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
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

enum class EumetsatLayerMode(
    val layerId: String,
    val titleRo: String,
    val titleEn: String,
    val descriptionRo: String
) {
    VISIBLE(
        layerId = "satellite-europe-visible",
        titleRo = "Vizibil (HRV / Zi)",
        titleEn = "Visible (HRV / Day)",
        descriptionRo = "Spectru vizibil de înaltă rezoluție (RGB). Ideal pe timpul zilei pentru a observa textura și structura plafonului noros."
    ),
    INFRARED(
        layerId = "satellite-europe-infrared",
        titleRo = "Infra-Roșu (IR)",
        titleEn = "Infrared (IR)",
        descriptionRo = "Radiometru termic 10.8 µm operațional 24/7 (zi și noapte). Nori reci și sisteme convective evidențiate în alb strălucitor."
    ),
    NIGHT_MICROPHYSICS(
        layerId = "satellite-europe-nightmicrophysics",
        titleRo = "Night Microphysics",
        titleEn = "Night Microphysics",
        descriptionRo = "Distinge tipurile de nori noaptea: galben pentru ceață și nori joși, roz pentru nori medii, roșu/albastru pentru nori înalți."
    ),
    MTG(
        layerId = "satellite-europe-mtg",
        titleRo = "MTG Multispectral",
        titleEn = "MTG Multispectral",
        descriptionRo = "Meteosat Third Generation FCI: compoziție cromatică naturală automată (vizibil ziua, tranziție spre infraroșu noaptea)."
    )
}

data class EumetsatSatelliteFrame(
    val timestampId: String,
    val formattedTime: String,
    val timeUtc: String,
    val layerMode: EumetsatLayerMode,
    val imageUrl: String
)

