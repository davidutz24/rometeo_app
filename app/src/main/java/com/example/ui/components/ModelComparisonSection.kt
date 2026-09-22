package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ModelComparisonHour
import com.example.model.Translations
import com.example.model.WeatherModel
import java.util.Locale

@Composable
fun ModelComparisonSection(
    comparisonHours: List<ModelComparisonHour>,
    selectedModel: WeatherModel,
    onSelectModel: (WeatherModel) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    if (comparisonHours.isEmpty()) return

    var isExpanded by remember { mutableStateOf(true) }

    // Consensus calculation for the next 12 hours
    val upcomingHours = comparisonHours.take(12)
    val avgSpread = upcomingHours.mapNotNull { it.tempSpread }.average().takeIf { !it.isNaN() } ?: 1.5
    val isHighAgreement = avgSpread < 1.8

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("model_comparison_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.CompareArrows,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = Translations.get("model_comparison", lang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = Translations.get("compare_all_models", lang),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = "Toggle comparison",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Consensus badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isHighAgreement) Color(0xFF10B981).copy(alpha = 0.12f) else Color(0xFFF59E0B).copy(alpha = 0.14f),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isHighAgreement) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFF59E0B).copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isHighAgreement) Icons.Rounded.CheckCircle else Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = if (isHighAgreement) Color(0xFF10B981) else Color(0xFFD97706),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isHighAgreement) {
                            "${Translations.get("high_agreement", lang)} • ${String.format(Locale.getDefault(), Translations.get("model_spread", lang), avgSpread)}"
                        } else {
                            "${Translations.get("divergence", lang)} • ${String.format(Locale.getDefault(), Translations.get("model_spread", lang), avgSpread)}"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isHighAgreement) Color(0xFF059669) else Color(0xFFD97706)
                        )
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    // Multi-Model Mini Legend
                    Text(
                        text = Translations.get("temp_comparison", lang),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Model Comparison Horizon Canvas
                    MultiModelCanvas(
                        comparisonHours = comparisonHours.take(18),
                        selectedModel = selectedModel,
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Model Table / List with Current Projected Temperature
                    val currentHour = comparisonHours.firstOrNull()
                    if (currentHour != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            currentHour.values.forEach { modelVal ->
                                val isSelected = modelVal.model == selectedModel
                                ModelComparisonRow(
                                    model = modelVal.model,
                                    temperature = modelVal.temperature,
                                    isSelected = isSelected,
                                    onClick = { onSelectModel(modelVal.model) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelComparisonRow(
    model: WeatherModel,
    temperature: Double?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) model.accentColor.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 0.5.dp,
            color = if (isSelected) model.accentColor else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(model.accentColor)
                )
                Text(
                    text = model.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = model.resolution.split(" ").first(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Text(
                text = if (temperature != null) String.format(Locale.getDefault(), "%.1f°C", temperature) else "--",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) model.accentColor else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
private fun MultiModelCanvas(
    comparisonHours: List<ModelComparisonHour>,
    selectedModel: WeatherModel,
    modifier: Modifier = Modifier
) {
    if (comparisonHours.size < 2) return

    val allTemps = comparisonHours.flatMap { h -> h.values.mapNotNull { it.temperature } }
    val minT = (allTemps.minOrNull() ?: 10.0) - 1.5
    val maxT = (allTemps.maxOrNull() ?: 30.0) + 1.5
    val rangeT = (maxT - minT).coerceAtLeast(3.0)

    val stepWidth = 44.dp
    val totalWidth = stepWidth * comparisonHours.size

    Box(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(modifier = Modifier.width(totalWidth).height(120.dp)) {
            val w = size.width
            val h = size.height
            val stepPx = stepWidth.toPx()

            // Draw horizontal zero/guide lines
            val guideY1 = h * 0.25f
            val guideY2 = h * 0.75f
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, guideY1),
                end = Offset(w, guideY1),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, guideY2),
                end = Offset(w, guideY2),
                strokeWidth = 1f
            )

            // Draw each model's line
            WeatherModel.entries.forEach { model ->
                val isSelected = model == selectedModel
                val strokeWidth = if (isSelected) 3.5f else 1.8f
                val lineColor = if (isSelected) model.accentColor else model.accentColor.copy(alpha = 0.6f)

                val points = comparisonHours.mapIndexedNotNull { idx, hour ->
                    val temp = hour.values.find { it.model == model }?.temperature
                    if (temp != null) {
                        val x = (idx + 0.5f) * stepPx
                        val normalized = ((temp - minT) / rangeT).toFloat()
                        val y = h * 0.85f - normalized * (h * 0.7f)
                        Offset(x, y)
                    } else null
                }

                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = lineColor,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                // If selected, draw points
                if (isSelected) {
                    points.forEach { pt ->
                        drawCircle(color = model.accentColor, radius = 3.5f, center = pt)
                    }
                }
            }
        }
    }
}
