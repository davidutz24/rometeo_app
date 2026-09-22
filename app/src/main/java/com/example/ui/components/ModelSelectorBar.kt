package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ForecastCategory
import com.example.model.Translations
import com.example.model.WeatherModel

@Composable
fun ModelSelectorBar(
    selectedCategory: ForecastCategory,
    onCategorySelected: (ForecastCategory) -> Unit,
    selectedModel: WeatherModel,
    onModelSelected: (WeatherModel) -> Unit,
    onShowModelInfo: (WeatherModel) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Category Selection Tabs: Short-term / Medium-term / Long-term
        ForecastCategoryBar(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            lang = lang
        )

        // Models for selected category
        val modelsInSelectedCategory = WeatherModel.forCategory(selectedCategory)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("model_selector_row"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(modelsInSelectedCategory, key = { it.id }) { model ->
                val isSelected = model == selectedModel
                ModelChip(
                    model = model,
                    isSelected = isSelected,
                    onClick = { onModelSelected(model) }
                )
            }
        }

        // Active model details subtitle bar
        ActiveModelQuickInfo(
            model = selectedModel,
            onInfoClick = { onShowModelInfo(selectedModel) }
        )
    }
}

@Composable
private fun ForecastCategoryBar(
    selectedCategory: ForecastCategory,
    onCategorySelected: (ForecastCategory) -> Unit,
    lang: AppLanguage
) {
    val categories = ForecastCategory.entries
    val selectedIndex = categories.indexOf(selectedCategory)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex.coerceAtLeast(0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 16.dp,
        divider = {},
        indicator = { tabPositions ->
            if (selectedIndex in tabPositions.indices) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    height = 3.dp,
                    color = when (selectedCategory) {
                        ForecastCategory.SHORT_TERM -> Color(0xFF10B981)
                        ForecastCategory.MEDIUM_TERM -> Color(0xFF38BDF8)
                        ForecastCategory.LONG_TERM -> Color(0xFF8B5CF6)
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forecast_category_tabs")
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = category == selectedCategory
            val (icon, title) = when (category) {
                ForecastCategory.SHORT_TERM -> Pair(Icons.Rounded.Schedule, Translations.get("category_short_term", lang))
                ForecastCategory.MEDIUM_TERM -> Pair(Icons.Rounded.Timeline, Translations.get("category_medium_term", lang))
                ForecastCategory.LONG_TERM -> Pair(Icons.Rounded.CalendarMonth, Translations.get("category_long_term", lang))
            }

            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                modifier = Modifier.testTag("category_tab_${category.id}"),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) {
                                when (category) {
                                    ForecastCategory.SHORT_TERM -> Color(0xFF10B981)
                                    ForecastCategory.MEDIUM_TERM -> Color(0xFF38BDF8)
                                    ForecastCategory.LONG_TERM -> Color(0xFF8B5CF6)
                                }
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ModelChip(
    model: WeatherModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) {
            model.accentColor.copy(alpha = 0.22f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
        },
        animationSpec = spring(),
        label = "chip_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) model.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        animationSpec = spring(),
        label = "chip_border"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(width = if (isSelected) 1.8.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("model_chip_${model.id}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Model indicator dot or AI/Rapid/Long icon
            if (model.isAi) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = "AI Model",
                    tint = model.accentColor,
                    modifier = Modifier.size(16.dp)
                )
            } else if (model.isRapidUpdate) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = "Flash Rapid Run",
                    tint = model.accentColor,
                    modifier = Modifier.size(16.dp)
                )
            } else if (model.isLongRange) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = "Extended Range",
                    tint = model.accentColor,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) model.accentColor else Color.Gray.copy(alpha = 0.6f))
                )
            }

            Text(
                text = model.shortName,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Resolution mini-badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) model.accentColor.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            ) {
                Text(
                    text = model.resolution.split(" ").first(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ActiveModelQuickInfo(
    model: WeatherModel,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = model.agency,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = model.architectureType,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                ),
                maxLines = 1
            )
        }

        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.size(32.dp).testTag("model_info_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = "Model Specs",
                tint = model.accentColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
