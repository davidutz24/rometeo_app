package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOutMap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.model.AnmRadarFrame
import com.example.model.AppLanguage
import com.example.model.EumetsatLayerMode
import com.example.model.EumetsatSatelliteFrame
import com.example.model.MeteosatChannelInfo
import com.example.model.MeteosatFrame
import com.example.model.RadarSatelliteSubTab
import com.example.model.RadarStationInfo
import com.example.model.RainViewerRadarFrame
import com.example.model.Translations
import com.example.viewmodel.ThemeMode
import com.example.viewmodel.WeatherViewModel
import kotlinx.coroutines.delay

@Composable
fun RadarSatelliteScreen(
    viewModel: WeatherViewModel,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val subTab by viewModel.radarSubTab.collectAsState()
    val nationalFrames by viewModel.nationalRadarFrames.collectAsState()
    val currentFrameIndex by viewModel.currentRadarFrameIndex.collectAsState()
    val isPlaying by viewModel.isRadarPlaying.collectAsState()
    val radarOpacity by viewModel.radarOpacity.collectAsState()
    val playbackSpeedMs by viewModel.radarPlaybackSpeedMs.collectAsState()
    val isLoadingRadar by viewModel.isLoadingRadar.collectAsState()

    val selectedStation by viewModel.selectedRadarStation.collectAsState()

    val selectedChannel by viewModel.selectedMeteosatChannel.collectAsState()
    val meteosatFrames by viewModel.meteosatFrames.collectAsState()
    val selectedMeteosatFrame by viewModel.selectedMeteosatFrame.collectAsState()
    val isLoadingMeteosat by viewModel.isLoadingMeteosat.collectAsState()

    val rainViewerFrames by viewModel.rainViewerFrames.collectAsState()
    val currentRainViewerIndex by viewModel.currentRainViewerIndex.collectAsState()
    val isRainViewerPlaying by viewModel.isRainViewerPlaying.collectAsState()
    val isLoadingRainViewer by viewModel.isLoadingRainViewer.collectAsState()

    val eumetsatFrames by viewModel.eumetsatFrames.collectAsState()
    val selectedEumetsatLayerMode by viewModel.selectedEumetsatLayerMode.collectAsState()
    val currentEumetsatIndex by viewModel.currentEumetsatIndex.collectAsState()
    val isEumetsatPlaying by viewModel.isEumetsatPlaying.collectAsState()
    val isLoadingEumetsat by viewModel.isLoadingEumetsat.collectAsState()

    val themeMode by viewModel.themeMode.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }
    var fullScreenTitle by remember { mutableStateOf("") }
    var isMapFullscreen by remember { mutableStateOf(false) }

    // National Radar loop playback effect
    LaunchedEffect(isPlaying, playbackSpeedMs, nationalFrames.size) {
        if (isPlaying && nationalFrames.isNotEmpty()) {
            while (true) {
                delay(playbackSpeedMs)
                viewModel.stepRadarFrame(forward = true)
            }
        }
    }

    // RainViewer loop playback effect
    LaunchedEffect(isRainViewerPlaying, rainViewerFrames.size) {
        if (isRainViewerPlaying && rainViewerFrames.isNotEmpty()) {
            while (true) {
                delay(600)
                viewModel.stepRainViewerFrame(forward = true)
            }
        }
    }

    // EUMETSAT loop playback effect
    LaunchedEffect(isEumetsatPlaying, eumetsatFrames.size) {
        if (isEumetsatPlaying && eumetsatFrames.isNotEmpty()) {
            while (true) {
                delay(500)
                viewModel.stepEumetsatFrame(forward = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("radar_satellite_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // --- Header Section ---
        item {
            RadarHeader(
                subTab = subTab,
                onRefresh = {
                    when (subTab) {
                        RadarSatelliteSubTab.NATIONAL_RADAR, RadarSatelliteSubTab.RADAR_STATIONS -> viewModel.loadNationalRadar()
                        RadarSatelliteSubTab.RAINVIEWER_RADAR -> viewModel.loadRainViewerFrames()
                        RadarSatelliteSubTab.METEOSAT -> {
                            viewModel.loadEumetsatFrames()
                            viewModel.loadMeteosatFrames(selectedChannel.tipNumber)
                        }
                    }
                },
                lang = lang
            )
        }

        // --- Sub-Tab Navigation Bar ---
        item {
            RadarSubTabBar(
                selectedTab = subTab,
                onSelectTab = { tab ->
                    viewModel.selectRadarSubTab(tab)
                    if (tab == RadarSatelliteSubTab.RADAR_STATIONS && selectedStation.code == "COMPOSITE") {
                        // Switch to Bobohalma as primary individual station
                        val bob = viewModel.radarStations.find { it.code == "BOB" }
                        if (bob != null) {
                            viewModel.selectRadarStation(bob)
                        }
                    }
                },
                lang = lang
            )
        }

        // --- Active Sub-Tab Content ---
        when (subTab) {
            RadarSatelliteSubTab.NATIONAL_RADAR, RadarSatelliteSubTab.RADAR_STATIONS -> {
                item {
                    InteractiveRadarCompositeView(
                        frames = nationalFrames,
                        currentIndex = currentFrameIndex,
                        isPlaying = isPlaying,
                        opacity = radarOpacity,
                        playbackSpeedMs = playbackSpeedMs,
                        isLoading = isLoadingRadar,
                        selectedStation = selectedStation,
                        allStations = viewModel.radarStations,
                        isDarkTheme = isDarkTheme,
                        onIndexChange = { viewModel.setRadarFrameIndex(it) },
                        onTogglePlay = { viewModel.toggleRadarPlayback() },
                        onStep = { viewModel.stepRadarFrame(it) },
                        onOpacityChange = { viewModel.setRadarOpacity(it) },
                        onSpeedChange = { viewModel.setRadarPlaybackSpeed(it) },
                        onSelectStation = { viewModel.selectRadarStation(it) },
                        onToggleFullscreen = { isMapFullscreen = true },
                        onOpenWeb = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.meteoromania.ro/radarm/radar.index.php")
                            )
                            context.startActivity(intent)
                        },
                        lang = lang
                    )
                }
            }

            RadarSatelliteSubTab.RAINVIEWER_RADAR -> {
                item {
                    RainViewerRadarView(
                        frames = rainViewerFrames,
                        currentIndex = currentRainViewerIndex,
                        isPlaying = isRainViewerPlaying,
                        opacity = radarOpacity,
                        isLoading = isLoadingRainViewer,
                        isDarkTheme = isDarkTheme,
                        onIndexChange = { viewModel.setRainViewerIndex(it) },
                        onTogglePlay = { viewModel.toggleRainViewerPlayback() },
                        onStep = { viewModel.stepRainViewerFrame(it) },
                        onOpacityChange = { viewModel.setRadarOpacity(it) },
                        onToggleFullscreen = { isMapFullscreen = true },
                        lang = lang
                    )
                }
            }

            RadarSatelliteSubTab.METEOSAT -> {
                item {
                    EumetsatSatelliteView(
                        eumetsatFrames = eumetsatFrames,
                        selectedEumetsatLayerMode = selectedEumetsatLayerMode,
                        currentEumetsatIndex = currentEumetsatIndex,
                        isEumetsatPlaying = isEumetsatPlaying,
                        isLoadingEumetsat = isLoadingEumetsat,
                        channels = viewModel.meteosatChannels,
                        selectedChannel = selectedChannel,
                        meteosatFrames = meteosatFrames,
                        selectedMeteosatFrame = selectedMeteosatFrame,
                        isLoadingMeteosat = isLoadingMeteosat,
                        onSelectEumetsatLayer = { viewModel.selectEumetsatLayer(it) },
                        onSelectEumetsatIndex = { viewModel.setEumetsatIndex(it) },
                        onToggleEumetsatPlay = { viewModel.toggleEumetsatPlayback() },
                        onStepEumetsat = { viewModel.stepEumetsatFrame(it) },
                        onSelectChannel = { viewModel.selectMeteosatChannel(it) },
                        onSelectMeteosatFrame = { viewModel.selectMeteosatFrame(it) },
                        onOpenFullScreen = { url, title ->
                            fullScreenImageUrl = url
                            fullScreenTitle = title
                        },
                        onOpenWeb = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://sat24.com/en-gb/country/ro")
                            )
                            context.startActivity(intent)
                        },
                        lang = lang
                    )
                }
            }
        }
    }

    // Fullscreen Interactive Radar Map Dialog
    if (isMapFullscreen) {
        Dialog(
            onDismissRequest = { isMapFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                if (subTab == RadarSatelliteSubTab.RAINVIEWER_RADAR) {
                    val currentRvFrame = rainViewerFrames.getOrNull(currentRainViewerIndex)
                    RadarMapView(
                        mode = RadarMapMode.RAINVIEWER_RADAR,
                        currentRadarImageUrl = null,
                        rainViewerTilePath = currentRvFrame?.path,
                        opacity = radarOpacity,
                        selectedStation = null,
                        allStations = emptyList(),
                        isDarkTheme = isDarkTheme,
                        onSelectStation = {},
                        isFullscreen = true,
                        onToggleFullscreen = { isMapFullscreen = false }
                    )
                } else {
                    val currentNatFrame = nationalFrames.getOrNull(currentFrameIndex)
                    RadarMapView(
                        mode = RadarMapMode.ANM_RADAR,
                        currentRadarImageUrl = currentNatFrame?.imageUrl,
                        rainViewerTilePath = null,
                        opacity = radarOpacity,
                        selectedStation = selectedStation,
                        allStations = viewModel.radarStations,
                        isDarkTheme = isDarkTheme,
                        onSelectStation = { viewModel.selectRadarStation(it) },
                        isFullscreen = true,
                        onToggleFullscreen = { isMapFullscreen = false }
                    )
                }

                // Close Button in Top Left
                IconButton(
                    onClick = { isMapFullscreen = false },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                        .size(42.dp)
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Închide ecran complet",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Image Zoom Dialog for Meteosat
    fullScreenImageUrl?.let { imageUrl ->
        RadarInteractiveZoomDialog(
            imageUrl = imageUrl,
            title = fullScreenTitle,
            onDismiss = { fullScreenImageUrl = null }
        )
    }
}

// -------------------------------------------------------------------------
// Top Header
// -------------------------------------------------------------------------
@Composable
private fun RadarHeader(
    subTab: RadarSatelliteSubTab,
    onRefresh: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0284C7), Color(0xFF0D9488))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Radar,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = Translations.get("nav_radar_satellite", lang),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = when (subTab) {
                                RadarSatelliteSubTab.NATIONAL_RADAR -> "ANM Doppler • Hartă Live & Mozaic Național"
                                RadarSatelliteSubTab.RADAR_STATIONS -> "Cele 7 Stații Doppler ANM (Bobohalma, Oradea...)"
                                RadarSatelliteSubTab.RAINVIEWER_RADAR -> "RainViewer API • Radar Mondial & Nowcast"
                                RadarSatelliteSubTab.METEOSAT -> "EUMETSAT METEOSAT-10 • Canale IR & Vizibil"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Actualizează",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-Tab Switcher Bar
// -------------------------------------------------------------------------
@Composable
private fun RadarSubTabBar(
    selectedTab: RadarSatelliteSubTab,
    onSelectTab: (RadarSatelliteSubTab) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
        divider = {}
    ) {
        Tab(
            selected = selectedTab == RadarSatelliteSubTab.NATIONAL_RADAR,
            onClick = { onSelectTab(RadarSatelliteSubTab.NATIONAL_RADAR) },
            text = {
                Text(
                    text = "Radar România",
                    fontWeight = if (selectedTab == RadarSatelliteSubTab.NATIONAL_RADAR) FontWeight.Bold else FontWeight.Medium
                )
            },
            icon = { Icon(Icons.Rounded.Radar, contentDescription = null, modifier = Modifier.size(18.dp)) }
        )

        Tab(
            selected = selectedTab == RadarSatelliteSubTab.RADAR_STATIONS,
            onClick = { onSelectTab(RadarSatelliteSubTab.RADAR_STATIONS) },
            text = {
                Text(
                    text = "Radare Individuale",
                    fontWeight = if (selectedTab == RadarSatelliteSubTab.RADAR_STATIONS) FontWeight.Bold else FontWeight.Medium
                )
            },
            icon = { Icon(Icons.Rounded.Sensors, contentDescription = null, modifier = Modifier.size(18.dp)) }
        )

        Tab(
            selected = selectedTab == RadarSatelliteSubTab.RAINVIEWER_RADAR,
            onClick = { onSelectTab(RadarSatelliteSubTab.RAINVIEWER_RADAR) },
            text = {
                Text(
                    text = "Radar Global",
                    fontWeight = if (selectedTab == RadarSatelliteSubTab.RAINVIEWER_RADAR) FontWeight.Bold else FontWeight.Medium
                )
            },
            icon = { Icon(Icons.Rounded.Public, contentDescription = null, modifier = Modifier.size(18.dp)) }
        )

        Tab(
            selected = selectedTab == RadarSatelliteSubTab.METEOSAT,
            onClick = { onSelectTab(RadarSatelliteSubTab.METEOSAT) },
            text = {
                Text(
                    text = "Satelit (EUMETSAT / MTG)",
                    fontWeight = if (selectedTab == RadarSatelliteSubTab.METEOSAT) FontWeight.Bold else FontWeight.Medium
                )
            },
            icon = { Icon(Icons.Rounded.Layers, contentDescription = null, modifier = Modifier.size(18.dp)) }
        )
    }
}

// -------------------------------------------------------------------------
// Sub-View 1 & 2: Interactive Radar View (National + Individual Stations)
// -------------------------------------------------------------------------
@Composable
private fun InteractiveRadarCompositeView(
    frames: List<AnmRadarFrame>,
    currentIndex: Int,
    isPlaying: Boolean,
    opacity: Float,
    playbackSpeedMs: Long,
    isLoading: Boolean,
    selectedStation: RadarStationInfo,
    allStations: List<RadarStationInfo>,
    isDarkTheme: Boolean,
    onIndexChange: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onStep: (Boolean) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onSpeedChange: (Long) -> Unit,
    onSelectStation: (RadarStationInfo) -> Unit,
    onToggleFullscreen: () -> Unit,
    onOpenWeb: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val currentFrame = frames.getOrNull(currentIndex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Station Selector Chips Row
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selectează Radar / Stație Doppler:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                if (selectedStation.code != "COMPOSITE") {
                    Text(
                        text = "Rază: ${selectedStation.coverageRadiusKm} km",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF0284C7),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(allStations) { station ->
                    val isSelected = station.code == selectedStation.code
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectStation(station) },
                        label = {
                            Text(
                                text = if (station.code == "COMPOSITE") "🇷🇴 Mozaic Național" else "📡 ${station.name}",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Main Radar Screen Card (Interactive Leaflet Map with Real Base Tiles)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(390.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    // Leaflet Radar Map View
                    RadarMapView(
                        mode = RadarMapMode.ANM_RADAR,
                        currentRadarImageUrl = currentFrame?.imageUrl,
                        rainViewerTilePath = null,
                        opacity = opacity,
                        selectedStation = selectedStation,
                        allStations = allStations,
                        isDarkTheme = isDarkTheme,
                        onSelectStation = onSelectStation,
                        height = 390.dp,
                        onToggleFullscreen = onToggleFullscreen
                    )

                    // Top Floating Frame Timestamp & Station Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.76f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) Color(0xFF10B981) else Color(0xFFEAB308))
                            )
                            Text(
                                text = currentFrame?.timeString ?: if (isLoading) "Se încarcă…" else "Live ANM",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            if (frames.isNotEmpty()) {
                                Text(
                                    text = "(${currentIndex + 1}/${frames.size})",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Bottom Floating Active Station Name
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (selectedStation.code == "COMPOSITE") "România (Toată Țara)" else "${selectedStation.name} • ${selectedStation.band}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Radar Playback & Slider Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Frame scrubber slider
                    if (frames.size > 1) {
                        Slider(
                            value = currentIndex.toFloat(),
                            onValueChange = { onIndexChange(it.toInt()) },
                            valueRange = 0f..(frames.size - 1).toFloat(),
                            steps = frames.size - 2,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF0284C7),
                                activeTrackColor = Color(0xFF0284C7)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Player Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Step back
                        IconButton(
                            onClick = { onStep(false) },
                            enabled = frames.isNotEmpty()
                        ) {
                            Icon(Icons.Rounded.FastRewind, contentDescription = "Pas înapoi")
                        }

                        // Play / Pause Primary Button
                        Button(
                            onClick = onTogglePlay,
                            enabled = frames.isNotEmpty(),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) Color(0xFFF97316) else Color(0xFF0284C7)
                            ),
                            modifier = Modifier.size(54.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pauză" else "Redă",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Step forward
                        IconButton(
                            onClick = { onStep(true) },
                            enabled = frames.isNotEmpty()
                        ) {
                            Icon(Icons.Rounded.FastForward, contentDescription = "Pas înainte")
                        }
                    }

                    // Speed and Opacity Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Playback Speed Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Viteză:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            listOf(600L to "0.7x", 400L to "1.0x", 200L to "2.0x").forEach { (speed, label) ->
                                val isSelected = playbackSpeedMs == speed
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .clickable { onSpeedChange(speed) }
                                        .padding(1.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Opacity Control Button / Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Opacitate: ${(opacity * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Opacity Slider
                    Slider(
                        value = opacity,
                        onValueChange = onOpacityChange,
                        valueRange = 0.3f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF0284C7),
                            activeTrackColor = Color(0xFF0284C7)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Selected Station Technical Profile Card (When Individual Station is Active)
        if (selectedStation.code != "COMPOSITE") {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Stația Radar ${selectedStation.name} (${selectedStation.code})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${selectedStation.county} • ${selectedStation.locationName}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0284C7).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = selectedStation.band,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Metric Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecMetricBox(
                            title = "Coordonate",
                            value = selectedStation.coordinates,
                            modifier = Modifier.weight(1.2f)
                        )
                        SpecMetricBox(
                            title = "Altitudine",
                            value = "${selectedStation.elevationMeters} m",
                            modifier = Modifier.weight(0.8f)
                        )
                        SpecMetricBox(
                            title = "Rază Acoperire",
                            value = "${selectedStation.coverageRadiusKm} km",
                            modifier = Modifier.weight(0.9f)
                        )
                    }

                    Text(
                        text = selectedStation.descriptionRo,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // dBZ Reflectivity Legend Card
        RadarDbzLegendCard(lang = lang)

        // Web Link Button
        OutlinedButton(
            onClick = onOpenWeb,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Deschide Portalul Radar ANM (meteoromania.ro)")
        }
    }
}

// -------------------------------------------------------------------------
// Sub-View 3: Global Radar (RainViewer Open-Source API)
// -------------------------------------------------------------------------
@Composable
private fun RainViewerRadarView(
    frames: List<RainViewerRadarFrame>,
    currentIndex: Int,
    isPlaying: Boolean,
    opacity: Float,
    isLoading: Boolean,
    isDarkTheme: Boolean,
    onIndexChange: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onStep: (Boolean) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onToggleFullscreen: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val currentFrame = frames.getOrNull(currentIndex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanatory Card for Open-Source API
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0284C7).copy(alpha = 0.12f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Public,
                    contentDescription = null,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "Radar Global Mondial (RainViewer)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Scanări radar europene și globale în timp real cu prognoză advectivă nowcast pe harta interactivă.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Radar Player Card (Interactive Leaflet Map with Real Base Map & Tiles)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(390.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    // RainViewer Leaflet Tile View
                    RadarMapView(
                        mode = RadarMapMode.RAINVIEWER_RADAR,
                        currentRadarImageUrl = null,
                        rainViewerTilePath = currentFrame?.path,
                        opacity = opacity,
                        selectedStation = null,
                        allStations = emptyList(),
                        isDarkTheme = isDarkTheme,
                        onSelectStation = {},
                        height = 390.dp,
                        onToggleFullscreen = onToggleFullscreen
                    )

                    // Floating Time Info Badge
                    currentFrame?.let { frame ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.76f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (frame.isNowcast) Color(0xFF8B5CF6) else Color(0xFF10B981))
                                )
                                Text(
                                    text = frame.formattedTime,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (frame.isNowcast) "PROGNOZĂ" else "RADAR LIVE",
                                    color = if (frame.isNowcast) Color(0xFFC4B5FD) else Color(0xFF6EE7B7),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Slider
                    if (frames.size > 1) {
                        Slider(
                            value = currentIndex.toFloat(),
                            onValueChange = { onIndexChange(it.toInt()) },
                            valueRange = 0f..(frames.size - 1).toFloat(),
                            steps = frames.size - 2,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF0284C7),
                                activeTrackColor = Color(0xFF0284C7)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Playback Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onStep(false) }, enabled = frames.isNotEmpty()) {
                            Icon(Icons.Rounded.FastRewind, contentDescription = "Step Back")
                        }

                        Button(
                            onClick = onTogglePlay,
                            enabled = frames.isNotEmpty(),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) Color(0xFFF97316) else Color(0xFF0284C7)
                            ),
                            modifier = Modifier.size(52.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pauză" else "Redă",
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = { onStep(true) }, enabled = frames.isNotEmpty()) {
                            Icon(Icons.Rounded.FastForward, contentDescription = "Step Forward")
                        }
                    }

                    // Opacity Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Opacitate:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = opacity,
                            onValueChange = onOpacityChange,
                            valueRange = 0.3f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF0284C7),
                                activeTrackColor = Color(0xFF0284C7)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${(opacity * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Attribution
        Text(
            text = "Date furnizate prin RainViewer API v2.0 cu acoperire multi-radar europeană sincronizată.",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// -------------------------------------------------------------------------
// Sub-View 4: EUMETSAT / MTG Satellite (Sat24 Europe/Romania) & ANM Archive
// -------------------------------------------------------------------------
@Composable
private fun EumetsatSatelliteView(
    eumetsatFrames: List<EumetsatSatelliteFrame>,
    selectedEumetsatLayerMode: EumetsatLayerMode,
    currentEumetsatIndex: Int,
    isEumetsatPlaying: Boolean,
    isLoadingEumetsat: Boolean,
    channels: List<MeteosatChannelInfo>,
    selectedChannel: MeteosatChannelInfo,
    meteosatFrames: List<MeteosatFrame>,
    selectedMeteosatFrame: MeteosatFrame?,
    isLoadingMeteosat: Boolean,
    onSelectEumetsatLayer: (EumetsatLayerMode) -> Unit,
    onSelectEumetsatIndex: (Int) -> Unit,
    onToggleEumetsatPlay: () -> Unit,
    onStepEumetsat: (Boolean) -> Unit,
    onSelectChannel: (MeteosatChannelInfo) -> Unit,
    onSelectMeteosatFrame: (MeteosatFrame) -> Unit,
    onOpenFullScreen: (String, String) -> Unit,
    onOpenWeb: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val currentFrame = eumetsatFrames.getOrNull(currentEumetsatIndex)
    var isAnmArchiveExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Satellite Layer Mode Chips (Visible, Infrared, Night Microphysics, MTG)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Mod Satelit EUMETSAT / MTG (România):",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(EumetsatLayerMode.entries.toList()) { mode ->
                    val isSelected = mode == selectedEumetsatLayerMode
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectEumetsatLayer(mode) },
                        label = {
                            Text(
                                text = when (mode) {
                                    EumetsatLayerMode.VISIBLE -> "☀️ Vizibil (HRV / Zi)"
                                    EumetsatLayerMode.INFRARED -> "🌡️ Infra-Roșu (IR)"
                                    EumetsatLayerMode.NIGHT_MICROPHYSICS -> "🌙 Night Microphysics"
                                    EumetsatLayerMode.MTG -> "🛰️ MTG Multispectral"
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0D9488),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Layer Description Banner Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedEumetsatLayerMode.titleRo,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0D9488).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "LIVE • EUMETSAT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D9488)
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = selectedEumetsatLayerMode.descriptionRo,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Main Satellite Image Display Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.25f)
                        .background(Color(0xFF0B132B))
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentFrame != null) {
                        SubcomposeAsyncImage(
                            model = currentFrame.imageUrl,
                            contentDescription = "Imagine Satelit Eumetsat",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFF2DD4BF))
                                }
                            }
                        )

                        // Top Floating Frame Timestamp Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isEumetsatPlaying) Color(0xFF10B981) else Color(0xFFEAB308))
                                )
                                Text(
                                    text = "${currentFrame.formattedTime} • ${currentFrame.timeUtc}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (eumetsatFrames.isNotEmpty()) {
                                    Text(
                                        text = "(${currentEumetsatIndex + 1}/${eumetsatFrames.size})",
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Top Zoom / Full Screen Button
                        IconButton(
                            onClick = {
                                onOpenFullScreen(
                                    currentFrame.imageUrl,
                                    "${selectedEumetsatLayerMode.titleRo} - ${currentFrame.formattedTime}"
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ZoomOutMap,
                                contentDescription = "Full Screen",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else if (isLoadingEumetsat) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(color = Color(0xFF2DD4BF))
                            Text(
                                text = "Se descarcă imaginile satelitare EUMETSAT…",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Text(
                            text = "Nicio imagine satelitară disponibilă",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Satellite Player Controls (Scrubber & Buttons)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (eumetsatFrames.size > 1) {
                        Slider(
                            value = currentEumetsatIndex.toFloat(),
                            onValueChange = { onSelectEumetsatIndex(it.toInt()) },
                            valueRange = 0f..(eumetsatFrames.size - 1).toFloat(),
                            steps = eumetsatFrames.size - 2,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF0D9488),
                                activeTrackColor = Color(0xFF0D9488)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Player Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onStepEumetsat(false) },
                            enabled = eumetsatFrames.isNotEmpty()
                        ) {
                            Icon(Icons.Rounded.FastRewind, contentDescription = "Cadru anterior")
                        }

                        Button(
                            onClick = onToggleEumetsatPlay,
                            enabled = eumetsatFrames.isNotEmpty(),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEumetsatPlaying) Color(0xFFF97316) else Color(0xFF0D9488)
                            ),
                            modifier = Modifier.size(54.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (isEumetsatPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isEumetsatPlaying) "Pauză" else "Redă",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = { onStepEumetsat(true) },
                            enabled = eumetsatFrames.isNotEmpty()
                        ) {
                            Icon(Icons.Rounded.FastForward, contentDescription = "Cadru următor")
                        }
                    }

                    // Quick Jump Frame Hours Row
                    if (eumetsatFrames.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(eumetsatFrames.mapIndexed { idx, frame -> idx to frame }) { (idx, frame) ->
                                val isSelected = idx == currentEumetsatIndex
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF0D9488) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.clickable { onSelectEumetsatIndex(idx) }
                                ) {
                                    Text(
                                        text = frame.formattedTime,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Night Microphysics Interpretation & Color Legend
        if (selectedEumetsatLayerMode == EumetsatLayerMode.NIGHT_MICROPHYSICS) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ghid Interpretare Culori (Night Microphysics RGB):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEAB308))
                        )
                        Text(
                            text = "Galben / Verde-deschis: Nori joși și ceață densă de noapte (stratus)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF65A30D))
                        )
                        Text(
                            text = "Verde / Kaki: Nori de altitudine medie (altocumulus)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626))
                        )
                        Text(
                            text = "Roșu / Purpuriu: Nori înalți de gheață (cirrus, vârful furtunilor)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E3A8A))
                        )
                        Text(
                            text = "Albastru închis / Negru: Sol senin fără acoperire noroasă",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Classical ANM Meteosat-10 Channels Expander
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAnmArchiveExpanded = !isAnmArchiveExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Layers,
                            contentDescription = null,
                            tint = Color(0xFF0D9488),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Arhivă Canale ANM Meteosat-10 (Canal 1 - 7)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = if (isAnmArchiveExpanded) "Ascunde ▲" else "Arată ▼",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF0D9488),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (isAnmArchiveExpanded) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(channels) { channel ->
                            val isSelected = channel.tipNumber == selectedChannel.tipNumber
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectChannel(channel) },
                                label = {
                                    Text(
                                        text = channel.titleRo,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }

                    if (selectedMeteosatFrame != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.3f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                        ) {
                            SubcomposeAsyncImage(
                                model = selectedMeteosatFrame.imageUrl,
                                contentDescription = "ANM Meteosat Frame",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Web Link Button
        OutlinedButton(
            onClick = onOpenWeb,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Vezi Satelit Live Sat24 România")
        }
    }
}

// -------------------------------------------------------------------------
// Reflectivity dBZ Legend Card
// -------------------------------------------------------------------------
@Composable
private fun RadarDbzLegendCard(
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "${Translations.get("radar_legend", lang)} (Scară Intensitate Precipitații dBZ):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Continuous Gradient Legend Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF00E4FF), // 5 dBZ
                                Color(0xFF00A2FF), // 15 dBZ
                                Color(0xFF00E100), // 25 dBZ
                                Color(0xFF009600), // 35 dBZ
                                Color(0xFFFFFF00), // 42 dBZ
                                Color(0xFFFF9900), // 48 dBZ
                                Color(0xFFFF0000), // 55 dBZ
                                Color(0xFFCC0000), // 60 dBZ
                                Color(0xFFFF00FF), // 65 dBZ
                                Color(0xFFFFFFFF)  // 70+ dBZ Hail
                            )
                        )
                    )
            )

            // Discrete Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    "5 dBZ" to "Burniță",
                    "25 dBZ" to "Ploaie",
                    "42 dBZ" to "Aversă",
                    "55 dBZ" to "Furtună",
                    "65+ dBZ" to "Grindină"
                ).forEach { (dbz, label) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dbz,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Helper Specification Metric Box
// -------------------------------------------------------------------------
@Composable
private fun SpecMetricBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// -------------------------------------------------------------------------
// Full-Screen Pinch-to-Zoom Dialog for Meteosat
// -------------------------------------------------------------------------
@Composable
private fun RadarInteractiveZoomDialog(
    imageUrl: String,
    title: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Interactive Pan & Zoom Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 6f)
                            if (scale > 1f) {
                                offsetX += pan.x
                                offsetY += pan.y
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                )
            }

            // Top Floating Bar with Title & Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            // Reset zoom indicator if zoomed in
            if (scale > 1.05f) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .clickable {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ZoomIn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Zoom: ${(scale * 100).toInt()}% • Atinge pentru resetare",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
