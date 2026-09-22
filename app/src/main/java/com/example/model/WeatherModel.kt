package com.example.model

import androidx.compose.ui.graphics.Color

enum class ForecastCategory(
    val id: String,
    val titleRes: String,
    val subtitle: String,
    val horizonDays: Int
) {
    SHORT_TERM(
        id = "short_term",
        titleRes = "category_short_term",
        subtitle = "0 – 72 Hours (High-Frequency / Convection)",
        horizonDays = 3
    ),
    MEDIUM_TERM(
        id = "medium_term",
        titleRes = "category_medium_term",
        subtitle = "3 – 15 Days (Synoptic Evolution)",
        horizonDays = 15
    ),
    LONG_TERM(
        id = "long_term",
        titleRes = "category_long_term",
        subtitle = "15 – 46 Days Sub-Seasonal Climate Trends",
        horizonDays = 46
    )
}

enum class WeatherModel(
    val id: String,
    val apiKey: String,
    val displayName: String,
    val shortName: String,
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
    // Short-term & Medium-term Google WeatherNext 3
    WEATHERNEXT_3_SHORT(
        id = "weathernext_3_short",
        apiKey = "google_weathernext2_ensemble",
        displayName = "Google WeatherNext 3",
        shortName = "WeatherNext 3 [Short]",
        agency = "Google DeepMind / Research",
        resolution = "0.25° (28 km) / 64 ENS",
        architectureType = "Graph Neural Network & Diffusion Generative",
        updateCycle = "00z, 12z (Rapid 6-hour roll)",
        accentColor = Color(0xFF4285F4),
        isAi = true,
        categories = listOf(ForecastCategory.SHORT_TERM)
    ),
    WEATHERNEXT_3_MEDIUM(
        id = "weathernext_3_medium",
        apiKey = "google_weathernext2_ensemble",
        displayName = "Google WeatherNext 3",
        shortName = "WeatherNext 3 [Medium]",
        agency = "Google DeepMind / Research",
        resolution = "0.25° (28 km) / 64 ENS",
        architectureType = "Deep Ensemble GNN Atmospheric Predictor",
        updateCycle = "00z, 12z (15-Day Horizon)",
        accentColor = Color(0xFF1A73E8),
        isAi = true,
        categories = listOf(ForecastCategory.MEDIUM_TERM)
    ),

    // Short-Term Models
    ICON_EU_FLASH(
        id = "icon_eu_flash",
        apiKey = "icon_eu",
        displayName = "ICON-EU Flash",
        shortName = "ICON Flash",
        agency = "DWD (Europe Rapid)",
        resolution = "6.5 km / Rapid",
        architectureType = "Rapid Convection-Permitting",
        updateCycle = "Every 3h (Fast Cycle)",
        accentColor = Color(0xFFEF4444),
        isRapidUpdate = true,
        categories = listOf(ForecastCategory.SHORT_TERM)
    ),
    ICON_EU(
        id = "icon_eu",
        apiKey = "icon_eu",
        displayName = "ICON-EU",
        shortName = "ICON-EU",
        agency = "DWD (Europe)",
        resolution = "6.5 km Regional",
        architectureType = "Nonhydrostatic Triangular Mesh",
        updateCycle = "Every 6h",
        accentColor = Color(0xFF10B981),
        isAi = false,
        categories = listOf(ForecastCategory.SHORT_TERM, ForecastCategory.MEDIUM_TERM)
    ),
    ARPEGE(
        id = "arpege",
        apiKey = "meteofrance_arpege_europe",
        displayName = "ARPEGE Europe",
        shortName = "ARPEGE Europe",
        agency = "Météo-France",
        resolution = "10 km (0.1° Europe)",
        architectureType = "Spectral Stretched Grid",
        updateCycle = "00z, 06z, 12z, 18z",
        accentColor = Color(0xFF06B6D4),
        isAi = false,
        categories = listOf(ForecastCategory.SHORT_TERM, ForecastCategory.MEDIUM_TERM)
    ),

    // Medium-Term Models
    ECMWF_IFS(
        id = "ecmwf_ifs",
        apiKey = "ecmwf_ifs025",
        displayName = "ECMWF IFS HRES",
        shortName = "IFS HRES",
        agency = "ECMWF (Europe)",
        resolution = "9 km (0.25°)",
        architectureType = "Hydrostatic / Spectral Dynamical Core",
        updateCycle = "00z, 06z, 12z, 18z",
        accentColor = Color(0xFF38BDF8),
        isAi = false,
        categories = listOf(ForecastCategory.MEDIUM_TERM)
    ),
    ECMWF_AIFS(
        id = "ecmwf_aifs",
        apiKey = "ecmwf_aifs025",
        displayName = "ECMWF AIFS (0.25)",
        shortName = "AIFS (0.25)",
        agency = "ECMWF (Europe AI)",
        resolution = "25 km (0.25°)",
        architectureType = "Deep Learning / Graph Neural Net",
        updateCycle = "00z, 06z, 12z, 18z",
        accentColor = Color(0xFFA855F7),
        isAi = true,
        categories = listOf(ForecastCategory.MEDIUM_TERM)
    ),
    ICON(
        id = "icon",
        apiKey = "icon_global",
        displayName = "ICON Global",
        shortName = "ICON Global",
        agency = "DWD (Germany)",
        resolution = "13 km Global",
        architectureType = "Icosahedral Nonhydrostatic",
        updateCycle = "Every 6h (00z, 06z, 12z, 18z)",
        accentColor = Color(0xFFF59E0B),
        isAi = false,
        categories = listOf(ForecastCategory.MEDIUM_TERM)
    ),
    GFS(
        id = "gfs",
        apiKey = "gfs_global",
        displayName = "GFS Global",
        shortName = "GFS Global",
        agency = "NOAA (USA)",
        resolution = "28 km (0.25°)",
        architectureType = "Finite-Volume Cubed-Sphere (FV3)",
        updateCycle = "Every 6h",
        accentColor = Color(0xFF3B82F6),
        isAi = false,
        categories = listOf(ForecastCategory.MEDIUM_TERM)
    ),

    // Long-Term (Sub-Seasonal)
    ECMWF_EXTENDED(
        id = "ecmwf_extended",
        apiKey = "ecmwf_seamless",
        displayName = "ECMWF Extended-Range [46-Day ENS]",
        shortName = "ECMWF 46-Day ENS",
        agency = "ECMWF (Europe Sub-Seasonal)",
        resolution = "36 km / 51 ENS Members",
        architectureType = "Coupled Ocean-Atmosphere Sub-Seasonal Ensemble (EC46/SEAS5)",
        updateCycle = "Daily at 20:30 UTC",
        accentColor = Color(0xFF8B5CF6),
        isLongRange = true,
        categories = listOf(ForecastCategory.LONG_TERM)
    );

    fun getDescription(lang: AppLanguage): String = when (this) {
        WEATHERNEXT_3_SHORT -> when (lang) {
            AppLanguage.ENGLISH -> "Google DeepMind's flagship WeatherNext 3 generative AI model for short-range horizons (0–72h). Combines spherical Graph Neural Networks with high-resolution radar assimilation for storm tracking."
            AppLanguage.ROMANIAN -> "Modelul de inteligență artificială generativă WeatherNext 3 al Google DeepMind pentru orizont pe termen scurt (0–72h). Combină rețele neuronale grafice cu asimilare radar pentru urmărirea furtunilor."
            AppLanguage.HUNGARIAN -> "A Google DeepMind WeatherNext 3 generatív mesterséges intelligencia modellje rövid távra (0–72 óra). Gömbölyű gróf neurális hálózatokat ötvöz nagy felbontású radardatokkal viharkövetéshez."
        }
        WEATHERNEXT_3_MEDIUM -> when (lang) {
            AppLanguage.ENGLISH -> "Google DeepMind's WeatherNext 3 ensemble for medium-range horizons (3–15 days). Uses 64 AI ensemble members on a 0.25° grid to outperform classical physics baselines across synoptic tracks."
            AppLanguage.ROMANIAN -> "Ansamblul WeatherNext 3 al Google DeepMind pentru orizont pe termen mediu (3–15 zile). Utilizează 64 de membri de ansamblu AI pe o grilă de 0.25° pentru precizie sinoptică superioară."
            AppLanguage.HUNGARIAN -> "A Google DeepMind WeatherNext 3 modellje középtávú időtávra (3–15 nap). 64 tagú mesterséges intelligencia együttest használ 0.25°-os felbontásban a fizikai modellek pontosságát felülmúlva."
        }
        ECMWF_EXTENDED -> when (lang) {
            AppLanguage.ENGLISH -> "ECMWF's world-leading sub-seasonal forecasting system (EC46/SEAS5) predicting atmospheric teleconnections, heatwaves, cold outbreaks, and precipitation anomalies up to 46 days ahead with 51 ensemble members."
            AppLanguage.ROMANIAN -> "Sistemul sub-sezonier de vârf al ECMWF (EC46/SEAS5) ce prezice teleconexiuni atmosferice, valuri de căldură, intruziuni polare și anomalii de precipitații până la 46 de zile înainte cu 51 de membri de ansamblu."
            AppLanguage.HUNGARIAN -> "Az ECMWF világvezető szub-szezonális előrejelző rendszere (EC46/SEAS5), amely 51 tagú együttessel jelzi előre a légköri telekonnexiókat, hőhullámokat és csapadékanomáliákat akár 46 napra előre."
        }
        ECMWF_IFS -> when (lang) {
            AppLanguage.ENGLISH -> "The world-renowned European High-Resolution (HRES) deterministic model, celebrated as the benchmark for medium-range atmospheric physics."
            AppLanguage.ROMANIAN -> "Modelul european determinist de înaltă rezoluție (HRES), considerat standardul de aur mondial pentru fizica atmosferei pe termen mediu."
            AppLanguage.HUNGARIAN -> "A világhírű európai nagy felbontású (HRES) modell, a középtávú légköri fizika nemzetközi etalonja."
        }
        ECMWF_AIFS -> when (lang) {
            AppLanguage.ENGLISH -> "ECMWF's revolutionary data-driven Artificial Intelligence Forecasting System, running 0.25° neural networks trained on decades of ERA5 climate reanalysis."
            AppLanguage.ROMANIAN -> "Sistemul revoluționar de prognoză bazat pe inteligență artificială al ECMWF, ce rulează rețele neuronale la 0.25° antrenate pe decenii de date climatice ERA5."
            AppLanguage.HUNGARIAN -> "Az ECMWF forradalmi mesterséges intelligencia előrejelző rendszere, 0.25°-os neurális hálózattal, több évtizednyi ERA5 éghajlati adatra támaszkodva."
        }
        ICON -> when (lang) {
            AppLanguage.ENGLISH -> "German Weather Service (DWD) global nonhydrostatic model utilizing an icosahedral triangular grid for robust synoptic wave propagation."
            AppLanguage.ROMANIAN -> "Modelul global nehidrostatic al Serviciului Meteorologic German (DWD), utilizând o rețea triunghiulară icosaedrică pentru simularea dinamicii sinoptice."
            AppLanguage.HUNGARIAN -> "A Német Meteorológiai Szolgálat (DWD) globális nemhidrosztatikus modellje, ikozaéderes háromszög ráccsal a légköri hullámok pontos szimulálásához."
        }
        ICON_EU -> when (lang) {
            AppLanguage.ENGLISH -> "High-resolution European domain nesting of ICON (6.5 km), delivering fine-scale topography resolution across the Carpathians, Alps, and plains."
            AppLanguage.ROMANIAN -> "Versiunea regională europeană de înaltă rezoluție a modelului ICON (6.5 km), cu reproducere excelentă a reliefului Carpaților, Alpilor și câmpiilor."
            AppLanguage.HUNGARIAN -> "Az ICON nagy felbontású európai változata (6.5 km), amely kiválóan modellezi a Kárpátok, Alpok és az alföldek domborzati hatásait."
        }
        ICON_EU_FLASH -> when (lang) {
            AppLanguage.ENGLISH -> "High-frequency rapid update cycle updated every 3 hours with 30-hour forecast horizon, specifically tuned for sudden convective storms and flash fronts."
            AppLanguage.ROMANIAN -> "Ciclu de actualizare rapidă la fiecare 3 ore cu orizont de prognoză de 30 de ore, calibrat special pentru furtuni convective bruște și fronturi rapide."
            AppLanguage.HUNGARIAN -> "Gyakori, 3 óránkénti gyors frissítési ciklus 30 órás időtávval, kifejezetten a hirtelen kialakuló zivatarok és gyors hidegfrontok előrejelzésére."
        }
        ARPEGE -> when (lang) {
            AppLanguage.ENGLISH -> "Météo-France's stretched-grid spectral model providing 0.1° resolution across Europe with superior Mediterranean cyclogenesis modeling."
            AppLanguage.ROMANIAN -> "Modelul spectral cu grilă variabilă al Météo-France, oferind o rezoluție de 0.1° în Europa, remarcabil pentru ciclogeneza mediteraneană și fronturi atlantice."
            AppLanguage.HUNGARIAN -> "A Météo-France változó rácsú spektrális modellje, 0.1°-os európai felbontással, kiemelkedő mediterrán ciklon- és csapadék-előrejelzéssel."
        }
        GFS -> when (lang) {
            AppLanguage.ENGLISH -> "NOAA's operational flagship global model featuring the Finite-Volume Cubed-Sphere dynamical core for reliable planetary-scale tracking."
            AppLanguage.ROMANIAN -> "Modelul global de bază al agenției americane NOAA, bazat pe nucleul dinamic FV3 pentru urmărirea sistemelor meteorologice planetare."
            AppLanguage.HUNGARIAN -> "Az amerikai NOAA operatív globális modellje FV3 dinamikus maggal, megbízható globális időjárási rendszerek követésére."
        }
    }

    companion object {
        fun fromId(id: String): WeatherModel =
            entries.find { it.id == id } ?: ECMWF_IFS

        fun forCategory(category: ForecastCategory): List<WeatherModel> =
            entries.filter { it.categories.contains(category) }
    }
}
