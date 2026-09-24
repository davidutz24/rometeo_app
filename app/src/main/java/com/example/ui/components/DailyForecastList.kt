package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Schedule
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.DailyPoint
import com.example.model.ForecastCategory
import com.example.model.HourlyPoint
import com.example.model.Translations
import com.example.model.WeatherModel
import java.util.Locale

@Composable
fun DailyForecastList(
    daily: List<DailyPoint>,
    extendedDaily: List<DailyPoint> = emptyList(),
    hourly: List<HourlyPoint> = emptyList(),
    model: WeatherModel,
    category: ForecastCategory = ForecastCategory.FORECAST,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val displayList = if (extendedDaily.isNotEmpty()) {
        extendedDaily
    } else {
        daily
    }

    if (displayList.isEmpty()) return

    val isLongTerm = category == ForecastCategory.LONG_TERM || model.isLongRange
    var isExpanded by remember(category, model) {
        mutableStateOf(true)
    }

    val allMins = displayList.map { it.minTemperature }
    val allMaxs = displayList.map { it.maxTemperature }
    val weekMin = (allMins.minOrNull() ?: 10.0)
    val weekMax = (allMaxs.maxOrNull() ?: 30.0)
    val weekRange = (weekMax - weekMin).coerceAtLeast(1.0)

    val titleText = when {
        isLongTerm -> when (model) {
            WeatherModel.ECMWF_AIFS -> when (lang) {
                AppLanguage.ROMANIAN -> "Prognoză AI Sinoptic AIFS (${displayList.size} Zile)"
                AppLanguage.ENGLISH -> "AIFS AI Synoptic Forecast (${displayList.size} Days)"
                AppLanguage.HUNGARIAN -> "AIFS AI szinoptikus előrejelzés (${displayList.size} nap)"
            }
            WeatherModel.ECMWF_EXTENDED -> when (lang) {
                AppLanguage.ROMANIAN -> "Tendință Sub-Sezonieră ECMWF ENS (${displayList.size} Zile)"
                AppLanguage.ENGLISH -> "ECMWF ENS Sub-Seasonal Trend (${displayList.size} Days)"
                AppLanguage.HUNGARIAN -> "ECMWF ENS szub-szezonális trend (${displayList.size} nap)"
            }
            WeatherModel.GEFS -> when (lang) {
                AppLanguage.ROMANIAN -> "Ansamblu Global GEFS (${displayList.size} Zile)"
                AppLanguage.ENGLISH -> "GEFS Global Ensemble (${displayList.size} Days)"
                AppLanguage.HUNGARIAN -> "GEFS globális együttes (${displayList.size} nap)"
            }
            WeatherModel.SEAS5 -> when (lang) {
                AppLanguage.ROMANIAN -> "Tendință Sezonieră ECMWF SEAS5 (${displayList.size} Zile)"
                AppLanguage.ENGLISH -> "ECMWF SEAS5 Seasonal Trend (${displayList.size} Days)"
                AppLanguage.HUNGARIAN -> "ECMWF SEAS5 szezonális trend (${displayList.size} nap)"
            }
            WeatherModel.CFSV2 -> when (lang) {
                AppLanguage.ROMANIAN -> "Prognoză Climatică NOAA CFSv2 (${displayList.size} Zile)"
                AppLanguage.ENGLISH -> "NOAA CFSv2 Climate Forecast (${displayList.size} Days)"
                AppLanguage.HUNGARIAN -> "NOAA CFSv2 éghajlati előrejelzés (${displayList.size} nap)"
            }
            else -> when (lang) {
                AppLanguage.ROMANIAN -> "${model.shortName} (${displayList.size} Zile)"
                AppLanguage.ENGLISH -> "${model.shortName} (${displayList.size} Days)"
                AppLanguage.HUNGARIAN -> "${model.shortName} (${displayList.size} nap)"
            }
        }
        else -> {
            val daysCount = displayList.size
            when (lang) {
                AppLanguage.ROMANIAN -> "Prognoză $daysCount zile (${model.shortName})"
                AppLanguage.ENGLISH -> "$daysCount-Day Forecast (${model.shortName})"
                AppLanguage.HUNGARIAN -> "$daysCount napos előrejelzés (${model.shortName})"
            }
        }
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
                if (isLongTerm) {
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
                val daysUnit = when (lang) {
                    AppLanguage.ROMANIAN -> "zile"
                    AppLanguage.ENGLISH -> "Days"
                    AppLanguage.HUNGARIAN -> "nap"
                }
                Text(
                    text = "${displayList.size} $daysUnit (${model.shortName})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        val itemsToShow = if (isLongTerm && !isExpanded) {
            displayList.take(7)
        } else {
            displayList
        }

        // Days list mode
        itemsToShow.forEachIndexed { index, day ->
            val dayHourly = remember(day.dateIso, hourly, isLongTerm) {
                if (isLongTerm) {
                    emptyList()
                } else {
                    hourly.filter { it.timeIso.startsWith(day.dateIso) }
                }
            }

            DetailedDailyCard(
                day = day,
                weekMin = weekMin,
                weekRange = weekRange,
                accentColor = model.accentColor,
                isLongTerm = isLongTerm,
                dayHourly = dayHourly,
                lang = lang
            )
        }

        if (isLongTerm && displayList.size > 7) {
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
                        when (lang) {
                            AppLanguage.ROMANIAN -> "Afișează mai puțin (7 zile)"
                            AppLanguage.ENGLISH -> "Show Less (7 Days)"
                            AppLanguage.HUNGARIAN -> "Kevesebb mutatása (7 nap)"
                        }
                    } else {
                        when (lang) {
                            AppLanguage.ROMANIAN -> "Afișează tot orizontul (${displayList.size} zile)"
                            AppLanguage.ENGLISH -> "Show Full Horizon (${displayList.size} Days)"
                            AppLanguage.HUNGARIAN -> "Teljes időtáv (${displayList.size} nap)"
                        }
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
    isLongTerm: Boolean,
    dayHourly: List<HourlyPoint>,
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

                // Temperature Range + Expand Indicator
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
                            .width(42.dp)
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

                    Icon(
                        imageVector = if (isItemExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expanded content when day is clicked
            AnimatedVisibility(
                visible = isItemExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Meteorological metrics chips: Vânt, Presiune, Precipitații
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

                    // 2. Prognoza pe ore (doar la Prognoză / Termen Scurt & Mediu, NU la Termen Lung)
                    if (!isLongTerm && dayHourly.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Schedule,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = Translations.get("hourly_breakdown_title", lang),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = accentColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${dayHourly.size} ore",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = accentColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Horizontal scroll of hourly details for this specific day
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(dayHourly, key = { it.timeIso }) { hourPoint ->
                                    DayHourlyItemCard(
                                        hourly = hourPoint,
                                        accentColor = accentColor
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

@Composable
private fun DayHourlyItemCard(
    hourly: HourlyPoint,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        modifier = Modifier.width(72.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = hourly.displayHour,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )

            WeatherIcon(
                condition = hourly.weatherCondition,
                size = 24.dp,
                animate = false
            )

            Text(
                text = String.format(Locale.getDefault(), "%.0f°", hourly.temperature),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            // Precipitații
            if (hourly.precipitation > 0.0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(9.dp)
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", hourly.precipitation),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    )
                }
            } else {
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                )
            }

            // Vânt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Air,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(9.dp)
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.0f", hourly.windSpeed),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
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
