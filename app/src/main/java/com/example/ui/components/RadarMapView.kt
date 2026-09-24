package com.example.ui.components

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.RadarStationInfo

enum class RadarMapMode {
    ANM_RADAR,
    RAINVIEWER_RADAR
}

class RadarWebInterface(
    private val onStationClicked: (String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onStationClick(stationCode: String) {
        mainHandler.post {
            onStationClicked(stationCode)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RadarMapView(
    mode: RadarMapMode,
    currentRadarImageUrl: String?,
    rainViewerTilePath: String?,
    opacity: Float,
    selectedStation: RadarStationInfo?,
    allStations: List<RadarStationInfo>,
    isDarkTheme: Boolean,
    onSelectStation: (RadarStationInfo) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 380.dp,
    isFullscreen: Boolean = false,
    onToggleFullscreen: (() -> Unit)? = null
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    val radarInterface = remember(allStations) {
        RadarWebInterface { stationCode ->
            val match = allStations.find { it.code == stationCode }
            if (match != null) {
                onSelectStation(match)
            }
        }
    }

    val stationsJson = remember(allStations) {
        val filtered = allStations.filter { it.code != "COMPOSITE" && it.latitude > 0.0 }
        filtered.joinToString(prefix = "[", postfix = "]") { s ->
            """{"code":"${s.code}","name":"${s.name}","lat":${s.latitude},"lon":${s.longitude},"radius":${s.coverageRadiusKm}}"""
        }
    }

    // Effect to update ANM Radar Overlay when frame or opacity changes
    LaunchedEffect(currentRadarImageUrl, opacity, mode, isMapLoaded) {
        if (!isMapLoaded) return@LaunchedEffect
        val wv = webViewRef ?: return@LaunchedEffect
        if (mode == RadarMapMode.ANM_RADAR) {
            val url = currentRadarImageUrl ?: ""
            val js = "if (window.setRadarOverlay) { window.setRadarOverlay('$url', $opacity); }"
            wv.evaluateJavascript(js, null)
        }
    }

    // Effect to update RainViewer layer when path changes
    LaunchedEffect(rainViewerTilePath, opacity, mode, isMapLoaded) {
        if (!isMapLoaded) return@LaunchedEffect
        val wv = webViewRef ?: return@LaunchedEffect
        if (mode == RadarMapMode.RAINVIEWER_RADAR) {
            val path = rainViewerTilePath ?: ""
            val js = "if (window.setRainViewerTile) { window.setRainViewerTile('$path', $opacity); }"
            wv.evaluateJavascript(js, null)
        }
    }

    // Effect to update selected station focus & range rings
    LaunchedEffect(selectedStation?.code, mode, isMapLoaded) {
        if (!isMapLoaded) return@LaunchedEffect
        val wv = webViewRef ?: return@LaunchedEffect
        if (mode == RadarMapMode.ANM_RADAR) {
            val station = selectedStation
            val js = if (station != null && station.code != "COMPOSITE") {
                "if (window.focusStation) { window.focusStation('${station.code}', ${station.latitude}, ${station.longitude}, ${station.coverageRadiusKm}); }"
            } else {
                "if (window.focusNational) { window.focusNational(); }"
            }
            wv.evaluateJavascript(js, null)
        }
    }

    // Effect to switch mode
    LaunchedEffect(mode, isMapLoaded) {
        if (!isMapLoaded) return@LaunchedEffect
        val wv = webViewRef ?: return@LaunchedEffect
        val modeStr = if (mode == RadarMapMode.RAINVIEWER_RADAR) "rainviewer" else "anm"
        wv.evaluateJavascript("if (window.setMode) { window.setMode('$modeStr'); }", null)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isFullscreen) Modifier.fillMaxSize() else Modifier.height(height))
            .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp))
            .background(if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFE2E8F0))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp)
            )
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(true)
                        builtInZoomControls = false
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        allowFileAccess = true
                        allowContentAccess = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d("RadarLeaflet", "${consoleMessage?.message()} [Line ${consoleMessage?.lineNumber()}]")
                            return true
                        }
                    }

                    addJavascriptInterface(radarInterface, "AndroidRadar")

                    // Disallow parent LazyColumn scrolling when touching the map
                    setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                v.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                v.parent?.requestDisallowInterceptTouchEvent(false)
                            }
                        }
                        false
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isMapLoaded = true

                            // Initialize map
                            view?.evaluateJavascript("if (window.initMap) { window.initMap($isDarkTheme, $stationsJson); }", null)

                            // Set initial mode
                            val modeStr = if (mode == RadarMapMode.RAINVIEWER_RADAR) "rainviewer" else "anm"
                            view?.evaluateJavascript("if (window.setMode) { window.setMode('$modeStr'); }", null)

                            if (mode == RadarMapMode.ANM_RADAR) {
                                currentRadarImageUrl?.let { imgUrl ->
                                    view?.evaluateJavascript("if (window.setRadarOverlay) { window.setRadarOverlay('$imgUrl', $opacity); }", null)
                                }
                                val station = selectedStation
                                if (station != null && station.code != "COMPOSITE") {
                                    view?.evaluateJavascript("if (window.focusStation) { window.focusStation('${station.code}', ${station.latitude}, ${station.longitude}, ${station.coverageRadiusKm}); }", null)
                                } else {
                                    view?.evaluateJavascript("if (window.focusNational) { window.focusNational(); }", null)
                                }
                            } else {
                                rainViewerTilePath?.let { path ->
                                    view?.evaluateJavascript("if (window.setRainViewerTile) { window.setRainViewerTile('$path', $opacity); }", null)
                                }
                            }
                        }
                    }

                    loadUrl("file:///android_asset/radar_map.html")
                    webViewRef = this
                }
            },
            update = { wv ->
                webViewRef = wv
            }
        )

        // Overlay Map Controls (Zoom in/out, Re-center, Fullscreen)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
            shadowElevation = 3.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                // Zoom in
                IconButton(
                    onClick = {
                        webViewRef?.evaluateJavascript("if (window.map) { window.map.zoomIn(); }", null)
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Rounded.ZoomIn,
                        contentDescription = "Zoom In",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Zoom out
                IconButton(
                    onClick = {
                        webViewRef?.evaluateJavascript("if (window.map) { window.map.zoomOut(); }", null)
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Rounded.ZoomOut,
                        contentDescription = "Zoom Out",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Re-center on station or country
                IconButton(
                    onClick = {
                        val station = selectedStation
                        if (station != null && station.code != "COMPOSITE") {
                            webViewRef?.evaluateJavascript("if (window.focusStation) { window.focusStation('${station.code}', ${station.latitude}, ${station.longitude}, ${station.coverageRadiusKm}); }", null)
                        } else {
                            webViewRef?.evaluateJavascript("if (window.focusNational) { window.focusNational(); }", null)
                        }
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Rounded.MyLocation,
                        contentDescription = "Recentrare",
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(18.dp)
                    )
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
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.destroy()
            webViewRef = null
        }
    }
}
