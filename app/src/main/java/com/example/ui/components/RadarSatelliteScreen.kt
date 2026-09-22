package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Speed
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
import com.example.model.MeteosatChannelInfo
import com.example.model.MeteosatFrame
import com.example.model.RadarSatelliteSubTab
import com.example.model.RadarStationFileItem
import com.example.model.RadarStationInfo
import com.example.model.RainViewerRadarFrame
import com.example.model.Translations
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
    val stationFiles by viewModel.stationFiles.collectAsState()
    val isLoadingStationFiles by viewModel.isLoadingStationFiles.collectAsState()

    val selectedChannel by viewModel.selectedMeteosatChannel.collectAsState()
    val meteosatFrames by viewModel.meteosatFrames.collectAsState()
    val selectedMeteosatFrame by viewModel.selectedMeteosatFrame.collectAsState()
    val isLoadingMeteosat by viewModel.isLoadingMeteosat.collectAsState()

    val rainViewerFrames by viewModel.rainViewerFrames.collectAsState()
    val currentRainViewerIndex by viewModel.currentRainViewerIndex.collectAsState()
    val isRainViewerPlaying by viewModel.isRainViewerPlaying.collectAsState()
    val isLoadingRainViewer by viewModel.isLoadingRainViewer.collectAsState()

    val context = LocalContext.current
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }
    var fullScreenTitle by remember { mutableStateOf("") }

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
                delay(500)
                viewModel.stepRainViewerFrame(forward = true)
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
                        RadarSatelliteSubTab.NATIONAL_RADAR -> viewModel.loadNationalRadar()
                        RadarSatelliteSubTab.RADAR_STATIONS -> viewModel.loadStationFiles(selectedStation.code)
                        RadarSatelliteSubTab.RAINVIEWER_RADAR -> viewModel.loadRainViewerFrames()
                        RadarSatelliteSubTab.METEOSAT -> viewModel.loadMeteosatFrames(selectedChannel.tipNumber)
                    }
                },
                lang = lang
            )
        }

        // --- Sub-Tab Navigation Bar ---
        item {
            RadarSubTabBar(
                selectedTab = subTab,
                onSelectTab = { viewModel.selectRadarSubTab(it) },
                lang = lang
            )
        }

        // --- Active Sub-Tab Content ---
        when (subTab) {
            RadarSatelliteSubTab.NATIONAL_RADAR -> {
                item {
                    NationalRadarView(
                        frames = nationalFrames,
                        currentIndex = currentFrameIndex,
                        isPlaying = isPlaying,
                        opacity = radarOpacity,
                        playbackSpeedMs = playbackSpeedMs,
                        isLoading = isLoadingRadar,
                        onIndexChange = { viewModel.setRadarFrameIndex(it) },
                        onTogglePlay = { viewModel.toggleRadarPlayback() },
                        onStep = { viewModel.stepRadarFrame(it) },
                        onOpacityChange = { viewModel.setRadarOpacity(it) },
                        onSpeedChange = { viewModel.setRadarPlaybackSpeed(it) },
                        onOpenFullScreen = { url, title ->
                            fullScreenImageUrl = url
                            fullScreenTitle = title
                        },
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

            RadarSatelliteSubTab.RADAR_STATIONS -> {
                item {
                    RadarStationsView(
                        stations = viewModel.radarStations,
                        selectedStation = selectedStation,
                        stationFiles = stationFiles,
                        isLoadingFiles = isLoadingStationFiles,
                        onSelectStation = { viewModel.selectRadarStation(it) },
                        onOpenWeb = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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
                        isLoading = isLoadingRainViewer,
                        onIndexChange = { viewModel.setRainViewerIndex(it) },
                        onTogglePlay = { viewModel.toggleRainViewerPlayback() },
                        onStep = { viewModel.stepRainViewerFrame(it) },
                        onOpenFullScreen = { url, title ->
                            fullScreenImageUrl = url
                            fullScreenTitle = title
                        },
                        lang = lang
                    )
                }
            }

            RadarSatelliteSubTab.METEOSAT -> {
                item {
                    MeteosatSatelliteView(
                        channels = viewModel.meteosatChannels,
                        selectedChannel = selectedChannel,
                        frames = meteosatFrames,
                        selectedFrame = selectedMeteosatFrame,
                        isLoading = isLoadingMeteosat,
                        onSelectChannel = { viewModel.selectMeteosatChannel(it) },
                        onSelectFrame = { viewModel.selectMeteosatFrame(it) },
                        onOpenFullScreen = { url, title ->
                            fullScreenImageUrl = url
                            fullScreenTitle = title
                        },
                        onOpenWeb = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.meteoromania.ro/sateliti/index.php?tip=${selectedChannel.tipNumber}")
                            )
                            context.startActivity(intent)
                        },
                        lang = lang
                    )
                }
            }
        }
    }

    // Fullscreen Zoom Dialog
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
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "ANM OpenData • METEOSAT • Doppler",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(38.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)
                        .testTag("radar_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
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
                    text = Translations.get("tab_national_radar", lang),
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
                    text = Translations.get("tab_radar_stations", lang),
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
                    text = Translations.get("tab_rainviewer", lang),
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
                    text = Translations.get("tab_satellite", lang),
                    fontWeight = if (selectedTab == RadarSatelliteSubTab.METEOSAT) FontWeight.Bold else FontWeight.Medium
                )
            },
            icon = { Icon(Icons.Rounded.Layers, contentDescription = null, modifier = Modifier.size(18.dp)) }
        )
    }
}

// -------------------------------------------------------------------------
// Sub-View 1: National Radar Composite
// -------------------------------------------------------------------------
@Composable
private fun NationalRadarView(
    frames: List<AnmRadarFrame>,
    currentIndex: Int,
    isPlaying: Boolean,
    opacity: Float,
    playbackSpeedMs: Long,
    isLoading: Boolean,
    onIndexChange: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onStep: (Boolean) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onSpeedChange: (Long) -> Unit,
    onOpenFullScreen: (String, String) -> Unit,
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
        // Main Radar Screen Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Interactive Radar Canvas Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.22f)
                        .background(Color(0xFF0F172A))
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentFrame != null) {
                        SubcomposeAsyncImage(
                            model = currentFrame.imageUrl,
                            contentDescription = "Radar Frame ${currentFrame.timeString}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(alpha = opacity),
                            loading = {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        )
                    } else if (isLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = Color(0xFF38BDF8))
                            Text(
                                text = "Se încarcă radarul național ANM…",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Text(
                            text = "Date radar indisponibile momentan",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Top Floating Time Badge
                    currentFrame?.let { frame ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.72f),
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
                                    text = frame.timeString,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Top Right Fullscreen Button
                    currentFrame?.let { frame ->
                        IconButton(
                            onClick = { onOpenFullScreen(frame.imageUrl, "Radar Național ANM - ${frame.timeString}") },
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
                    }

                    // Bottom Right Frame Counter
                    if (frames.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "${currentIndex + 1} / ${frames.size}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
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
                                text = Translations.get("radar_speed", lang),
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
                                text = "${(opacity * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // dBZ Reflectivity Legend Card
        RadarDbzLegendCard(lang = lang)

        // Radar Info & Open ANM Link Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Despre Mozaicul Radar Național",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    text = "Mozaicul radar ANM combină scanările volumetrice de la toate cele 7 radare meteorologice naționale Doppler (WSR-98D și C-Band) într-o singură proiecție compozită la nivel de țară. Datele se actualizează automat la fiecare 10 minute.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                )

                OutlinedButton(
                    onClick = onOpenWeb,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deschide Harta Radar pe meteoromania.ro")
                }

                val context = LocalContext.current
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://opendata.meteoromania.ro/radar/"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Depozit OpenData Radar ANM (opendata.meteoromania.ro/radar/)")
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-View 2: 7 National Radar Stations (OpenData)
// -------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RadarStationsView(
    stations: List<RadarStationInfo>,
    selectedStation: RadarStationInfo,
    stationFiles: List<RadarStationFileItem>,
    isLoadingFiles: Boolean,
    onSelectStation: (RadarStationInfo) -> Unit,
    onOpenWeb: (String) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val openDataRootUrl = "https://opendata.meteoromania.ro/radar/"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dedicated OpenData Portal Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Sensors,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Translations.get("opendata_portal_title", lang),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = Translations.get("opendata_portal_subtitle", lang),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Official URL container pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = openDataRootUrl,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Action buttons: Open Portal + Copy Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(openDataRootUrl))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Deschide Portalul",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(openDataRootUrl))
                            Toast.makeText(
                                context,
                                Translations.get("link_copied", lang),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Translations.get("copy_link", lang),
                            fontSize = 13.sp
                        )
                    }
                }

                // Quick server subdirectories
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Directoare disponibile în /radar/ (click pentru selectare):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val dirs = listOf("COMPOSITE", "BAR", "BOB", "BUC", "CRA", "MED", "ORA", "TIM")
                        dirs.forEach { dir ->
                            val isSelected = selectedStation.code == dir
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                modifier = Modifier.clickable {
                                    stations.find { it.code == dir }?.let { onSelectStation(it) }
                                }
                            ) {
                                Text(
                                    text = "$dir/",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Horizontal Stations Selector
        Text(
            text = "Selectează Stația Radar:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(stations) { station ->
                val isSelected = station.code == selectedStation.code
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectStation(station) },
                    label = {
                        Text(
                            text = "${station.code} • ${station.name}",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (station.code == "COMPOSITE") Icons.Rounded.Public else Icons.Rounded.Radar,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )
            }
        }

        // Station Details Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Station title & county
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Stația ${selectedStation.name} (${selectedStation.code})",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${selectedStation.county} • ${selectedStation.locationName}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
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

                // Grid specifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecMetricBox(
                        title = "Coordonate",
                        value = selectedStation.coordinates,
                        modifier = Modifier.weight(1f)
                    )
                    SpecMetricBox(
                        title = "Altitudine",
                        value = "${selectedStation.elevationMeters} m",
                        modifier = Modifier.weight(0.7f)
                    )
                    SpecMetricBox(
                        title = "Rază Acoperire",
                        value = "${selectedStation.coverageRadiusKm} km",
                        modifier = Modifier.weight(0.9f)
                    )
                }

                // Description
                Text(
                    text = selectedStation.descriptionRo,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                )

                // Parameters flow row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Produse Volumetrice Disponibile:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        selectedStation.parameters.forEach { param ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = param,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Open OpenData Folder Button
                Button(
                    onClick = { onOpenWeb(selectedStation.openDataUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(Icons.Rounded.Folder, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Deschide Depozit OpenData ANM (${selectedStation.code})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Real-time Station Files Inspector
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fișiere HDF5 Recente (${selectedStation.code})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    if (isLoadingFiles) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    }
                }

                if (stationFiles.isEmpty() && !isLoadingFiles) {
                    Text(
                        text = "Directoriul OpenData este accesibil la ${selectedStation.openDataUrl}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                } else {
                    stationFiles.forEach { file ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenWeb(file.downloadUrl) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = file.filename,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${file.timestampStr} • ${file.fileSizeStr}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF0284C7).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = file.productType,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0284C7)
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-View 3: RainViewer Open-Source Radar
// -------------------------------------------------------------------------
@Composable
private fun RainViewerRadarView(
    frames: List<RainViewerRadarFrame>,
    currentIndex: Int,
    isPlaying: Boolean,
    isLoading: Boolean,
    onIndexChange: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onStep: (Boolean) -> Unit,
    onOpenFullScreen: (String, String) -> Unit,
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
                        text = "Radar Global Open-Source (RainViewer)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Integrare API globală cu scanări trecute (Past) și prognoză radar prin advecție atmosferică (Nowcast).",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Radar Player Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Info on current frame
                currentFrame?.let { frame ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (frame.isNowcast) Color(0xFF8B5CF6) else Color(0xFF10B981))
                            )
                            Text(
                                text = frame.formattedTime,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (frame.isNowcast) Color(0xFF8B5CF6).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (frame.isNowcast) "PROGNOZĂ NOWCAST" else "OBSERVAȚIE RADAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (frame.isNowcast) Color(0xFF8B5CF6) else Color(0xFF10B981)
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

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

                // Controls
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
                        modifier = Modifier.size(50.dp),
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

                // Open-source attribution
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
    }
}

// -------------------------------------------------------------------------
// Sub-View 4: METEOSAT-10 Satellite
// -------------------------------------------------------------------------
@Composable
private fun MeteosatSatelliteView(
    channels: List<MeteosatChannelInfo>,
    selectedChannel: MeteosatChannelInfo,
    frames: List<MeteosatFrame>,
    selectedFrame: MeteosatFrame?,
    isLoading: Boolean,
    onSelectChannel: (MeteosatChannelInfo) -> Unit,
    onSelectFrame: (MeteosatFrame) -> Unit,
    onOpenFullScreen: (String, String) -> Unit,
    onOpenWeb: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Channel Selector Chips
        Text(
            text = Translations.get("satellite_channels", lang) + ":",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

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
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0D9488),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Channel Spec & Description Card
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
                        text = selectedChannel.titleRo,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0D9488).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Rezoluție: ${selectedChannel.resolution}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D9488)
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = selectedChannel.descriptionRo,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Text(
                    text = "Bandă spectrală: ${selectedChannel.spectrum}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Satellite Image Display Card
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
                    if (selectedFrame != null) {
                        SubcomposeAsyncImage(
                            model = selectedFrame.imageUrl,
                            contentDescription = "Meteosat Frame",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFF2DD4BF))
                                }
                            }
                        )

                        // Top UTC Time badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = selectedFrame.timeUtc,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        // Top Zoom Button
                        IconButton(
                            onClick = {
                                onOpenFullScreen(
                                    selectedFrame.imageUrl,
                                    "${selectedChannel.titleRo} - ${selectedFrame.timeUtc}"
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
                    } else if (isLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(color = Color(0xFF2DD4BF))
                            Text(
                                text = "Se descarcă datele METEOSAT-10…",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Text(
                            text = "Nicio imagine disponibilă",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Available Observation Hours Row
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = Translations.get("satellite_time", lang) + ":",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(frames) { frame ->
                            val isSelected = frame.imageUrl == selectedFrame?.imageUrl
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF0D9488) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.clickable { onSelectFrame(frame) }
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

        // Web Link Button
        OutlinedButton(
            onClick = onOpenWeb,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Vezi Galeria METEOSAT pe meteoromania.ro")
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
                text = "${Translations.get("radar_legend", lang)} (Scară Intensitate Precipitații):",
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
// Full-Screen Pinch-to-Zoom Dialog
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
