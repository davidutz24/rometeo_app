package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.CompassCalibration
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CurrentWeather
import com.example.model.Translations
import com.example.model.WeatherModel
import java.util.Locale

@Composable
fun CurrentWeatherCard(
    current: CurrentWeather,
    model: WeatherModel,
    isOfflineCache: Boolean,
    cachedAtTimestamp: Long,
    lang: AppLanguage,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("current_weather_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status bar (Live Model vs Offline Cache) + Refresh button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOfflineCache) {
                        Color(0xFFF59E0B).copy(alpha = 0.18f)
                    } else {
                        model.accentColor.copy(alpha = 0.16f)
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isOfflineCache) Color(0xFFF59E0B) else model.accentColor.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isOfflineCache) Color(0xFFF59E0B) else Color(0xFF10B981))
                        )
                        Text(
                            text = if (isOfflineCache) {
                                Translations.get("offline_cached", lang)
                            } else {
                                "${model.shortName} • ${Translations.get("live_model", lang)}"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isOfflineCache) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                // Relative time + refresh icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatRelativeTime(cachedAtTimestamp, lang),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(28.dp).testTag("refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = Translations.get("refresh", lang),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Weather Hero: Icon & Big Temperature
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherIcon(
                    condition = current.weatherCondition,
                    size = 76.dp,
                    animate = true
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f°", current.temperature),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 62.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${Translations.get("feels_like", lang)} ${String.format(Locale.getDefault(), "%.1f°C", current.apparentTemperature)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Condition title
            Text(
                text = current.weatherCondition.getDescription(lang),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Metrics Grid (Humidity, Wind + Compass, Pressure, Rain)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    icon = Icons.Rounded.WaterDrop,
                    label = Translations.get("humidity", lang),
                    value = "${current.relativeHumidity}%",
                    accentColor = Color(0xFF38BDF8)
                )

                MetricWindCompass(
                    speedKmH = current.windSpeed,
                    directionDeg = current.windDirection,
                    label = Translations.get("wind", lang),
                    accentColor = Color(0xFF10B981)
                )

                MetricItem(
                    icon = Icons.Rounded.Speed,
                    label = Translations.get("pressure", lang),
                    value = "${current.surfacePressure.toInt()} hPa",
                    accentColor = Color(0xFFA855F7)
                )

                MetricItem(
                    icon = Icons.Rounded.WaterDrop,
                    label = Translations.get("precipitation", lang),
                    value = String.format(Locale.getDefault(), "%.1f mm", current.precipitation),
                    accentColor = Color(0xFF0284C7)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = accentColor.copy(alpha = 0.14f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun MetricWindCompass(
    speedKmH: Double,
    directionDeg: Int,
    label: String,
    accentColor: Color
) {
    val animatedRotation by animateFloatAsState(
        targetValue = directionDeg.toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "compass_rot"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = accentColor.copy(alpha = 0.14f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Navigation,
                    contentDescription = "$label Compass",
                    tint = accentColor,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(animatedRotation)
                )
            }
        }
        Text(
            text = "${speedKmH.toInt()} km/h",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

private fun formatRelativeTime(timestamp: Long, lang: AppLanguage): String {
    if (timestamp <= 0L) return Translations.get("just_now", lang)
    val diffSeconds = (System.currentTimeMillis() - timestamp) / 1000
    return when {
        diffSeconds < 60 -> Translations.get("just_now", lang)
        diffSeconds < 3600 -> {
            val mins = (diffSeconds / 60).toInt()
            String.format(Locale.getDefault(), Translations.get("min_ago", lang), mins)
        }
        else -> {
            val hours = (diffSeconds / 3600).toInt()
            String.format(Locale.getDefault(), Translations.get("hours_ago", lang), hours)
        }
    }
}
