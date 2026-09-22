package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppNavTab
import com.example.model.Translations

@Composable
fun AppBottomNavigationBar(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    activeWarningsCount: Int,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 6.dp
    ) {
        // Tab 1: Forecasts
        NavigationBarItem(
            selected = currentTab == AppNavTab.FORECASTS,
            onClick = { onTabSelected(AppNavTab.FORECASTS) },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Cloud,
                    contentDescription = Translations.get("nav_forecasts", lang)
                )
            },
            label = {
                Text(
                    text = Translations.get("nav_forecasts", lang),
                    fontWeight = if (currentTab == AppNavTab.FORECASTS) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            )
        )

        // Tab 2: Radar & Satelit
        NavigationBarItem(
            selected = currentTab == AppNavTab.RADAR_SATELLITE,
            onClick = { onTabSelected(AppNavTab.RADAR_SATELLITE) },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Radar,
                    contentDescription = Translations.get("nav_radar_satellite", lang)
                )
            },
            label = {
                Text(
                    text = Translations.get("nav_radar_satellite", lang),
                    fontWeight = if (currentTab == AppNavTab.RADAR_SATELLITE) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0284C7),
                selectedTextColor = Color(0xFF0284C7),
                indicatorColor = Color(0xFF0284C7).copy(alpha = 0.18f)
            )
        )

        // Tab 3: Avertizari ANM (with active alert badge)
        NavigationBarItem(
            selected = currentTab == AppNavTab.ANM_WARNINGS,
            onClick = { onTabSelected(AppNavTab.ANM_WARNINGS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (activeWarningsCount > 0) {
                            Badge(
                                containerColor = Color(0xFFEF4444),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = activeWarningsCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = Translations.get("nav_anm_warnings", lang)
                    )
                }
            },
            label = {
                Text(
                    text = Translations.get("nav_anm_warnings", lang),
                    fontWeight = if (currentTab == AppNavTab.ANM_WARNINGS) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFEF4444),
                selectedTextColor = Color(0xFFEF4444),
                indicatorColor = Color(0xFFEF4444).copy(alpha = 0.15f)
            )
        )

        // Tab 3: Produse ANM (Climatology maps)
        NavigationBarItem(
            selected = currentTab == AppNavTab.ANM_PRODUCTS,
            onClick = { onTabSelected(AppNavTab.ANM_PRODUCTS) },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Layers,
                    contentDescription = Translations.get("nav_anm_products", lang)
                )
            },
            label = {
                Text(
                    text = Translations.get("nav_anm_products", lang),
                    fontWeight = if (currentTab == AppNavTab.ANM_PRODUCTS) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.tertiary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            )
        )
    }
}
