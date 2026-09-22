package com.example.data.remote

import android.text.Html
import com.example.model.AnmProductItem
import com.example.model.AnmRadarFrame
import com.example.model.AnmWarningItem
import com.example.model.AnmWarningSeverity
import com.example.model.MeteosatChannelInfo
import com.example.model.MeteosatFrame
import com.example.model.RadarStationFileItem
import com.example.model.RadarStationInfo
import com.example.model.RainViewerRadarFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object AnmRemoteDataSource {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private const val BASE_ANM_URL = "https://www.meteoromania.ro"
    private const val NOWCASTING_API = "$BASE_ANM_URL/wp-json/meteoapi/v2/avertizari-nowcasting"
    private const val GENERAL_API = "$BASE_ANM_URL/wp-json/meteoapi/v2/avertizari-generale"

    suspend fun getNowcastingWarnings(): List<AnmWarningItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<AnmWarningItem>()
        try {
            val request = Request.Builder()
                .url(NOWCASTING_API)
                .header("User-Agent", "Mozilla/5.0 (Android Meteo Models)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val body = response.body?.string() ?: return@withContext emptyList()

            val root = JSONObject(body)
            val avObj = root.opt("avertizare")

            val items = when (avObj) {
                is JSONArray -> {
                    val res = mutableListOf<JSONObject>()
                    for (i in 0 until avObj.length()) {
                        avObj.optJSONObject(i)?.let { res.add(it) }
                    }
                    res
                }
                is JSONObject -> listOf(avObj)
                else -> emptyList()
            }

            for ((index, item) in items.withIndex()) {
                val attrs = item.optJSONObject("@attributes") ?: JSONObject()
                val typeName = attrs.optString("numeTipMesaj", "Atenționare nowcasting")
                val colorName = attrs.optString("numeCuloare", "galben")
                val start = attrs.optString("dataInceput", "")
                val end = attrs.optString("dataSfarsit", "")
                val zoneRaw = attrs.optString("zona", "")
                val semnalare = attrs.optString("semnalare", "")

                val cleanZone = cleanHtml(zoneRaw)
                val interval = if (start.isNotEmpty() && end.isNotEmpty()) {
                    "$start → $end"
                } else {
                    attrs.optString("intervalul", "În vigoare")
                }

                list.add(
                    AnmWarningItem(
                        id = "nowcast_$index",
                        typeName = typeName,
                        severity = AnmWarningSeverity.fromColorName(colorName),
                        validInterval = interval,
                        startDate = start,
                        endDate = end,
                        affectedZones = cleanZone.ifEmpty { "Zone conform comunicatului ANM" },
                        targetPhenomena = semnalare.ifEmpty { "Fenomene meteorologice periculoase imediate" },
                        fullMessageClean = "$typeName [$colorName]\nValabilitate: $interval\nZone afectate:\n$cleanZone\n\nFenomene vizate:\n$semnalare",
                        isNowcasting = true,
                        mapImageUrl = "https://www.meteoromania.ro/wp-content/plugins/meteo/harti/harta.svg.php"
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        list
    }

    suspend fun getGeneralWarnings(): List<AnmWarningItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<AnmWarningItem>()
        try {
            val request = Request.Builder()
                .url(GENERAL_API)
                .header("User-Agent", "Mozilla/5.0 (Android Meteo Models)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val body = response.body?.string() ?: return@withContext emptyList()

            val root = JSONObject(body)
            val avObj = root.opt("avertizare")

            val items = when (avObj) {
                is JSONArray -> {
                    val res = mutableListOf<JSONObject>()
                    for (i in 0 until avObj.length()) {
                        avObj.optJSONObject(i)?.let { res.add(it) }
                    }
                    res
                }
                is JSONObject -> listOf(avObj)
                else -> emptyList()
            }

            for ((index, item) in items.withIndex()) {
                val attrs = item.optJSONObject("@attributes") ?: JSONObject()
                val typeName = attrs.optString("numeTipMesaj", "Avertizare meteorologică generală")
                val colorName = attrs.optString("numeCuloare", "galben")
                val start = attrs.optString("dataAparitiei", "")
                val end = attrs.optString("dataExpirarii", "")
                val rawMsg = attrs.optString("mesaj", "")
                val targetPhenomena = attrs.optString("fenomeneVizate", "Conform textului avertizării")
                val affectedZones = attrs.optString("zonaAfectata", "Conform hărții ANM")

                val cleanMsg = cleanHtml(rawMsg)
                val interval = if (start.isNotEmpty() && end.isNotEmpty()) {
                    "$start → $end"
                } else {
                    attrs.optString("intervalul", "Conform textului")
                }

                list.add(
                    AnmWarningItem(
                        id = "general_$index",
                        typeName = typeName,
                        severity = AnmWarningSeverity.fromColorName(colorName),
                        validInterval = interval,
                        startDate = start,
                        endDate = end,
                        affectedZones = affectedZones,
                        targetPhenomena = targetPhenomena,
                        fullMessageClean = cleanMsg.ifEmpty { "Avertizare emisă de Administrația Națională de Meteorologie." },
                        isNowcasting = false,
                        mapImageUrl = "https://www.meteoromania.ro/wp-content/plugins/meteo/harti/harta.svg.php"
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        list
    }

    private fun cleanHtml(html: String): String {
        return try {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().trim()
        } catch (e: Exception) {
            html.replace(Regex("<[^>]*>"), " ").trim()
        }
    }

    fun getAnmClimatologyProducts(): List<AnmProductItem> {
        return listOf(
            AnmProductItem(
                id = "temp_orara",
                titleRo = "Temperatura Aerului (Orară)",
                titleEn = "Air Temperature (Hourly)",
                titleHu = "Léghőmérséklet (Óránkénti)",
                descriptionRo = "Harta valorilor de temperatură a aerului la stațiile meteorologice din România, actualizată orar.",
                descriptionEn = "Hourly surface air temperature map measured across Romanian national meteorological stations.",
                descriptionHu = "Románia meteorológiai állomásain mért óránkénti léghőmérsékleti térkép.",
                imageUrl = "https://www.meteoromania.ro/images/clima/temperatura_orara.png",
                updateFrequency = "Orar (Hourly)",
                category = "Temperatura"
            ),
            AnmProductItem(
                id = "ttresim",
                titleRo = "Temperatura Resimțită",
                titleEn = "Apparent / Real-Feel Temperature",
                titleHu = "Érzékelt Hőmérséklet",
                descriptionRo = "Harta temperaturii resimțite calculată pe baza umidității, vântului și radiației.",
                descriptionEn = "Real-feel apparent temperature index combining wind chill and heat index.",
                descriptionHu = "Szél és páratartalom alapján kalkulált érzékelt hőmérséklet.",
                imageUrl = "https://www.meteoromania.ro/images/clima/ttresim.png",
                updateFrequency = "Orar (Hourly)",
                category = "Confort Termic"
            ),
            AnmProductItem(
                id = "itu_orar",
                titleRo = "Indicele de Confort Termic (ITU Orar)",
                titleEn = "Thermal Comfort Index (Hourly ITU)",
                titleHu = "Hőérzeti Index (Óránkénti ITU)",
                descriptionRo = "Indicele temperatură-umezeală (ITU) interpolat la nivelul întregii țări. Pragul critic este de 80 unități.",
                descriptionEn = "Temperature-Humidity Index (ITU) interpolated nationally. Critical threshold is 80 units.",
                descriptionHu = "Országos hőmérséklet-páratartalom komfortindex (ITU). Kritikus érték: 80.",
                imageUrl = "https://www.meteoromania.ro/images/clima/ITU_orar_interpolat.png",
                updateFrequency = "Orar (Hourly)",
                category = "Confort Termic"
            ),
            AnmProductItem(
                id = "temp_orara_ieri",
                titleRo = "Temperatura Orară (Ieri)",
                titleEn = "Hourly Temperature (Yesterday)",
                titleHu = "Óránkénti Hőmérséklet (Tegnap)",
                descriptionRo = "Distribuția temperaturilor orare din ziua precedentă pe teritoriul României.",
                descriptionEn = "Comparison map of hourly temperatures recorded yesterday.",
                descriptionHu = "Az előző napi óránkénti hőmérséklet eloszlása.",
                imageUrl = "https://www.meteoromania.ro/images/clima/temperatura_orara_ieri.png",
                updateFrequency = "Zilnic (Daily)",
                category = "Istoric Recente"
            ),
            AnmProductItem(
                id = "temp_min_ieri",
                titleRo = "Temperatura Minimă Înregistrată (Ieri)",
                titleEn = "Minimum Temperature (Yesterday)",
                titleHu = "Legalacsonyabb Hőmérséklet (Tegnap)",
                descriptionRo = "Harta valorilor minime nocturne și matinale de temperatură din ziua precedentă.",
                descriptionEn = "Minimum night and morning temperatures registered across Romania yesterday.",
                descriptionHu = "A tegnapi nap folyamán regisztrált legalacsonyabb hőmérsékletek.",
                imageUrl = "https://www.meteoromania.ro/images/clima/temperatura_min_ieri.png",
                updateFrequency = "Zilnic (Daily)",
                category = "Istoric Recente"
            ),
            AnmProductItem(
                id = "itu_ieri",
                titleRo = "Indicele de Confort Termic (Ieri)",
                titleEn = "Thermal Comfort Index (Yesterday)",
                titleHu = "Hőérzeti Index (Tegnap)",
                descriptionRo = "Valorile ITU orare interpolate calculate pentru ziua de ieri.",
                descriptionEn = "Interpolated ITU comfort index recorded over the previous day.",
                descriptionHu = "Tegnapi hőérzeti index értékek.",
                imageUrl = "https://www.meteoromania.ro/images/clima/ITU_orar_interpolat_ieri.png",
                updateFrequency = "Zilnic (Daily)",
                category = "Confort Termic"
            ),
            AnmProductItem(
                id = "precipitatii_luna",
                titleRo = "Cantitatea de Precipitații Lunară ANM",
                titleEn = "Monthly Precipitation Accumulation ANM",
                titleHu = "Havi Csapadékösszeg ANM",
                descriptionRo = "Harta cantităților cumulate de precipitații (l/mp) din luna de referință.",
                descriptionEn = "Monthly accumulated precipitation map (liters/m²) issued by ANM.",
                descriptionHu = "Az ANM által kiadott havi összesített csapadéktérkép.",
                imageUrl = "https://www.meteoromania.ro/images/anm_maps/precipitation_08_2026.png",
                updateFrequency = "Lunar (Monthly)",
                category = "Precipitații"
            ),
            AnmProductItem(
                id = "temperatura_medie_luna",
                titleRo = "Temperatura Medie Lunară ANM",
                titleEn = "Monthly Mean Temperature ANM",
                titleHu = "Havi Középhőmérséklet ANM",
                descriptionRo = "Harta temperaturilor medii lunare și a anomaliilor termice pe teritoriul României.",
                descriptionEn = "Monthly mean surface temperature map across Romanian climate stations.",
                descriptionHu = "Románia havi középhőmérsékleti térképe az ANM állomásai alapján.",
                imageUrl = "https://www.meteoromania.ro/images/anm_maps/temperature_08_2026.png",
                updateFrequency = "Lunar (Monthly)",
                category = "Climatologie"
            )
        )
    }

    // --- ANM National Composite Radar (Updated every 10 min) ---
    suspend fun getNationalRadarFrames(): List<AnmRadarFrame> = withContext(Dispatchers.IO) {
        val result = mutableListOf<AnmRadarFrame>()
        try {
            val request = Request.Builder()
                .url("https://www.meteoromania.ro/wp-content/plugins/meteo/json/imagini-radar.php")
                .header("User-Agent", "Mozilla/5.0 (Android Meteo Models)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val body = response.body?.string() ?: return@withContext emptyList()

            val root = JSONObject(body)
            val pozeObj = root.optJSONObject("poze") ?: return@withContext emptyList()

            val keys = pozeObj.keys()
            val listWithIndex = mutableListOf<Pair<Int, AnmRadarFrame>>()

            while (keys.hasNext()) {
                val key = keys.next()
                val idx = key.toIntOrNull() ?: continue
                val item = pozeObj.optJSONObject(key) ?: continue
                val rawUrl = item.optString("poza", "")
                val timp = item.optString("timp", "")
                if (rawUrl.isNotEmpty()) {
                    val cleanUrl = rawUrl.replace("\\/", "/")
                    listWithIndex.add(idx to AnmRadarFrame(idx, cleanUrl, timp))
                }
            }

            // Sort ascending by index (0 is oldest, 39 is most recent)
            listWithIndex.sortBy { it.first }
            result.addAll(listWithIndex.map { it.second })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        result
    }

    // --- 7 National Dual-Pol Doppler Radar Stations (ANM OpenData) ---
    fun getRadarStations(): List<RadarStationInfo> {
        return listOf(
            RadarStationInfo(
                code = "ORA",
                name = "Oradea",
                county = "Bihor",
                locationName = "Dealul Vântului",
                coordinates = "47.078° N, 21.933° E",
                elevationMeters = 205,
                radarModel = "C-Band Polarimetric Doppler",
                band = "C-Band (5.6 GHz)",
                coverageRadiusKm = 250,
                openDataUrl = "https://opendata.meteoromania.ro/radar/ORA/",
                parameters = listOf(
                    "dBZ (Reflectivitate)",
                    "dBR (Rata precipitațiilor)",
                    "Height (Înălțime ecou)",
                    "V (Viteză radială Doppler)",
                    "RhoHV (Coeficient corelație)",
                    "ZDR (Reflectivitate diferențială)",
                    "KDP (Fază diferențială)"
                ),
                descriptionRo = "Acoperă Crișana, nord-vestul României, granița vestică și Munții Apuseni. Monitorizează furtunile convective care intră din Câmpia Panonică.",
                descriptionEn = "Covers Crișana, NW Romania, western borders and Apuseni Mountains. Detects incoming convective severe storms from the Pannonian basin."
            ),
            RadarStationInfo(
                code = "BOB",
                name = "Bobohalma",
                county = "Mureș",
                locationName = "Bobohalma (Târnăveni)",
                coordinates = "46.367° N, 24.233° E",
                elevationMeters = 510,
                radarModel = "S-Band Dual-Pol Doppler (WSR-98D)",
                band = "S-Band (2.8 GHz)",
                coverageRadiusKm = 460,
                openDataUrl = "https://opendata.meteoromania.ro/radar/BOB/",
                parameters = listOf(
                    "dBZ (Reflectivitate)",
                    "dBR (Rata precipitațiilor)",
                    "Height (Înălțime ecou)",
                    "V (Viteză radială Doppler)",
                    "RhoHV (Coeficient corelație)",
                    "ZDR (Reflectivitate diferențială)",
                    "KDP (Fază diferențială)"
                ),
                descriptionRo = "Principalul radar de mare putere din Podișul Transilvaniei. Raza extinsă acoperă întregul arc intracarpatic.",
                descriptionEn = "Primary high-power S-band radar covering the Transylvanian Plateau and intra-Carpathian arc with long-range penetrative capabilities."
            ),
            RadarStationInfo(
                code = "BUC",
                name = "București",
                county = "București / Ilfov",
                locationName = "Aeroport Băneasa",
                coordinates = "44.502° N, 26.079° E",
                elevationMeters = 90,
                radarModel = "C-Band Polarimetric Doppler",
                band = "C-Band (5.6 GHz)",
                coverageRadiusKm = 250,
                openDataUrl = "https://opendata.meteoromania.ro/radar/BUC/",
                parameters = listOf(
                    "dBZ (Reflectivitate)",
                    "dBR (Rata precipitațiilor)",
                    "Height (Înălțime ecou)",
                    "V (Viteză radială Doppler)",
                    "RhoHV (Coeficient corelație)",
                    "ZDR (Reflectivitate diferențială)",
                    "KDP (Fază diferențială)"
                ),
                descriptionRo = "Monitorizează aria metropolitană București, Câmpia Română și valea inferioară a Dunării.",
                descriptionEn = "Dedicated surveillance for Bucharest metropolitan area, southern Romanian plain, and Danube lower basin."
            ),
            RadarStationInfo(
                code = "CRA",
                name = "Craiova",
                county = "Dolj",
                locationName = "Cârcea",
                coordinates = "44.275° N, 23.900° E",
                elevationMeters = 190,
                radarModel = "S-Band Dual-Pol Doppler (WSR-98D)",
                band = "S-Band (2.8 GHz)",
                coverageRadiusKm = 460,
                openDataUrl = "https://opendata.meteoromania.ro/radar/CRA/",
                parameters = listOf(
                    "dBZ (Reflectivitate)",
                    "dBR (Rata precipitațiilor)",
                    "Height (Înălțime ecou)",
                    "V (Viteză radială Doppler)",
                    "RhoHV (Coeficient corelație)",
                    "ZDR (Reflectivitate diferențială)",
                    "KDP (Fază diferențială)"
                ),
                descriptionRo = "Radar strategic pentru Oltenia și sudul României, detectând furtunile din Peninsula Balcanică.",
                descriptionEn = "Strategic S-band radar monitoring Oltenia, southern plains, and convective fronts propagating north from the Balkans."
            ),
            RadarStationInfo(
                code = "MED",
                name = "Mediaș",
                county = "Sibiu",
                locationName = "Dealul Cetății",
                coordinates = "46.160° N, 24.350° E",
                elevationMeters = 435,
                radarModel = "C-Band Polarimetric Doppler",
                band = "C-Band (5.6 GHz)",
                coverageRadiusKm = 250,
                openDataUrl = "https://opendata.meteoromania.ro/radar/MED/",
                parameters = listOf(
                    "dBZ (Reflectivitate)",
                    "dBR (Rata precipitațiilor)",
                    "Height (Înălțime ecou)",
                    "V (Viteză radială Doppler)",
                    "RhoHV (Coeficient corelație)",
                    "ZDR (Reflectivitate diferențială)",
                    "KDP (Fază diferențială)"
                ),
                descriptionRo = "Amplasat în inima Transilvaniei, asigură supravegherea montană a Carpaților Meridionali și Orientali.",
                descriptionEn = "Central Transylvanian location providing detailed orographic precipitation analysis over the Southern & Eastern Carpathians."
            ),
            RadarStationInfo(
                code = "BAR",
                name = "Bârlad",
                county = "Vaslui",
                locationName = "Grivița",
                coordinates = "46.220° N, 27.650° E",
                elevationMeters = 230,
                radarModel = "S-Band Dual-Pol Doppler (WSR-98D)",
                band = "S-Band (2.8 GHz)",
                coverageRadiusKm = 460,
                openDataUrl = "https://opendata.meteoromania.ro/radar/BAR/",
                parameters = listOf(
                    "dBZ (Reflectivitate)",
                    "dBR (Rata precipitațiilor)",
                    "Height (Înălțime ecou)",
                    "V (Viteză radială Doppler)",
                    "RhoHV (Coeficient corelație)",
                    "ZDR (Reflectivitate diferențială)",
                    "KDP (Fază diferențială)"
                ),
                descriptionRo = "Acoperă Moldova, estul României și frontierele estice, monitorizând ciclonii din bazinul Mării Negre.",
                descriptionEn = "Surveillance for Moldavia and eastern Romania, essential for tracking Black Sea convective systems."
            ),
            RadarStationInfo(
                code = "TIM",
                name = "Timișoara",
                county = "Timiș",
                locationName = "Urseni",
                coordinates = "45.698° N, 21.285° E",
                elevationMeters = 95,
                radarModel = "S-Band Dual-Pol Doppler (WSR-98D)",
                band = "S-Band (2.8 GHz)",
                coverageRadiusKm = 460,
                openDataUrl = "https://opendata.meteoromania.ro/radar/TIM/",
                parameters = listOf(
                    "dBZ (Reflectivitate)",
                    "dBR (Rata precipitațiilor)",
                    "Height (Înălțime ecou)",
                    "V (Viteză radială Doppler)",
                    "RhoHV (Coeficient corelație)",
                    "ZDR (Reflectivitate diferențială)",
                    "KDP (Fază diferențială)"
                ),
                descriptionRo = "Radar cu acoperire strategică în Banat, vestul Olteniei și frontiera sud-vestică cu Serbia și Ungaria.",
                descriptionEn = "Strategic coverage for Banat, western Oltenia, and the southwestern corridor bordering Serbia and Hungary."
            ),
            RadarStationInfo(
                code = "COMPOSITE",
                name = "Mozaic Național ANM",
                county = "România",
                locationName = "Server Central ANM",
                coordinates = "45.943° N, 24.966° E",
                elevationMeters = 0,
                radarModel = "Rețea Integrată Națională",
                band = "Composite Multi-Frecvență",
                coverageRadiusKm = 500,
                openDataUrl = "https://opendata.meteoromania.ro/radar/COMPOSITE/",
                parameters = listOf(
                    "dBZ Composite (Reflectivitate Națională)",
                    "dBR Composite (Rată Precipitații)",
                    "Height Composite (Vârf Noros Maxim)"
                ),
                descriptionRo = "Sinteză națională realizată prin fuziunea datelor de la toate cele 7 radare din Rețeaua Națională Doppler.",
                descriptionEn = "National mosaic generated by merging raw volumetric scans from all 7 Doppler stations into a continuous Romanian grid."
            )
        )
    }

    suspend fun getStationLatestFiles(stationCode: String): List<RadarStationFileItem> = withContext(Dispatchers.IO) {
        val result = mutableListOf<RadarStationFileItem>()
        try {
            val url = "https://opendata.meteoromania.ro/radar/$stationCode/"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Android Meteo Models)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val html = response.body?.string() ?: return@withContext emptyList()

            // Match lines like: <a href="ORA_2026092209150200dBZ.hdf">...</a> 22-Sep-2026 12:16 910K
            val pattern = Pattern.compile(
                "<a href=\"(${stationCode}_(\\d{8})(\\d{4})\\d{4}([A-Za-z]+)\\.hdf)\">.*?</a>\\s+(\\d{2}-[A-Za-z]+-\\d{4}\\s+\\d{2}:\\d{2})\\s+([0-9A-Za-z]+)",
                Pattern.CASE_INSENSITIVE
            )
            val matcher = pattern.matcher(html)
            val allMatches = mutableListOf<RadarStationFileItem>()

            while (matcher.find()) {
                val filename = matcher.group(1) ?: continue
                val dateStr = matcher.group(2) ?: ""
                val timeStr = matcher.group(3) ?: ""
                val product = matcher.group(4) ?: "Radar"
                val serverDate = matcher.group(5) ?: ""
                val size = matcher.group(6) ?: ""

                val formattedTime = if (timeStr.length == 4) {
                    "${timeStr.substring(0, 2)}:${timeStr.substring(2, 4)} UTC"
                } else timeStr

                allMatches.add(
                    RadarStationFileItem(
                        filename = filename,
                        productType = product,
                        timestampStr = "$serverDate ($formattedTime)",
                        fileSizeStr = size,
                        downloadUrl = "$url$filename"
                    )
                )
            }

            // Return the latest 15 files (reversed)
            result.addAll(allMatches.takeLast(15).reversed())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        result
    }

    // --- ANM METEOSAT-10 Satellite Channels ---
    fun getMeteosatChannels(): List<MeteosatChannelInfo> {
        return listOf(
            MeteosatChannelInfo(
                tipNumber = 1,
                channelId = "id806",
                titleRo = "Analiza Maselor de Aer (Air Mass RGB)",
                titleEn = "Air Mass Analysis (RGB)",
                descriptionRo = "Compoziție colorată din canale IR și vapori de apă ce evidențiază fronturile reci/calde, jet stream-ul și intruziunile uscate din stratosferă.",
                descriptionEn = "Color composite from IR and water-vapour channels highlighting frontal dynamics, jet streams, and dry stratospheric intrusions.",
                resolution = "3.0 km",
                spectrum = "WV 6.2 µm / WV 7.3 µm / IR 9.7 µm / IR 10.8 µm"
            ),
            MeteosatChannelInfo(
                tipNumber = 2,
                channelId = "id813",
                titleRo = "Vizibil 0.6 µm (Reflectanță)",
                titleEn = "Visible 0.6 µm (Reflectance)",
                descriptionRo = "Canal optic solar diurn ce oferă o imagine clară a formațiunilor noroase joase, ceții, stratocumulus și zăpezii montane.",
                descriptionEn = "Daytime solar optical channel providing high contrast for low clouds, fog, stratocumulus, and snow cover.",
                resolution = "3.0 km",
                spectrum = "Optic Vizibil 0.635 µm"
            ),
            MeteosatChannelInfo(
                tipNumber = 3,
                channelId = "id814",
                titleRo = "Canal Infraroșu Termic 10.8 µm",
                titleEn = "Thermal Infrared 10.8 µm",
                descriptionRo = "Măsoară temperatura de strălucire a norilor și solului atât ziua cât și noaptea. Norii înalți și reci (Cumulonimbus) apar albi strălucitori.",
                descriptionEn = "Measures cloud top and surface brightness temperature day & night. High cold clouds (Cumulonimbus) appear bright white.",
                resolution = "3.0 km",
                spectrum = "Infraroșu Termic 10.8 µm"
            ),
            MeteosatChannelInfo(
                tipNumber = 4,
                channelId = "id815",
                titleRo = "Canal Vizibil Înaltă Rezoluție 0.7 µm (HRV)",
                titleEn = "High Resolution Visible 0.7 µm (HRV)",
                descriptionRo = "Canal de înaltă rezoluție spațială (1 km) pentru observarea detaliată a convecției locale, furtunilor și undelor orografice.",
                descriptionEn = "Sub-kilometer visible channel for precise tracking of localized convective cells, storm towers, and orographic mountain waves.",
                resolution = "1.0 km (Sub-satelitar)",
                spectrum = "HRV 0.4 – 1.1 µm"
            )
        )
    }

    suspend fun getMeteosatFrames(tipNumber: Int): List<MeteosatFrame> = withContext(Dispatchers.IO) {
        val result = mutableListOf<MeteosatFrame>()
        try {
            val url = "https://www.meteoromania.ro/sateliti/index.php?tip=$tipNumber"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Android Meteo Models)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val html = response.body?.string() ?: return@withContext emptyList()

            // Match options like: <OPTION value="img/id806/id806_2026092209.png">Imagine satelit ... la data: An 2026 Luna 09 Zi 22 Ora 09 UTC</OPTION>
            val pattern = Pattern.compile(
                "<OPTION\\s+value=\"(img/id\\d+/id\\d+_(\\d{4})(\\d{2})(\\d{2})(\\d{2})\\.png)\">(.*?)</OPTION>",
                Pattern.CASE_INSENSITIVE
            )
            val matcher = pattern.matcher(html)

            while (matcher.find()) {
                val relPath = matcher.group(1) ?: continue
                val year = matcher.group(2) ?: ""
                val month = matcher.group(3) ?: ""
                val day = matcher.group(4) ?: ""
                val hour = matcher.group(5) ?: ""
                val fullUrl = "https://www.meteoromania.ro/sateliti/$relPath"
                val timeUtc = "$day.$month.$year $hour:00 UTC"

                result.add(
                    MeteosatFrame(
                        imageUrl = fullUrl,
                        timeUtc = timeUtc,
                        formattedTime = "$hour:00 UTC"
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        result
    }

    // --- RainViewer Open-Source Radar API ---
    suspend fun getRainViewerRadarFrames(): List<RainViewerRadarFrame> = withContext(Dispatchers.IO) {
        val result = mutableListOf<RainViewerRadarFrame>()
        try {
            val request = Request.Builder()
                .url("https://api.rainviewer.com/public/weather-maps.json")
                .header("User-Agent", "Mozilla/5.0 (Android Meteo Models)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            val body = response.body?.string() ?: return@withContext emptyList()

            val root = JSONObject(body)
            val host = root.optString("host", "https://tilecache.rainviewer.com")
            val radarObj = root.optJSONObject("radar") ?: return@withContext emptyList()

            val past = radarObj.optJSONArray("past") ?: JSONArray()
            val nowcast = radarObj.optJSONArray("nowcast") ?: JSONArray()

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }

            for (i in 0 until past.length()) {
                val item = past.optJSONObject(i) ?: continue
                val time = item.optLong("time", 0)
                val path = item.optString("path", "")
                if (path.isNotEmpty() && time > 0) {
                    val date = Date(time * 1000)
                    result.add(
                        RainViewerRadarFrame(
                            timeUnix = time,
                            path = "$host$path",
                            formattedTime = sdf.format(date),
                            isNowcast = false
                        )
                    )
                }
            }

            for (i in 0 until nowcast.length()) {
                val item = nowcast.optJSONObject(i) ?: continue
                val time = item.optLong("time", 0)
                val path = item.optString("path", "")
                if (path.isNotEmpty() && time > 0) {
                    val date = Date(time * 1000)
                    result.add(
                        RainViewerRadarFrame(
                            timeUnix = time,
                            path = "$host$path",
                            formattedTime = "${sdf.format(date)} (Prognoză)",
                            isNowcast = true
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        result
    }
}

