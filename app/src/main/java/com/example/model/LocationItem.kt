package com.example.model

data class LocationItem(
    val id: Long = 0,
    val name: String,
    val country: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String = "auto",
    val isFavorite: Boolean = false
) {
    val displaySubtitle: String
        get() = listOfNotNull(admin1, country).filter { it.isNotBlank() }.joinToString(", ")

    companion object {
        val DEFAULT_LOCATIONS = listOf(
            LocationItem(
                id = 1,
                name = "Bucharest",
                country = "Romania",
                admin1 = "Municipiul București",
                latitude = 44.4323,
                longitude = 26.1063,
                timezone = "Europe/Bucharest",
                isFavorite = true
            ),
            LocationItem(
                id = 2,
                name = "Cluj-Napoca",
                country = "Romania",
                admin1 = "Cluj",
                latitude = 46.7712,
                longitude = 23.6236,
                timezone = "Europe/Bucharest",
                isFavorite = true
            ),
            LocationItem(
                id = 3,
                name = "Budapest",
                country = "Hungary",
                admin1 = "Budapest",
                latitude = 47.4979,
                longitude = 19.0402,
                timezone = "Europe/Budapest",
                isFavorite = true
            ),
            LocationItem(
                id = 4,
                name = "Timișoara",
                country = "Romania",
                admin1 = "Timiș",
                latitude = 45.7537,
                longitude = 21.2257,
                timezone = "Europe/Bucharest",
                isFavorite = false
            ),
            LocationItem(
                id = 5,
                name = "Brașov",
                country = "Romania",
                admin1 = "Brașov",
                latitude = 45.6579,
                longitude = 25.6012,
                timezone = "Europe/Bucharest",
                isFavorite = false
            ),
            LocationItem(
                id = 6,
                name = "Debrecen",
                country = "Hungary",
                admin1 = "Hajdú-Bihar",
                latitude = 47.5316,
                longitude = 21.6273,
                timezone = "Europe/Budapest",
                isFavorite = false
            ),
            LocationItem(
                id = 7,
                name = "Szeged",
                country = "Hungary",
                admin1 = "Csongrád-Csanád",
                latitude = 46.2530,
                longitude = 20.1414,
                timezone = "Europe/Budapest",
                isFavorite = false
            ),
            LocationItem(
                id = 8,
                name = "London",
                country = "United Kingdom",
                admin1 = "Greater London",
                latitude = 51.5074,
                longitude = -0.1278,
                timezone = "Europe/London",
                isFavorite = false
            )
        )
    }
}
