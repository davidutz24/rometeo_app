package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.HourlyPoint
import com.example.model.Translations
import com.example.model.WeatherModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HourlyForecastChart(
    hourly: List<HourlyPoint>,
    model: WeatherModel,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val nowMs = System.currentTimeMillis()
    val isoFormat = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US) }
    val validHourly = remember(hourly) {
        hourly.filter { pt ->
            val t = try { isoFormat.parse(pt.timeIso)?.time } catch (_: Exception) { null }
            t == null || (t + 3600_000L > nowMs)
        }
    }

    if (validHourly.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("hourly_forecast_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp)
        ) {
            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = Translations.get("hourly_forecast", lang),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                selectedIndex?.let { idx ->
                    val item = validHourly.getOrNull(idx)
                    if (item != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = model.accentColor.copy(alpha = 0.18f)
                        ) {
                            val hourText = if (item.displayHour.equals("Acum", ignoreCase = true) || item.displayHour.equals("Now", ignoreCase = true)) {
                                Translations.get("now", lang)
                            } else {
                                item.displayHour
                            }
                            Text(
                                text = "$hourText • ${String.format(Locale.getDefault(), "%.1f°C", item.temperature)}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = model.accentColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Bezier Graph (Scrollable or 24-hour viewport)
            val pointsToDraw = validHourly.take(24)
            HourlyBezierCanvas(
                hourly = pointsToDraw,
                modelColor = model.accentColor,
                selectedIndex = selectedIndex,
                onSelectIndex = { selectedIndex = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal Scroller of Hourly Cards
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(validHourly.take(24)) { index, item ->
                    val isSelected = selectedIndex == index
                    HourlyItemCard(
                        item = item,
                        accentColor = model.accentColor,
                        isSelected = isSelected,
                        lang = lang,
                        onClick = { selectedIndex = if (isSelected) null else index }
                    )
                }
            }
        }
    }
}

@Composable
private fun HourlyBezierCanvas(
    hourly: List<HourlyPoint>,
    modelColor: Color,
    selectedIndex: Int?,
    onSelectIndex: (Int?) -> Unit
) {
    val temps = hourly.map { it.temperature }
    val minTemp = (temps.minOrNull() ?: 10.0) - 2.0
    val maxTemp = (temps.maxOrNull() ?: 30.0) + 2.0
    val tempRange = (maxTemp - minTemp).coerceAtLeast(4.0)

    val stepWidth = 48.dp
    val canvasWidth = stepWidth * hourly.size.coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .width(canvasWidth)
                .height(110.dp)
                .pointerInput(hourly) {
                    detectTapGestures { offset ->
                        val idx = (offset.x / stepWidth.toPx()).toInt()
                        if (idx in hourly.indices) {
                            onSelectIndex(idx)
                        } else {
                            onSelectIndex(null)
                        }
                    }
                }
                .pointerInput(hourly) {
                    detectDragGestures { change, _ ->
                        val idx = (change.position.x / stepWidth.toPx()).toInt()
                        if (idx in hourly.indices) {
                            onSelectIndex(idx)
                        }
                    }
                }
        ) {
            val h = size.height
            val stepPx = stepWidth.toPx()
            val points = hourly.mapIndexed { idx, item ->
                val x = (idx + 0.5f) * stepPx
                val normalized = ((item.temperature - minTemp) / tempRange).toFloat()
                val y = h * 0.82f - normalized * (h * 0.62f)
                Offset(x, y)
            }

            if (points.size < 2) return@Canvas

            // Build smooth cubic bezier path
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val cx = (p0.x + p1.x) / 2f
                    cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                }
            }

            // Fill path below curve
            val fillPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, h)
                lineTo(points.first().x, h)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        modelColor.copy(alpha = 0.35f),
                        modelColor.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // Draw line stroke
            drawPath(
                path = path,
                color = modelColor,
                style = Stroke(width = 3.2f, cap = StrokeCap.Round)
            )

            // Draw points
            points.forEachIndexed { idx, pt ->
                val isSel = selectedIndex == idx
                drawCircle(
                    color = if (isSel) Color.White else modelColor,
                    radius = if (isSel) 5.5f else 3.5f,
                    center = pt
                )
                if (isSel) {
                    drawCircle(
                        color = modelColor,
                        radius = 8.5f,
                        center = pt,
                        style = Stroke(width = 2.5f)
                    )
                    // Draw vertical guide line
                    drawLine(
                        color = modelColor.copy(alpha = 0.5f),
                        start = Offset(pt.x, 0f),
                        end = Offset(pt.x, h),
                        strokeWidth = 1.5f
                    )
                }
            }
        }
    }
}

@Composable
private fun HourlyItemCard(
    item: HourlyPoint,
    accentColor: Color,
    isSelected: Boolean,
    lang: AppLanguage,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .width(68.dp)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) accentColor else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val hourLabel = if (item.displayHour.equals("Acum", ignoreCase = true) || item.displayHour.equals("Now", ignoreCase = true)) {
                Translations.get("now", lang)
            } else {
                item.displayHour
            }
            Text(
                text = hourLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            WeatherIcon(
                condition = item.weatherCondition,
                size = 28.dp,
                animate = false
            )

            Text(
                text = String.format(Locale.getDefault(), "%.0f°", item.temperature),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            if (item.precipitation > 0.0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WaterDrop,
                        contentDescription = "Precipitation",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", item.precipitation),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
