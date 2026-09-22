package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val shortCode: String) {
    ENGLISH("en", "English", "EN"),
    ROMANIAN("ro", "Română", "RO"),
    HUNGARIAN("hu", "Magyar", "HU")
}

object Translations {
    fun get(key: String, lang: AppLanguage): String {
        return strings[key]?.get(lang) ?: key
    }

    private val strings: Map<String, Map<AppLanguage, String>> = mapOf(
        "category_short_term" to mapOf(
            AppLanguage.ENGLISH to "Short-Term Forecast",
            AppLanguage.ROMANIAN to "Prognoză Termen Scurt",
            AppLanguage.HUNGARIAN to "Rövid távú előrejelzés"
        ),
        "category_medium_term" to mapOf(
            AppLanguage.ENGLISH to "Medium-Term Forecast",
            AppLanguage.ROMANIAN to "Prognoză Termen Mediu",
            AppLanguage.HUNGARIAN to "Középtávú előrejelzés"
        ),
        "category_long_term" to mapOf(
            AppLanguage.ENGLISH to "Long-Term Forecast",
            AppLanguage.ROMANIAN to "Prognoză Termen Lung",
            AppLanguage.HUNGARIAN to "Hosszú távú előrejelzés"
        ),
        "category_short_subtitle" to mapOf(
            AppLanguage.ENGLISH to "0 – 72 Hours (High-Frequency & Rapid Update)",
            AppLanguage.ROMANIAN to "0 – 72 Ore (Frecvență înaltă & actualizări rapide)",
            AppLanguage.HUNGARIAN to "0 – 72 óra (Nagy felbontás és gyorsfrissítés)"
        ),
        "category_medium_subtitle" to mapOf(
            AppLanguage.ENGLISH to "3 – 15 Days (Synoptic Atmospheric Evolution)",
            AppLanguage.ROMANIAN to "3 – 15 Zile (Evoluție atmosferică sinoptică)",
            AppLanguage.HUNGARIAN to "3 – 15 nap (Szinoptikus légköri fejlődés)"
        ),
        "category_long_subtitle" to mapOf(
            AppLanguage.ENGLISH to "15 – 46 Days (Sub-Seasonal Climate Anomalies)",
            AppLanguage.ROMANIAN to "15 – 46 Zile (Anomalii climatice sub-sezoniere)",
            AppLanguage.HUNGARIAN to "15 – 46 nap (Szub-szezonális éghajlati trendek)"
        ),
        "long_term_forecast_title" to mapOf(
            AppLanguage.ENGLISH to "ECMWF Sub-Seasonal Trend (46-Day ENS)",
            AppLanguage.ROMANIAN to "Tendință Sub-Sezonieră ECMWF (46 Zile ENS)",
            AppLanguage.HUNGARIAN to "ECMWF Szub-szezonális trend (46 napos ENS)"
        ),
        "weekly_overview" to mapOf(
            AppLanguage.ENGLISH to "Weekly Breakdown",
            AppLanguage.ROMANIAN to "Defalcare săptămânală",
            AppLanguage.HUNGARIAN to "Heti bontás"
        ),
        "app_title" to mapOf(
            AppLanguage.ENGLISH to "Meteo Models",
            AppLanguage.ROMANIAN to "Meteo Models",
            AppLanguage.HUNGARIAN to "Meteo Models"
        ),
        "feels_like" to mapOf(
            AppLanguage.ENGLISH to "Feels like",
            AppLanguage.ROMANIAN to "Se simte ca",
            AppLanguage.HUNGARIAN to "Hőérzet"
        ),
        "hourly_forecast" to mapOf(
            AppLanguage.ENGLISH to "Hourly Forecast",
            AppLanguage.ROMANIAN to "Prognoză orară",
            AppLanguage.HUNGARIAN to "Óránkénti előrejelzés"
        ),
        "daily_forecast" to mapOf(
            AppLanguage.ENGLISH to "7-Day Forecast",
            AppLanguage.ROMANIAN to "Prognoză 7 zile",
            AppLanguage.HUNGARIAN to "7 napos előrejelzés"
        ),
        "model_comparison" to mapOf(
            AppLanguage.ENGLISH to "Multi-Model Comparison",
            AppLanguage.ROMANIAN to "Comparație Multi-Model",
            AppLanguage.HUNGARIAN to "Többmodell-összehasonlítás"
        ),
        "model_consensus" to mapOf(
            AppLanguage.ENGLISH to "Model Consensus",
            AppLanguage.ROMANIAN to "Consens Modele",
            AppLanguage.HUNGARIAN to "Modellkonszenzus"
        ),
        "model_spread" to mapOf(
            AppLanguage.ENGLISH to "Spread: ±%.1f°C",
            AppLanguage.ROMANIAN to "Variație: ±%.1f°C",
            AppLanguage.HUNGARIAN to "Szórás: ±%.1f°C"
        ),
        "high_agreement" to mapOf(
            AppLanguage.ENGLISH to "High Model Agreement",
            AppLanguage.ROMANIAN to "Concordanță ridicată",
            AppLanguage.HUNGARIAN to "Nagy modell-egyetértés"
        ),
        "divergence" to mapOf(
            AppLanguage.ENGLISH to "Model Divergence",
            AppLanguage.ROMANIAN to "Divergență între modele",
            AppLanguage.HUNGARIAN to "Modellek eltérése"
        ),
        "humidity" to mapOf(
            AppLanguage.ENGLISH to "Humidity",
            AppLanguage.ROMANIAN to "Umiditate",
            AppLanguage.HUNGARIAN to "Páratartalom"
        ),
        "wind" to mapOf(
            AppLanguage.ENGLISH to "Wind",
            AppLanguage.ROMANIAN to "Vânt",
            AppLanguage.HUNGARIAN to "Szél"
        ),
        "pressure" to mapOf(
            AppLanguage.ENGLISH to "Pressure",
            AppLanguage.ROMANIAN to "Presiune",
            AppLanguage.HUNGARIAN to "Légnyomás"
        ),
        "precipitation" to mapOf(
            AppLanguage.ENGLISH to "Precipitation",
            AppLanguage.ROMANIAN to "Precipitații",
            AppLanguage.HUNGARIAN to "Csapadék"
        ),
        "offline_cached" to mapOf(
            AppLanguage.ENGLISH to "Offline Cache",
            AppLanguage.ROMANIAN to "Date din memorie offline",
            AppLanguage.HUNGARIAN to "Offline gyorsítótár"
        ),
        "live_model" to mapOf(
            AppLanguage.ENGLISH to "Live Model",
            AppLanguage.ROMANIAN to "Model în direct",
            AppLanguage.HUNGARIAN to "Élő modell"
        ),
        "updated" to mapOf(
            AppLanguage.ENGLISH to "Updated",
            AppLanguage.ROMANIAN to "Actualizat",
            AppLanguage.HUNGARIAN to "Frissítve"
        ),
        "just_now" to mapOf(
            AppLanguage.ENGLISH to "Just now",
            AppLanguage.ROMANIAN to "Chiar acum",
            AppLanguage.HUNGARIAN to "Épp most"
        ),
        "min_ago" to mapOf(
            AppLanguage.ENGLISH to "%d min ago",
            AppLanguage.ROMANIAN to "acum %d min",
            AppLanguage.HUNGARIAN to "%d perce"
        ),
        "hours_ago" to mapOf(
            AppLanguage.ENGLISH to "%d h ago",
            AppLanguage.ROMANIAN to "acum %d ore",
            AppLanguage.HUNGARIAN to "%d órája"
        ),
        "search_hint" to mapOf(
            AppLanguage.ENGLISH to "Search city (e.g. Bucharest, Cluj, Budapest)…",
            AppLanguage.ROMANIAN to "Caută oraș (ex. București, Cluj, Budapesta)…",
            AppLanguage.HUNGARIAN to "Város keresése (pl. Budapest, Kolozsvár, Bécs)…"
        ),
        "saved_cities" to mapOf(
            AppLanguage.ENGLISH to "Saved Cities",
            AppLanguage.ROMANIAN to "Orașe salvate",
            AppLanguage.HUNGARIAN to "Mentett városok"
        ),
        "add_favorite" to mapOf(
            AppLanguage.ENGLISH to "Save to favorites",
            AppLanguage.ROMANIAN to "Adaugă la favorite",
            AppLanguage.HUNGARIAN to "Mentés a kedvencekhez"
        ),
        "refresh" to mapOf(
            AppLanguage.ENGLISH to "Refresh",
            AppLanguage.ROMANIAN to "Reîmprospătează",
            AppLanguage.HUNGARIAN to "Frissítés"
        ),
        "model_info" to mapOf(
            AppLanguage.ENGLISH to "Model Specs",
            AppLanguage.ROMANIAN to "Detalii Model",
            AppLanguage.HUNGARIAN to "Modell adatok"
        ),
        "resolution" to mapOf(
            AppLanguage.ENGLISH to "Resolution",
            AppLanguage.ROMANIAN to "Rezoluție",
            AppLanguage.HUNGARIAN to "Felbontás"
        ),
        "agency" to mapOf(
            AppLanguage.ENGLISH to "Provider Agency",
            AppLanguage.ROMANIAN to "Agenție furnizoare",
            AppLanguage.HUNGARIAN to "Szolgáltató ügynökség"
        ),
        "update_freq" to mapOf(
            AppLanguage.ENGLISH to "Update Frequency",
            AppLanguage.ROMANIAN to "Frecvență actualizare",
            AppLanguage.HUNGARIAN to "Frissítési gyakoriság"
        ),
        "architecture" to mapOf(
            AppLanguage.ENGLISH to "Architecture",
            AppLanguage.ROMANIAN to "Arhitectură",
            AppLanguage.HUNGARIAN to "Architektúra"
        ),
        "select_language" to mapOf(
            AppLanguage.ENGLISH to "Select Language",
            AppLanguage.ROMANIAN to "Selectează Limba",
            AppLanguage.HUNGARIAN to "Nyelv kiválasztása"
        ),
        "theme" to mapOf(
            AppLanguage.ENGLISH to "Theme",
            AppLanguage.ROMANIAN to "Temă",
            AppLanguage.HUNGARIAN to "Téma"
        ),
        "theme_light" to mapOf(
            AppLanguage.ENGLISH to "Light",
            AppLanguage.ROMANIAN to "Luminos",
            AppLanguage.HUNGARIAN to "Világos"
        ),
        "theme_dark" to mapOf(
            AppLanguage.ENGLISH to "Dark",
            AppLanguage.ROMANIAN to "Întunecat",
            AppLanguage.HUNGARIAN to "Sötét"
        ),
        "theme_system" to mapOf(
            AppLanguage.ENGLISH to "System",
            AppLanguage.ROMANIAN to "Sistem",
            AppLanguage.HUNGARIAN to "Rendszer"
        ),
        "close" to mapOf(
            AppLanguage.ENGLISH to "Close",
            AppLanguage.ROMANIAN to "Închide",
            AppLanguage.HUNGARIAN to "Bezárás"
        ),
        "retry" to mapOf(
            AppLanguage.ENGLISH to "Retry",
            AppLanguage.ROMANIAN to "Reîncearcă",
            AppLanguage.HUNGARIAN to "Újra"
        ),
        "no_internet_cached" to mapOf(
            AppLanguage.ENGLISH to "Offline mode active. Showing cached forecasts.",
            AppLanguage.ROMANIAN to "Modul offline este activ. Se afișează prognoza salvată.",
            AppLanguage.HUNGARIAN to "Offline mód aktív. Mentett előrejelzés megjelenítése."
        ),
        "compare_all_models" to mapOf(
            AppLanguage.ENGLISH to "Cross-Model Consensus & Divergence",
            AppLanguage.ROMANIAN to "Consens și Divergență între Modele",
            AppLanguage.HUNGARIAN to "Modellek konszenzusa és szórása"
        ),
        "temp_comparison" to mapOf(
            AppLanguage.ENGLISH to "Temperature Comparison (°C)",
            AppLanguage.ROMANIAN to "Comparație Temperatură (°C)",
            AppLanguage.HUNGARIAN to "Hőmérséklet-összehasonlítás (°C)"
        ),
        "precipitation_comparison" to mapOf(
            AppLanguage.ENGLISH to "Precipitation (mm)",
            AppLanguage.ROMANIAN to "Precipitații (mm)",
            AppLanguage.HUNGARIAN to "Csapadék (mm)"
        ),
        "nav_forecasts" to mapOf(
            AppLanguage.ENGLISH to "Forecasts",
            AppLanguage.ROMANIAN to "Prognoze",
            AppLanguage.HUNGARIAN to "Előrejelzések"
        ),
        "nav_anm_warnings" to mapOf(
            AppLanguage.ENGLISH to "ANM Warnings",
            AppLanguage.ROMANIAN to "Avertizări ANM",
            AppLanguage.HUNGARIAN to "ANM Riasztások"
        ),
        "nav_anm_products" to mapOf(
            AppLanguage.ENGLISH to "ANM Products",
            AppLanguage.ROMANIAN to "Produse ANM",
            AppLanguage.HUNGARIAN to "ANM Termékek"
        ),
        "anm_official_source" to mapOf(
            AppLanguage.ENGLISH to "Official data provided by ANM (meteoromania.ro)",
            AppLanguage.ROMANIAN to "Date oficiale furnizate de Administrația Națională de Meteorologie",
            AppLanguage.HUNGARIAN to "Hivatalos adatok az ANM (meteoromania.ro) forrásából"
        ),
        "anm_tab_general" to mapOf(
            AppLanguage.ENGLISH to "General Warnings",
            AppLanguage.ROMANIAN to "Avertizări Generale",
            AppLanguage.HUNGARIAN to "Általános Riasztások"
        ),
        "anm_tab_nowcasting" to mapOf(
            AppLanguage.ENGLISH to "Nowcasting (Immediate)",
            AppLanguage.ROMANIAN to "Nowcasting (Imediat)",
            AppLanguage.HUNGARIAN to "Nowcasting (Azonnali)"
        ),
        "anm_no_warnings" to mapOf(
            AppLanguage.ENGLISH to "No active weather warnings at this moment.",
            AppLanguage.ROMANIAN to "Nu există avertizări meteorologice active în acest moment.",
            AppLanguage.HUNGARIAN to "Jelenleg nincsenek érvényben lévő meteorológiai riasztások."
        ),
        "anm_refresh" to mapOf(
            AppLanguage.ENGLISH to "Refresh ANM",
            AppLanguage.ROMANIAN to "Actualizează ANM",
            AppLanguage.HUNGARIAN to "Frissítés"
        ),
        "anm_validity" to mapOf(
            AppLanguage.ENGLISH to "Validity",
            AppLanguage.ROMANIAN to "Valabilitate",
            AppLanguage.HUNGARIAN to "Érvényesség"
        ),
        "anm_affected_zones" to mapOf(
            AppLanguage.ENGLISH to "Affected Areas",
            AppLanguage.ROMANIAN to "Zone Afectate",
            AppLanguage.HUNGARIAN to "Érintett területek"
        ),
        "anm_phenomena" to mapOf(
            AppLanguage.ENGLISH to "Targeted Phenomena",
            AppLanguage.ROMANIAN to "Fenomene Vizate",
            AppLanguage.HUNGARIAN to "Várható jelenségek"
        ),
        "anm_view_map" to mapOf(
            AppLanguage.ENGLISH to "View Synoptic Map",
            AppLanguage.ROMANIAN to "Vezi Harta Sinoptică",
            AppLanguage.HUNGARIAN to "Térkép megtekintése"
        ),
        "anm_open_web" to mapOf(
            AppLanguage.ENGLISH to "Open meteoromania.ro",
            AppLanguage.ROMANIAN to "Deschide meteoromania.ro",
            AppLanguage.HUNGARIAN to "Megnyitás: meteoromania.ro"
        ),
        "anm_products_title" to mapOf(
            AppLanguage.ENGLISH to "National Meteorological Climatology Products",
            AppLanguage.ROMANIAN to "Hărți și Produse Climatologice Naționale ANM",
            AppLanguage.HUNGARIAN to "Országos Meteorológiai Termékek és Hőtérképek"
        ),
        "anm_products_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Official observed maps: Hourly Air Temp, Real-Feel, ITU index & monthly aggregates",
            AppLanguage.ROMANIAN to "Hărți oficiale observate: Temp. orară, Temp. resimțită, Indice ITU & cumulat lunar",
            AppLanguage.HUNGARIAN to "Hivatalos térképek: óránkénti léghőmérséklet, hőérzet, ITU index és havi adatok"
        ),
        "anm_view_full_map" to mapOf(
            AppLanguage.ENGLISH to "Inspect Full Map",
            AppLanguage.ROMANIAN to "Mărește Harta",
            AppLanguage.HUNGARIAN to "Térkép nagyítása"
        ),
        "nav_radar_satellite" to mapOf(
            AppLanguage.ENGLISH to "Radar & Sat",
            AppLanguage.ROMANIAN to "Radar & Satelit",
            AppLanguage.HUNGARIAN to "Radar és Műhold"
        ),
        "tab_national_radar" to mapOf(
            AppLanguage.ENGLISH to "National Radar",
            AppLanguage.ROMANIAN to "Radar Național",
            AppLanguage.HUNGARIAN to "Országos Radar"
        ),
        "tab_radar_stations" to mapOf(
            AppLanguage.ENGLISH to "7 ANM Stations",
            AppLanguage.ROMANIAN to "7 Stații ANM",
            AppLanguage.HUNGARIAN to "7 ANM Állomás"
        ),
        "tab_rainviewer" to mapOf(
            AppLanguage.ENGLISH to "Global Radar",
            AppLanguage.ROMANIAN to "Radar Global",
            AppLanguage.HUNGARIAN to "Globális Radar"
        ),
        "tab_satellite" to mapOf(
            AppLanguage.ENGLISH to "METEOSAT-10",
            AppLanguage.ROMANIAN to "Satelit METEOSAT",
            AppLanguage.HUNGARIAN to "METEOSAT Műhold"
        ),
        "radar_legend" to mapOf(
            AppLanguage.ENGLISH to "Reflectivity dBZ",
            AppLanguage.ROMANIAN to "Reflectivitate dBZ",
            AppLanguage.HUNGARIAN to "Reflektivitás dBZ"
        ),
        "radar_play" to mapOf(
            AppLanguage.ENGLISH to "Play Loop",
            AppLanguage.ROMANIAN to "Redă Buclă",
            AppLanguage.HUNGARIAN to "Lejátszás"
        ),
        "radar_pause" to mapOf(
            AppLanguage.ENGLISH to "Pause",
            AppLanguage.ROMANIAN to "Pauză",
            AppLanguage.HUNGARIAN to "Szünet"
        ),
        "radar_speed" to mapOf(
            AppLanguage.ENGLISH to "Playback Speed",
            AppLanguage.ROMANIAN to "Viteză Rulare",
            AppLanguage.HUNGARIAN to "Sebesség"
        ),
        "radar_opacity" to mapOf(
            AppLanguage.ENGLISH to "Radar Opacity",
            AppLanguage.ROMANIAN to "Opacitate Radar",
            AppLanguage.HUNGARIAN to "Átlátszóság"
        ),
        "radar_opendata_title" to mapOf(
            AppLanguage.ENGLISH to "7 National Radar Stations (OpenData)",
            AppLanguage.ROMANIAN to "Rețeaua Națională Radar ANM (7 Stații)",
            AppLanguage.HUNGARIAN to "7 Országos ANM Radarállomás (OpenData)"
        ),
        "radar_opendata_desc" to mapOf(
            AppLanguage.ENGLISH to "ANM Doppler Radar Network with raw HDF5 volumetric scans updated every 5-10 min",
            AppLanguage.ROMANIAN to "Rețeaua Doppler ANM cu date brute HDF5 actualizate la 5-10 minute",
            AppLanguage.HUNGARIAN to "ANM Doppler radarhálózat nyers HDF5 adatokkal 5-10 percenként frissítve"
        ),
        "radar_open_station_folder" to mapOf(
            AppLanguage.ENGLISH to "Open OpenData Repository",
            AppLanguage.ROMANIAN to "Deschide Depozit OpenData",
            AppLanguage.HUNGARIAN to "OpenData megnyitása"
        ),
        "satellite_channels" to mapOf(
            AppLanguage.ENGLISH to "Spectral Channels",
            AppLanguage.ROMANIAN to "Canale Spectrale",
            AppLanguage.HUNGARIAN to "Spektrális csatornák"
        ),
        "satellite_time" to mapOf(
            AppLanguage.ENGLISH to "Observation Time (UTC)",
            AppLanguage.ROMANIAN to "Ora Observației (UTC)",
            AppLanguage.HUNGARIAN to "Megfigyelés ideje (UTC)"
        ),
        "opendata_portal_title" to mapOf(
            AppLanguage.ENGLISH to "ANM OpenData Radar Portal",
            AppLanguage.ROMANIAN to "Portal Oficial OpenData Radar ANM",
            AppLanguage.HUNGARIAN to "Hivatalos ANM OpenData Radar Portál"
        ),
        "opendata_portal_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Direct access to raw volumetric Doppler radar scans (HDF5 / OPERA)",
            AppLanguage.ROMANIAN to "Acces direct la fișierele volumetrice brute Doppler (format HDF5 / OPERA)",
            AppLanguage.HUNGARIAN to "Közvetlen hozzáférés a nyers Doppler radarmérésekhez (HDF5 / OPERA formátum)"
        ),
        "opendata_open_portal" to mapOf(
            AppLanguage.ENGLISH to "Open OpenData Portal (opendata.meteoromania.ro/radar/)",
            AppLanguage.ROMANIAN to "Deschide Portalul OpenData (opendata.meteoromania.ro/radar/)",
            AppLanguage.HUNGARIAN to "OpenData Portál megnyitása (opendata.meteoromania.ro/radar/)"
        ),
        "copy_link" to mapOf(
            AppLanguage.ENGLISH to "Copy Link",
            AppLanguage.ROMANIAN to "Copiază Link",
            AppLanguage.HUNGARIAN to "Hivatkozás másolása"
        ),
        "link_copied" to mapOf(
            AppLanguage.ENGLISH to "Link copied to clipboard!",
            AppLanguage.ROMANIAN to "Link copiat în clipboard!",
            AppLanguage.HUNGARIAN to "Hivatkozás a vágólapra másolva!"
        )
    )
}
