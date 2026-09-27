package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.model.RadarStationInfo
import kotlin.math.roundToInt

// Bounds for ANM Composite Radar in Mercator projection
// SW: 42.007679 N, 17.972679 E
// NE: 49.162879 N, 31.476679 E
private const val MIN_LAT = 42.007679f
private const val MAX_LAT = 49.162879f
private const val MIN_LON = 17.972679f
private const val MAX_LON = 31.476679f

private data class MapCity(val name: String, val lat: Float, val lon: Float, val isMajor: Boolean = false)

private val ROMANIAN_CITIES = listOf(
    MapCity("București", 44.432f, 26.104f, true),
    MapCity("Cluj-Napoca", 46.771f, 23.600f, true),
    MapCity("Timișoara", 45.754f, 21.226f, true),
    MapCity("Iași", 47.158f, 27.601f, true),
    MapCity("Constanța", 44.179f, 28.650f, true),
    MapCity("Craiova", 44.330f, 23.795f, true),
    MapCity("Brașov", 45.658f, 25.601f, true),
    MapCity("Galați", 45.435f, 28.055f, false),
    MapCity("Oradea", 47.046f, 21.919f, true),
    MapCity("Sibiu", 45.798f, 24.126f, false),
    MapCity("Suceava", 47.651f, 26.255f, false),
    MapCity("Bacău", 46.567f, 26.914f, false),
    MapCity("Pitești", 44.856f, 24.869f, false),
    MapCity("Arad", 46.186f, 21.312f, false),
    MapCity("Baia Mare", 47.660f, 23.580f, false),
    MapCity("Târgu Mureș", 46.542f, 24.557f, false),
    MapCity("Buzău", 45.150f, 26.833f, false),
    MapCity("Botoșani", 47.744f, 26.666f, false),
    MapCity("Satu Mare", 47.790f, 22.890f, false),
    MapCity("Râmnicu Vâlcea", 45.105f, 24.369f, false)
)

// Geo coordinates of Romania outline polygon
private val ROMANIA_BORDER_COORDS = listOf(
    Pair(48.26f, 22.85f), Pair(48.01f, 23.47f), Pair(47.92f, 24.47f),
    Pair(47.98f, 25.05f), Pair(48.22f, 26.33f), Pair(48.15f, 26.75f),
    Pair(48.26f, 27.14f), Pair(47.95f, 27.35f), Pair(47.38f, 27.60f),
    Pair(46.68f, 28.15f), Pair(46.15f, 28.18f), Pair(45.45f, 28.23f),
    Pair(45.35f, 28.80f), Pair(45.18f, 29.65f), Pair(44.82f, 29.58f),
    Pair(44.38f, 28.85f), Pair(43.85f, 28.58f), Pair(43.68f, 27.95f),
    Pair(44.02f, 27.35f), Pair(44.00f, 26.65f), Pair(43.88f, 25.95f),
    Pair(43.62f, 25.22f), Pair(43.68f, 24.55f), Pair(43.75f, 23.85f),
    Pair(43.98f, 22.95f), Pair(44.22f, 22.65f), Pair(44.62f, 22.58f),
    Pair(44.60f, 21.65f), Pair(44.85f, 21.35f), Pair(45.22f, 21.38f),
    Pair(45.75f, 20.65f), Pair(46.15f, 21.22f), Pair(46.55f, 21.45f),
    Pair(47.05f, 21.75f), Pair(47.58f, 22.15f), Pair(47.95f, 22.88f),
    Pair(48.26f, 22.85f)
)

/**
 * 100% Native Jetpack Compose Radar Map View
 * Renders Romania's geographical relief, borders, cities, Doppler stations, range circles,
 * and overlays ANM's live radar reflectivity frames with smooth gestures and zero webview dependencies.
 */
@Composable
fun NativeRadarMapView(
    currentRadarImageUrl: String?,
    opacity: Float,
    selectedStation: RadarStationInfo?,
    allStations: List<RadarStationInfo>,
    isDarkTheme: Boolean,
    onSelectStation: (RadarStationInfo) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 390.dp,
    isFullscreen: Boolean = false,
    onToggleFullscreen: (() -> Unit)? = null
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    // Palette
    val bgColor = if (isDarkTheme) Color(0xFF0D1527) else Color(0xFFE2E8F0)
    val seaColor = if (isDarkTheme) Color(0xFF09101F) else Color(0xFFBAE6FD)
    val landColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val mountainColor = if (isDarkTheme) Color(0xFF172554).copy(alpha = 0.7f) else Color(0xFFCBD5E1)
    val borderColor = if (isDarkTheme) Color(0xFF38BDF8).copy(alpha = 0.65f) else Color(0xFF0284C7).copy(alpha = 0.75f)
    val danubeColor = if (isDarkTheme) Color(0xFF0284C7).copy(alpha = 0.8f) else Color(0xFF0369A1)
    val gridColor = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.04f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isFullscreen) Modifier.fillMaxSize() else Modifier.height(height))
            .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 18.dp))
            .background(bgColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(if (isFullscreen) 0.dp else 18.dp)
            )
            .clipToBounds()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 4.5f)
                    val maxOffset = (scale - 1f) * 400f
                    offsetX = (offsetX + pan.x).coerceIn(-maxOffset, maxOffset)
                    offsetY = (offsetY + pan.y).coerceIn(-maxOffset, maxOffset)
                }
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Helper function to project Lat/Lon to Canvas Pixels inside bounds
        fun project(lat: Float, lon: Float): Offset {
            val normX = (lon - MIN_LON) / (MAX_LON - MIN_LON)
            val normY = (MAX_LAT - lat) / (MAX_LAT - MIN_LAT)
            return Offset(normX * widthPx, normY * heightPx)
        }

        // Inner Zoomable & Pannable Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
        ) {
            // 1. Base Map Layer (Geographical relief & Romania vector outline)
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Ocean / Sea Background
                drawRect(color = seaColor)

                // Grid Lines (Latitude / Longitude)
                val latSteps = listOf(43f, 44f, 45f, 46f, 47f, 48f)
                val lonSteps = listOf(20f, 22f, 24f, 26f, 28f, 30f)
                latSteps.forEach { lat ->
                    val y = project(lat, MIN_LON).y
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(widthPx, y),
                        strokeWidth = 1f
                    )
                }
                lonSteps.forEach { lon ->
                    val x = project(MIN_LAT, lon).x
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, heightPx),
                        strokeWidth = 1f
                    )
                }

                // Romania Landmass Polygon
                if (ROMANIA_BORDER_COORDS.isNotEmpty()) {
                    val borderPath = Path()
                    val first = ROMANIA_BORDER_COORDS.first()
                    val p0 = project(first.first, first.second)
                    borderPath.moveTo(p0.x, p0.y)
                    for (i in 1 until ROMANIA_BORDER_COORDS.size) {
                        val c = ROMANIA_BORDER_COORDS[i]
                        val pt = project(c.first, c.second)
                        borderPath.lineTo(pt.x, pt.y)
                    }
                    borderPath.close()

                    // Fill Landmass
                    drawPath(
                        path = borderPath,
                        brush = Brush.radialGradient(
                            colors = listOf(landColor, landColor.copy(alpha = 0.92f)),
                            center = project(45.94f, 24.96f),
                            radius = widthPx * 0.45f
                        )
                    )

                    // Draw Carpathian Mountains Arch (Carpații Orientali & Meridionali)
                    val carpathianPath = Path()
                    val cStart = project(47.8f, 25.2f)
                    carpathianPath.moveTo(cStart.x, cStart.y)
                    val cPoints = listOf(
                        project(47.2f, 25.6f),
                        project(46.5f, 25.8f),
                        project(45.8f, 26.2f),
                        project(45.4f, 25.5f),
                        project(45.3f, 24.5f),
                        project(45.3f, 23.5f),
                        project(45.1f, 22.7f)
                    )
                    cPoints.forEach { pt -> carpathianPath.lineTo(pt.x, pt.y) }
                    drawPath(
                        path = carpathianPath,
                        color = mountainColor,
                        style = Stroke(width = 18f * (widthPx / 600f), cap = StrokeCap.Round)
                    )

                    // Apuseni Mountains
                    drawCircle(
                        color = mountainColor,
                        radius = 22f * (widthPx / 600f),
                        center = project(46.55f, 23.0f)
                    )

                    // Border outline
                    drawPath(
                        path = borderPath,
                        color = borderColor,
                        style = Stroke(
                            width = 2.2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 4f), 0f)
                        )
                    )
                }

                // Danube River (Dunărea)
                val danubeCoords = listOf(
                    Pair(44.62f, 21.65f), Pair(44.60f, 22.58f), Pair(44.22f, 22.65f),
                    Pair(43.98f, 22.95f), Pair(43.75f, 23.85f), Pair(43.68f, 24.55f),
                    Pair(43.62f, 25.22f), Pair(43.88f, 25.95f), Pair(44.02f, 27.35f),
                    Pair(44.35f, 28.05f), Pair(45.35f, 28.15f), Pair(45.45f, 28.75f),
                    Pair(45.18f, 29.65f)
                )
                val danubePath = Path()
                val d0 = project(danubeCoords.first().first, danubeCoords.first().second)
                danubePath.moveTo(d0.x, d0.y)
                for (i in 1 until danubeCoords.size) {
                    val p = project(danubeCoords[i].first, danubeCoords[i].second)
                    danubePath.lineTo(p.x, p.y)
                }
                drawPath(
                    path = danubePath,
                    color = danubeColor,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )

                // Black Sea Label Area
                val blackSeaCenter = project(43.8f, 29.8f)
                drawCircle(
                    color = seaColor,
                    radius = 35f,
                    center = blackSeaCenter
                )
            }

            // 2. Coil Live Radar Reflectivity Overlay
            // Renders the transparent ANM composite radar PNG aligned with the coordinates
            if (!currentRadarImageUrl.isNullOrEmpty()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentRadarImageUrl)
                        .crossfade(250)
                        .build(),
                    contentDescription = "Radar ANM Doppler",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = opacity
                        },
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color(0xFF0284C7),
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                )
            }

            // 3. Vector Overlay: Doppler Radar Rings for Active Station
            Canvas(modifier = Modifier.fillMaxSize()) {
                val station = selectedStation
                if (station != null && station.code != "COMPOSITE" && station.latitude > 0.0) {
                    val center = project(station.latitude.toFloat(), station.longitude.toFloat())

                    // Scale km to canvas px (Romania is ~700 km wide)
                    val kmToPx = (widthPx * 0.55f) / 500f

                    val ringsKm = listOf(50f, 100f, 150f, 200f, station.coverageRadiusKm.toFloat())
                    ringsKm.forEach { distKm ->
                        val rPx = distKm * kmToPx
                        val isMax = distKm == station.coverageRadiusKm.toFloat()
                        drawCircle(
                            color = if (isMax) Color(0xFF0284C7) else Color(0xFF0284C7).copy(alpha = 0.5f),
                            radius = rPx,
                            center = center,
                            style = Stroke(
                                width = if (isMax) 2f else 1f,
                                pathEffect = if (isMax) PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f) else PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f)
                            )
                        )
                    }

                    // Azimuth Cardinal Crosshairs
                    val maxR = station.coverageRadiusKm * kmToPx
                    drawLine(
                        color = Color(0xFF0284C7).copy(alpha = 0.35f),
                        start = Offset(center.x - maxR, center.y),
                        end = Offset(center.x + maxR, center.y),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color(0xFF0284C7).copy(alpha = 0.35f),
                        start = Offset(center.x, center.y - maxR),
                        end = Offset(center.x, center.y + maxR),
                        strokeWidth = 1f
                    )

                    // Animated Pulse Beacon
                    val pulseR = (station.coverageRadiusKm * kmToPx) * pulseRadius
                    drawCircle(
                        color = Color(0xFF38BDF8).copy(alpha = pulseAlpha * 0.45f),
                        radius = pulseR,
                        center = center,
                        style = Stroke(width = 2.5f)
                    )

                    // Center Station Dot
                    drawCircle(
                        color = Color(0xFFF97316),
                        radius = 6.5f,
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5f,
                        center = center
                    )
                }
            }

            // 4. Cities and Station Markers Overlay
            // Romanian Cities Markers
            ROMANIAN_CITIES.forEach { city ->
                val pos = project(city.lat, city.lon)
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (pos.x - 24.dp.toPx()).roundToInt(),
                                (pos.y - 12.dp.toPx()).roundToInt()
                            )
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(if (city.isMajor) 4.5.dp else 3.5.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Color.White.copy(alpha = 0.85f) else Color(0xFF1E293B))
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = city.name,
                            fontSize = if (city.isMajor) 9.sp else 8.sp,
                            fontWeight = if (city.isMajor) FontWeight.Bold else FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                        )
                    }
                }
            }

            // The 7 Doppler Radar Stations Icons
            allStations.filter { it.code != "COMPOSITE" && it.latitude > 0.0 }.forEach { st ->
                val pos = project(st.latitude.toFloat(), st.longitude.toFloat())
                val isSelected = selectedStation?.code == st.code

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (pos.x - 14.dp.toPx()).roundToInt(),
                                (pos.y - 14.dp.toPx()).roundToInt()
                            )
                        }
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFF97316) else Color(0xFF0284C7))
                        .border(
                            width = if (isSelected) 2.dp else 1.5.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onSelectStation(st)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = st.code,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // 5. Floating Controls Overlay (Top Right: Zoom In, Zoom Out, Recenter, Fullscreen)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                // Zoom In
                IconButton(
                    onClick = {
                        scale = (scale * 1.35f).coerceAtMost(4.5f)
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Rounded.ZoomIn,
                        contentDescription = "Mărește",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Zoom Out
                IconButton(
                    onClick = {
                        scale = (scale / 1.35f).coerceAtLeast(1f)
                        if (scale == 1f) {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Rounded.ZoomOut,
                        contentDescription = "Micșorează",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Recenter
                IconButton(
                    onClick = {
                        val st = selectedStation
                        if (st != null && st.code != "COMPOSITE" && st.latitude > 0.0) {
                            scale = 2.2f
                            val centerNormX = (st.longitude.toFloat() - MIN_LON) / (MAX_LON - MIN_LON)
                            val centerNormY = (MAX_LAT - st.latitude.toFloat()) / (MAX_LAT - MIN_LAT)
                            offsetX = (0.5f - centerNormX) * widthPx * scale
                            offsetY = (0.5f - centerNormY) * heightPx * scale
                        } else {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        }
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Rounded.MyLocation,
                        contentDescription = "Recentrare",
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Reset Zoom
                if (scale > 1.05f) {
                    IconButton(
                        onClick = {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            Icons.Rounded.RestartAlt,
                            contentDescription = "Reset Zoom",
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                // Fullscreen toggle
                if (onToggleFullscreen != null) {
                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen,
                            contentDescription = if (isFullscreen) "Ieși din ecran complet" else "Ecran complet",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Bottom Left Mode Label
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.72f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Text(
                    text = "Radar Nativ HD • România (${(scale * 100).toInt() / 100f}x)",
                    color = Color.White,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
