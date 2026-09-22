package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.DailyPoint
import com.example.model.ForecastCategory
import com.example.model.Translations
import com.example.model.WeatherModel
import java.util.Locale

@Composable
fun DailyForecastList(
    daily: List<DailyPoint>,
    extendedDaily: List<DailyPoint> = emptyList(),
    model: WeatherModel,
    category: ForecastCategory = ForecastCategory.SHORT_TERM,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val displayList = if (model.isLongRange && extendedDaily.isNotEmpty()) {
        extendedDaily
    } else if (model == WeatherModel.WEATHERNEXT_3_MEDIUM && extendedDaily.isNotEmpty()) {
        extendedDaily
    } else {
        daily
    }

    if (displayList.isEmpty()) return

    // In Medium and Long-term standalone list mode, start expanded or show list items cleanly
    val isStandaloneListMode = category == ForecastCategory.MEDIUM_TERM || category == ForecastCategory.LONG_TERM
    var isExpanded by remember(category, model) {
        mutableStateOf(if (model.isLongRange) false else true)
    }

    val allMins = displayList.map { it.minTemperature }
    val allMaxs = displayList.map { it.maxTemperature }
    val weekMin = (allMins.minOrNull() ?: 10.0)
    val weekMax = (allMaxs.maxOrNull() ?: 30.0)
    val weekRange = (weekMax - weekMin).coerceAtLeast(1.0)

    val titleText = when {
        category == ForecastCategory.LONG_TERM -> Translations.get("long_term_forecast_title", lang)
        category == ForecastCategory.MEDIUM_TERM -> Translations.get("category_medium_term", lang)
        model.isRapidUpdate -> "ICON-EU Flash (30-Hour Horizon)"
        else -> Translations.get("daily_forecast", lang)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("daily_forecast_container"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (category == ForecastCategory.LONG_TERM) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = model.accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = model.accentColor.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, model.accentColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "${displayList.size} Days (${model.shortName})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        val itemsToShow = if (model.isLongRange && !isExpanded) {
            displayList.take(7)
        } else {
            displayList
        }

        // Days list mode
        itemsToShow.forEachIndexed { index, day ->
            DetailedDailyCard(
                day = day,
                weekMin = weekMin,
                weekRange = weekRange,
                accentColor = model.accentColor,
                isDetailed = isStandaloneListMode,
                lang = lang
            )
        }

        if (model.isLongRange && displayList.size > 7) {
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.textButtonColors(contentColor = model.accentColor)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isExpanded) {
                        "Show Less (7 Days)"
                    } else {
                        "Show Full 46-Day ECMWF Ensemble (${displayList.size} Days)"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun DetailedDailyCard(
    day: DailyPoint,
    weekMin: Double,
    weekRange: Double,
    accentColor: Color,
    isDetailed: Boolean,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    var isItemExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isItemExpanded = !isItemExpanded }
            .testTag("daily_item_${day.dateIso}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Primary summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Day + Date
                Column(modifier = Modifier.width(88.dp)) {
                    Text(
                        text = day.displayDay,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = day.dateIso,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    )
                }

                // Weather Icon + Condition text
                Row(
                    modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeatherIcon(
                        condition = day.weatherCondition,
                        size = 32.dp,
                        animate = false
                    )
                    Column {
                        Text(
                            text = day.weatherCondition.getDescription(lang),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )
                        if (day.precipitationSum > 0.0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f mm", day.precipitationSum),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Temperature Range
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.0f°", day.minTemperature),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    // Compact mini temperature gradient indicator
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        val startFraction = ((day.minTemperature - weekMin) / weekRange).coerceIn(0.0, 1.0).toFloat()
                        val endFraction = ((day.maxTemperature - weekMin) / weekRange).coerceIn(0.0, 1.0).toFloat()
                        val barWidthFraction = (endFraction - startFraction).coerceAtLeast(0.12f)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(barWidthFraction)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF38BDF8), Color(0xFFF59E0B))
                                    )
                                )
                        )
                    }

                    Text(
                        text = String.format(Locale.getDefault(), "%.0f°", day.maxTemperature),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // Always show meteorological info chips in standalone list mode, or when expanded in short-term
            if (isDetailed || isItemExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Max Wind
                        DayMetricChip(
                            icon = Icons.Rounded.Air,
                            label = Translations.get("wind", lang),
                            value = String.format(Locale.getDefault(), "%.1f km/h", day.maxWindSpeed),
                            tint = Color(0xFF10B981)
                        )

                        // Mean Pressure
                        DayMetricChip(
                            icon = Icons.Rounded.Speed,
                            label = Translations.get("pressure", lang),
                            value = String.format(Locale.getDefault(), "%.0f hPa", day.surfacePressure),
                            tint = Color(0xFF8B5CF6)
                        )

                        // Precipitation accumulation
                        DayMetricChip(
                            icon = Icons.Rounded.WaterDrop,
                            label = Translations.get("precipitation", lang),
                            value = String.format(Locale.getDefault(), "%.1f mm", day.precipitationSum),
                            tint = Color(0xFF0284C7)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayMetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp
                )
            )
        }
    }
}
