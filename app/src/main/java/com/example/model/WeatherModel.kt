package com.example.model

import androidx.compose.ui.graphics.Color

enum class ForecastCategory(
    val id: String,
    val titleRes: String,
    val subtitle: String,
    val horizonDays: Int
) {
    FORECAST(
        id = "forecast",
        titleRes = "category_forecast",
        subtitle = "0 – 15 Zile (Termen Scurt & Mediu)",
        horizonDays = 15
    ),
    LONG_TERM(
        id = "long_term",
        titleRes = "category_long_term",
        subtitle = "15 – 46 Zile (Ansambluri Sub-Sezoniere & Climatice)",
        horizonDays = 46
    );

    companion object {
        val SHORT_TERM get() = FORECAST
        val MEDIUM_TERM get() = FORECAST
    }
}

enum class WeatherModel(
    val id: String,
    val apiKey: String,
    val displayName: String,
    val shortName: String,
    val badgeText: String,
    val subtitleTagline: String,
    val agency: String,
    val resolution: String,
    val architectureType: String,
    val updateCycle: String,
    val accentColor: Color,
    val isAi: Boolean = false,
    val isRapidUpdate: Boolean = false,
    val isLongRange: Boolean = false,
    val categories: List<ForecastCategory> = emptyList()
) {
    // -------------------------------------------------------------------------
    // Categoria 1: Prognoză (Termen Scurt & Mediu: 0 – 15 Zile)
    // Ordine:
    // 1. ICON-EU Flash (30 ore) - Actualizare rapidă (la 3 ore) • 6.5 km
    // 2. Google WeatherNext 3 (Global AI) - AI Satelitar (Actualizat orar) • 5 km / 10 km
    // 3. ICON-EU Standard (5 zile / 120 ore) - Regional European • 6.5 km
    // 4. ECMWF IFS HRES (15 zile / 360 ore) - Global de referință • 9 km
    // -------------------------------------------------------------------------
    ICON_EU_FLASH(
        id = "icon_eu_flash",
        apiKey = "icon_eu",
        displayName = "ICON-EU Flash (30 ore)",
        shortName = "ICON Flash (30h)",
        badgeText = "6.5 km",
        subtitleTagline = "Actualizare rapidă (la 3 ore) • 6.5 km",
        agency = "DWD (Germania Rapid)",
        resolution = "6.5 km / Rapid Update",
        architectureType = "Rapid Update Cycle (la fiecare 3 ore)",
        updateCycle = "La fiecare 3h (0 – 30 ore)",
        accentColor = Color(0xFFEF4444),
        isRapidUpdate = true,
        categories = listOf(ForecastCategory.FORECAST)
    ),
    WEATHERNEXT_3(
        id = "weathernext_3",
        apiKey = "google_weathernext2_ensemble",
        displayName = "Google WeatherNext 3 (Global AI)",
        shortName = "WeatherNext 3",
        badgeText = "5/10 km",
        subtitleTagline = "AI Satelitar (Actualizat orar) • 5 km / 10 km",
        agency = "Google DeepMind",
        resolution = "5 km (Temp/Umiditate) • 10 km (Vânt/Precip)",
        architectureType = "Global AI Deep Ensemble (64 membri) & Satelit Geostaționar",
        updateCycle = "Actualizat orar (0 – 48h) / Cicluri principale (până la 15 zile)",
        accentColor = Color(0xFF4285F4),
        isAi = true,
        categories = listOf(ForecastCategory.FORECAST)
    ),
    ICON_EU(
        id = "icon_eu",
        apiKey = "icon_eu",
        displayName = "ICON-EU Standard (5 zile / 120 ore)",
        shortName = "ICON-EU (5z)",
        badgeText = "6.5 km",
        subtitleTagline = "Regional European • 6.5 km",
        agency = "DWD (Europa)",
        resolution = "6.5 km Regional",
        architectureType = "Nonhydrostatic Triangular Mesh (120h)",
        updateCycle = "La fiecare 6h (0 – 5 zile)",
        accentColor = Color(0xFF10B981),
        isAi = false,
        categories = listOf(ForecastCategory.FORECAST)
    ),
    ECMWF_IFS(
        id = "ecmwf_ifs",
        apiKey = "ecmwf_ifs025",
        displayName = "ECMWF IFS HRES (15 zile / 360 ore)",
        shortName = "ECMWF IFS HRES",
        badgeText = "9 km",
        subtitleTagline = "Global de referință • 9 km",
        agency = "ECMWF (Europa)",
        resolution = "9 km (0.1° / 0.25° HRES)",
        architectureType = "Hydrostatic / Spectral Benchmark (360h)",
        updateCycle = "00z, 06z, 12z, 18z (0 – 15 zile)",
        accentColor = Color(0xFF38BDF8),
        isAi = false,
        categories = listOf(ForecastCategory.FORECAST)
    ),

    // -------------------------------------------------------------------------
    // Categoria 2: Termen Lung (15 – 46 Zile)
    // Modele exclusive solicitate:
    // 1. ECMWF AIFS (15 zile)
    // 2. ECMWF ENS (15 – 46 zile)
    // 3. GEFS Ensemble (16 – 35 zile)
    // 4. ECMWF SEAS5 (46 zile)
    // 5. NOAA CFSv2 (46 zile separat)
    // -------------------------------------------------------------------------
    ECMWF_AIFS(
        id = "ecmwf_aifs",
        apiKey = "ecmwf_aifs025_single",
        displayName = "ECMWF AIFS (15 zile)",
        shortName = "AIFS AI (15z)",
        badgeText = "25 km",
        subtitleTagline = "AI Sinoptic Global • 25 km",
        agency = "ECMWF (European AI Centre)",
        resolution = "25 km (0.25° Deep Learning)",
        architectureType = "Artificial Intelligence Forecasting System (ERA5)",
        updateCycle = "00z, 12z (0 – 15 zile)",
        accentColor = Color(0xFFA855F7),
        isAi = true,
        isLongRange = true,
        categories = listOf(ForecastCategory.LONG_TERM)
    ),
    ECMWF_EXTENDED(
        id = "ecmwf_extended",
        apiKey = "ecmwf_ec46",
        displayName = "ECMWF ENS (15 – 46 zile)",
        shortName = "ECMWF ENS (46z)",
        badgeText = "46z",
        subtitleTagline = "Ansamblu Sub-Sezonier (51 membri) • 36 km",
        agency = "ECMWF (Sub-Seasonal)",
        resolution = "36 km / 51 Membri ENS",
        architectureType = "Coupled Ocean-Atmosphere Sub-Seasonal Ensemble (EC46)",
        updateCycle = "Zilnic la 20:30 UTC (15 – 46 zile)",
        accentColor = Color(0xFF8B5CF6),
        isLongRange = true,
        categories = listOf(ForecastCategory.LONG_TERM)
    ),
    GEFS(
        id = "gefs",
        apiKey = "gfs_seamless",
        displayName = "GEFS Ensemble (16 – 35 zile)",
        shortName = "GEFS ENS (35z)",
        badgeText = "35z",
        subtitleTagline = "Ansamblu Global NOAA (31 membri) • 35z",
        agency = "NOAA / NCEP (SUA)",
        resolution = "31 Membri ENS Sub-Seasonal",
        architectureType = "Global Ensemble Forecast System Sub-Seasonal",
        updateCycle = "La fiecare 6h (16 – 35 zile)",
        accentColor = Color(0xFF06B6D4),
        isLongRange = true,
        categories = listOf(ForecastCategory.LONG_TERM)
    ),
    SEAS5(
        id = "seas5",
        apiKey = "ecmwf_seas5",
        displayName = "ECMWF SEAS5 Climatologie (46 zile)",
        shortName = "SEAS5 (46z)",
        badgeText = "46z",
        subtitleTagline = "Tendință Sezonieră ECMWF • 46z",
        agency = "ECMWF (Europa)",
        resolution = "36 km / 51 Membri ENS Sezonier",
        architectureType = "Coupled Ocean-Atmosphere Long-Range Seasonal Forecast System",
        updateCycle = "Actualizare lunară / decadală (46 zile)",
        accentColor = Color(0xFFEAB308),
        isLongRange = true,
        categories = listOf(ForecastCategory.LONG_TERM)
    ),
    CFSV2(
        id = "cfsv2",
        apiKey = "cfs_seamless",
        displayName = "NOAA CFSv2 Climatologie (46 zile)",
        shortName = "CFSv2 (46z)",
        badgeText = "46z",
        subtitleTagline = "Tendință Climatică NOAA NCEP • 46z",
        agency = "NOAA / NCEP (SUA)",
        resolution = "Coupled Forecast System v2",
        architectureType = "Coupled Atmosphere-Ocean Climate Model (CFS v2)",
        updateCycle = "Rulări zilnice pe 46 de zile",
        accentColor = Color(0xFFF97316),
        isLongRange = true,
        categories = listOf(ForecastCategory.LONG_TERM)
    ),

    // Modele secundare opționale de referință
    GFS(
        id = "gfs",
        apiKey = "gfs_global",
        displayName = "GFS 0.25° (16 zile)",
        shortName = "GFS 0.25° (16z)",
        badgeText = "0.25°",
        subtitleTagline = "Model Global NOAA • 28 km",
        agency = "NOAA (SUA)",
        resolution = "28 km (0.25° FV3)",
        architectureType = "Finite-Volume Cubed-Sphere (FV3) Core (384h)",
        updateCycle = "00z, 06z, 12z, 18z (0 – 16 zile)",
        accentColor = Color(0xFF3B82F6),
        isAi = false,
        isLongRange = true,
        categories = emptyList() // Exclus din Termen Lung conform cererii
    ),
    ARPEGE(
        id = "arpege",
        apiKey = "meteofrance_arpege_europe",
        displayName = "ARPEGE Europe",
        shortName = "ARPEGE",
        badgeText = "10 km",
        subtitleTagline = "Regional Météo-France • 10 km",
        agency = "Météo-France",
        resolution = "10 km (0.1° Europe)",
        architectureType = "Spectral Stretched Grid",
        updateCycle = "00z, 06z, 12z, 18z",
        accentColor = Color(0xFF06B6D4),
        isAi = false,
        categories = emptyList()
    ),
    ICON(
        id = "icon",
        apiKey = "icon_global",
        displayName = "ICON Global",
        shortName = "ICON Global",
        badgeText = "13 km",
        subtitleTagline = "Model Global DWD • 13 km",
        agency = "DWD (Germania)",
        resolution = "13 km Global",
        architectureType = "Icosahedral Nonhydrostatic",
        updateCycle = "La fiecare 6h",
        accentColor = Color(0xFFF59E0B),
        isAi = false,
        categories = emptyList()
    );

    fun getDescription(lang: AppLanguage): String = when (this) {
        ICON_EU_FLASH -> when (lang) {
            AppLanguage.ENGLISH -> "The freshest physical model available for Romania from the German Weather Service (DWD). Updated rapidly every 3 hours with a 30-hour forecast horizon at 6.5 km resolution, providing the lowest processing latency for same-day and next-day real-time weather before heading out."
            AppLanguage.ROMANIAN -> "Cel mai proaspăt model fizic disponibil pentru România, dezvoltat de Serviciul Meteorologic German (DWD). Actualizat rapid la fiecare 3 ore cu un orizont de 30 de ore la o rezoluție de 6.5 km, oferă cel mai mic decalaj temporal de procesare pentru ziua în curs și următoarele 24–30 de ore, ideal înainte de a ieși din casă."
            AppLanguage.HUNGARIAN -> "A legfrissebb fizikai modell Románia számára a Német Meteorológiai Szolgálattól (DWD). 3 óránkénti gyorsfrissítéssel és 30 órás időtávval (6.5 km) minimális késleltetéssel nyújt előrejelzést."
        }
        WEATHERNEXT_3 -> when (lang) {
            AppLanguage.ENGLISH -> "Google DeepMind's unified global AI forecasting system WeatherNext 3, running a 64-member deep ensemble. Directly assimilates geostationary satellite imagery (no terrestrial Doppler radar assimilation). Employs a 5 km station-head grid strictly for 2m temperature and dew point / relative humidity downscaled for real terrain elevation and valleys/mountains. Gridded precipitation (rain/snow), 10m wind, cloud cover, and solar radiation are computed on the 10 km grid, while 25 km represents upper-air atmospheric variables. Delivers hourly rolling nowcasts (0–48h) and main synoptic runs out to 15 days (360 hours)."
            AppLanguage.ROMANIAN -> "Sistemul global unificat de inteligență artificială Google DeepMind WeatherNext 3, bazat pe un ansamblu de 64 de membri. Asimilează direct imagini de la sateliți geostaționari (nu folosește radare Doppler terestre). Grila de 5 km este optimizată prin modulul station-head exclusiv pentru temperatura la 2 m și punctul de rouă / umiditate în funcție de altitudinea reală și micro-orografie (văi, dealuri, munți). Precipitațiile (ploaie, ninsoare), vântul la 10 m, gradul de nori și radiația solară sunt calculate pe grila de 10 km, iar 25 km reprezintă straturile înalte ale atmosferei. Oferă actualizări orare rapide (nowcasting 0–48h) și prognoze sinoptice pe 15 zile (360 ore) la ciclurile principale."
            AppLanguage.HUNGARIAN -> "A Google DeepMind egységes globális WeatherNext 3 mesterséges intelligencia rendszere 64 tagú együttessel. Közvetlenül geostacionárius műholdképeket asszimilál (földi Doppler radar nélkül). Az 5 km-es rácsot kizárólag a 2 m-es hőmérsékletre és páratartalomra használja a domborzat és tengerszint feletti magasság alapján. A csapadék, szél és felhőzet 10 km-es rácson érkezik. Óránkénti 48 órás nowcastingot és 15 napos szinoptikus előrejelzést nyújt."
        }
        ICON_EU -> when (lang) {
            AppLanguage.ENGLISH -> "The fundamental benchmark for 2–5 day (120 hours) weather in Romania at 6.5 km resolution. Faithfully resolves Carpathian orography, valley wind channels, and Transylvanian basin temperatures for weekday and weekend trip planning."
            AppLanguage.ROMANIAN -> "Standardul de bază pentru prognoza pe 2–5 zile (120 ore) în România la o rezoluție de 6.5 km. Rezolvă fidel orografia Carpaților, vânturile din văi și depresiunile Transilvaniei pentru planificarea din timpul săptămânii sau a weekendului."
            AppLanguage.HUNGARIAN -> "Az alapvető etalon a 2–5 napos (120 óra) előrejelzéshez Romániában 6.5 km-es felbontással. Kiválóan modellezi a Kárpátok domborzatát és a völgyek szélviszonyait."
        }
        ECMWF_IFS -> when (lang) {
            AppLanguage.ENGLISH -> "The worldwide gold standard for medium-range synoptic atmospheric physics up to 15 days (360 hours) at 9 km resolution. Ideal for spotting heatwaves, strong cold fronts, or major large-scale weather pattern shifts heading into week 2."
            AppLanguage.ROMANIAN -> "Etalonul mondial pentru prognoza sinoptică pe termen mediu și lung (15 zile / 360 ore) la o rezoluție de 9 km. Utilizatorul îl accesează atunci când dorește să identifice valuri de căldură, fronturi reci sau schimbări majore de tipar atmosferic în a doua săptămână."
            AppLanguage.HUNGARIAN -> "A nemzetközi meteorológia etalonja középtávú szinoptikus előrejelzésre akár 15 napig (360 óra), 9 km-es felbontással. Kiváló hőhullámok, hidegfrontok és időjárási mintázatok követésére."
        }
        ECMWF_AIFS -> when (lang) {
            AppLanguage.ENGLISH -> "ECMWF's revolutionary data-driven Artificial Intelligence Forecasting System (0.25° grid) out to 15 days, trained on decades of ERA5 climate reanalysis."
            AppLanguage.ROMANIAN -> "Sistemul inovator de prognoză bazat pe inteligență artificială al ECMWF (grilă 0.25°) până la 15 zile, antrenat pe decenii de reanaliză climatică globală ERA5."
            AppLanguage.HUNGARIAN -> "Az ECMWF forradalmi mesterséges intelligencia előrejelző rendszere 15 napos időtávval, ERA5 adatokra építve."
        }
        ECMWF_EXTENDED -> when (lang) {
            AppLanguage.ENGLISH -> "ECMWF's world-leading sub-seasonal forecasting system (EC46/SEAS5) with 51 ensemble members predicting heatwaves, cold air outbreaks, and precipitation anomalies from 15 up to 46 days."
            AppLanguage.ROMANIAN -> "Sistemul sub-sezonier de vârf mondial al ECMWF (EC46) cu 51 de membri de ansamblu, ce prognozează valuri de căldură, intruziuni reci și anomalii de precipitații între 15 și 46 de zile."
            AppLanguage.HUNGARIAN -> "Az ECMWF világvezető szub-szezonális rendszere (EC46) 51 tagú együttessel, 15-től akár 46 napig."
        }
        GFS -> when (lang) {
            AppLanguage.ENGLISH -> "NOAA's operational flagship global model (0.25° FV3 dynamical core) providing a full 16-day synoptic forecast horizon (384 hours)."
            AppLanguage.ROMANIAN -> "Modelul global de bază al agenției americane NOAA (nucleu dinamic FV3 0.25°) cu orizont complet de 16 zile (384 ore)."
            AppLanguage.HUNGARIAN -> "Az amerikai NOAA operatív globális modellje FV3 dinamikus maggal, 16 napos időtávval (384 óra)."
        }
        GEFS -> when (lang) {
            AppLanguage.ENGLISH -> "NOAA's Global Ensemble Forecast System running with 31 ensemble members extending out to 35 days for sub-seasonal spread and atmospheric regime analysis."
            AppLanguage.ROMANIAN -> "Sistemul de prognoză de ansamblu al NOAA (GEFS) cu 31 de membri, extins până la 35 de zile pentru analiza probabilităților și a regimurilor de circulație atmosferică."
            AppLanguage.HUNGARIAN -> "A NOAA globális együttes előrejelző rendszere (GEFS) 31 taggal, akár 35 napos időtávval."
        }
        SEAS5 -> when (lang) {
            AppLanguage.ENGLISH -> "ECMWF SEAS5 long-range seasonal forecast system with 51 ensemble members on a 36 km grid, providing 46-day climate trends and circulation anomalies."
            AppLanguage.ROMANIAN -> "Sistemul avansat de prognoză sezonieră ECMWF SEAS5 cu 51 de membri de ansamblu pe grilă de 36 km, oferind tendințe climatice și anomalii pe 46 de zile."
            AppLanguage.HUNGARIAN -> "Az ECMWF SEAS5 szezonális előrejelző rendszere 51 tagú együttessel, 46 napos éghajlati trendekkel."
        }
        CFSV2 -> when (lang) {
            AppLanguage.ENGLISH -> "NOAA CFSv2 (Climate Forecast System Version 2) coupled ocean-atmosphere model providing 46-day sub-seasonal signals and precipitation anomalies."
            AppLanguage.ROMANIAN -> "Modelul climatic cuplat atmosferă-ocean NOAA CFSv2 (Climate Forecast System Version 2) cu rulări extinse pe 46 de zile pentru anomalii și regimuri globale."
            AppLanguage.HUNGARIAN -> "A NOAA CFSv2 kapcsolt óceán-légkör éghajlati modellje 46 napos szub-szezonális jelzésekkel."
        }
        ARPEGE -> when (lang) {
            AppLanguage.ENGLISH -> "Météo-France's stretched-grid spectral model providing 0.1° resolution across Europe."
            AppLanguage.ROMANIAN -> "Modelul spectral cu grilă variabilă al Météo-France (rezoluție 0.1° în Europa)."
            AppLanguage.HUNGARIAN -> "A Météo-France változó rácsú modellje (0.1° Európában)."
        }
        ICON -> when (lang) {
            AppLanguage.ENGLISH -> "German Weather Service (DWD) global icosahedral nonhydrostatic model."
            AppLanguage.ROMANIAN -> "Modelul global nehidrostatic al Serviciului Meteorologic German (DWD)."
            AppLanguage.HUNGARIAN -> "A DWD globális nemhidrosztatikus modellje."
        }
    }

    companion object {
        // Compatibility aliases for previous references
        val WEATHERNEXT_3_5KM get() = WEATHERNEXT_3
        val WEATHERNEXT_3_10KM get() = WEATHERNEXT_3
        val WEATHERNEXT_3_25KM get() = WEATHERNEXT_3
        val WEATHERNEXT_3_SHORT get() = WEATHERNEXT_3
        val WEATHERNEXT_3_MEDIUM get() = WEATHERNEXT_3

        fun fromId(id: String): WeatherModel =
            when (id) {
                "weathernext_3",
                "weathernext_3_5km",
                "weathernext_3_10km",
                "weathernext_3_25km",
                "weathernext_3_short",
                "weathernext_3_medium" -> WEATHERNEXT_3
                else -> entries.find { it.id == id } ?: ICON_EU_FLASH
            }

        fun forCategory(category: ForecastCategory): List<WeatherModel> =
            entries.filter { it.categories.contains(category) }
    }
}
