package com.example.model

enum class AppNavTab(val id: String) {
    FORECASTS("forecasts"),
    RADAR_SATELLITE("radar_satellite"),
    ANM_WARNINGS("anm_warnings"),
    ANM_PRODUCTS("anm_products")
}

enum class AnmWarningSeverity(val code: String, val displayName: String, val hexColor: Long) {
    GREEN("verde", "Verde - Fără risc", 0xFF10B981),
    YELLOW("galben", "Cod Galben", 0xFFEAB308),
    ORANGE("portocaliu", "Cod Portocaliu", 0xFFF97316),
    RED("rosu", "Cod Roșu", 0xFFEF4444);

    companion object {
        fun fromColorName(name: String?): AnmWarningSeverity {
            return when (name?.lowercase()?.trim()) {
                "galben", "yellow" -> YELLOW
                "portocaliu", "orange" -> ORANGE
                "rosu", "roșu", "red" -> RED
                else -> YELLOW
            }
        }
    }
}

data class AnmWarningItem(
    val id: String,
    val typeName: String,
    val severity: AnmWarningSeverity,
    val validInterval: String,
    val startDate: String?,
    val endDate: String?,
    val affectedZones: String,
    val targetPhenomena: String,
    val fullMessageClean: String,
    val isNowcasting: Boolean,
    val mapImageUrl: String? = null
)

data class AnmProductItem(
    val id: String,
    val titleRo: String,
    val titleEn: String,
    val titleHu: String,
    val descriptionRo: String,
    val descriptionEn: String,
    val descriptionHu: String,
    val imageUrl: String,
    val updateFrequency: String,
    val category: String
)
