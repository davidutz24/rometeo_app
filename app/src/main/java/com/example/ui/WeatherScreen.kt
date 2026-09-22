package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.AppNavTab
import com.example.model.ForecastCategory
import com.example.model.Translations
import com.example.model.WeatherConditionType
import com.example.model.WeatherForecastResult
import com.example.model.WeatherModel
import com.example.ui.components.AnmProductsScreen
import com.example.ui.components.AnmWarningsScreen
import com.example.ui.components.AnimatedWeatherBackground
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.AppInfoDialog
import com.example.ui.components.CurrentWeatherCard
import com.example.ui.components.DailyForecastList
import com.example.ui.components.HourlyForecastChart
import com.example.ui.components.LocationSearchDialog
import com.example.ui.components.ModelComparisonSection
import com.example.ui.components.ModelDetailsDialog
import com.example.ui.components.ModelSelectorBar
import com.example.ui.components.RadarSatelliteScreen
import com.example.ui.components.WeatherHeader
import com.example.viewmodel.ForecastUiState
import com.example.viewmodel.ThemeMode
import com.example.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val savedLocations by viewModel.savedLocations.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()
    val forecastState by viewModel.forecastState.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val currentNavTab by viewModel.currentNavTab.collectAsStateWithLifecycle()
    val generalWarnings by viewModel.generalWarnings.collectAsStateWithLifecycle()
    val nowcastingWarnings by viewModel.nowcastingWarnings.collectAsStateWithLifecycle()
    val isLoadingAnm by viewModel.isLoadingAnm.collectAsStateWithLifecycle()

    val totalActiveWarnings = generalWarnings.size + nowcastingWarnings.size

    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }

    var showLocationSearch by remember { mutableStateOf(false) }
    var showModelInfoFor by remember { mutableStateOf<WeatherModel?>(null) }
    var showAppInfoDialog by remember { mutableStateOf(false) }

    // Current weather condition for animated background
    val conditionType = when (val state = forecastState) {
        is ForecastUiState.Success -> state.data.current.weatherCondition.type
        else -> WeatherConditionType.CLEAR_DAY
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Dynamic procedural animated sky canvas
        AnimatedWeatherBackground(
            conditionType = conditionType,
            isDark = isDark
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column {
                    WeatherHeader(
                        location = currentLocation,
                        language = language,
                        themeMode = themeMode,
                        onLocationClick = { showLocationSearch = true },
                        onLanguageSelect = { viewModel.setLanguage(it) },
                        onThemeToggle = {
                            val nextMode = when (themeMode) {
                                ThemeMode.SYSTEM -> ThemeMode.LIGHT
                                ThemeMode.LIGHT -> ThemeMode.DARK
                                ThemeMode.DARK -> ThemeMode.SYSTEM
                            }
                            viewModel.setThemeMode(nextMode)
                        },
                        onInfoClick = { showAppInfoDialog = true }
                    )

                    if (currentNavTab == AppNavTab.FORECASTS) {
                        ModelSelectorBar(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { viewModel.selectCategory(it) },
                            selectedModel = selectedModel,
                            onModelSelected = { viewModel.selectModel(it) },
                            onShowModelInfo = { showModelInfoFor = it },
                            lang = language
                        )
                    }
                }
            },
            bottomBar = {
                AppBottomNavigationBar(
                    currentTab = currentNavTab,
                    onTabSelected = { viewModel.selectNavTab(it) },
                    activeWarningsCount = totalActiveWarnings,
                    lang = language
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentNavTab) {
                    AppNavTab.FORECASTS -> {
                        when (val state = forecastState) {
                            is ForecastUiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(14.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                color = selectedModel.accentColor,
                                                modifier = Modifier.size(44.dp)
                                            )
                                            Text(
                                                text = "${selectedModel.displayName} • ${Translations.get("live_model", language)}…",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            is ForecastUiState.Success -> {
                                ForecastContent(
                                    data = state.data,
                                    selectedCategory = selectedCategory,
                                    selectedModel = selectedModel,
                                    lang = language,
                                    onSelectModel = { viewModel.selectModel(it) },
                                    onRefresh = { viewModel.refreshForecast() }
                                )
                            }

                            is ForecastUiState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp)
                                            .testTag("error_card"),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.CloudOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Text(
                                                text = Translations.get("no_internet_cached", language),
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            Text(
                                                text = state.message,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    textAlign = TextAlign.Center
                                                )
                                            )
                                            Button(
                                                onClick = { viewModel.refreshForecast() },
                                                shape = RoundedCornerShape(14.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = selectedModel.accentColor
                                                ),
                                                modifier = Modifier.testTag("retry_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Refresh,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.size(8.dp))
                                                Text(text = Translations.get("retry", language))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    AppNavTab.RADAR_SATELLITE -> {
                        RadarSatelliteScreen(
                            viewModel = viewModel,
                            lang = language
                        )
                    }

                    AppNavTab.ANM_WARNINGS -> {
                        AnmWarningsScreen(
                            nowcastingList = nowcastingWarnings,
                            generalList = generalWarnings,
                            isLoading = isLoadingAnm,
                            onRefresh = { viewModel.loadAnmWarnings() },
                            lang = language
                        )
                    }

                    AppNavTab.ANM_PRODUCTS -> {
                        AnmProductsScreen(
                            products = viewModel.anmProducts,
                            lang = language
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showLocationSearch) {
        LocationSearchDialog(
            savedLocations = savedLocations,
            currentLocation = currentLocation,
            searchResults = searchResults,
            isSearching = isSearching,
            onQueryChanged = { viewModel.onSearchQueryChanged(it) },
            onSelectLocation = { viewModel.selectLocation(it) },
            onSaveLocation = { viewModel.addLocationToSaved(it) },
            onDeleteLocation = { viewModel.deleteLocation(it) },
            onDismiss = { showLocationSearch = false },
            lang = language
        )
    }

    showModelInfoFor?.let { model ->
        ModelDetailsDialog(
            model = model,
            lang = language,
            onDismiss = { showModelInfoFor = null }
        )
    }

    if (showAppInfoDialog) {
        AppInfoDialog(
            onDismiss = { showAppInfoDialog = false },
            lang = language
        )
    }
}

@Composable
private fun ForecastContent(
    data: WeatherForecastResult,
    selectedCategory: ForecastCategory,
    selectedModel: WeatherModel,
    lang: AppLanguage,
    onSelectModel: (WeatherModel) -> Unit,
    onRefresh: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("forecast_content_list"),
        contentPadding = PaddingValues(top = 10.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (selectedCategory == ForecastCategory.SHORT_TERM) {
            // SHORT-TERM: Front page layout with current weather hero + hourly forecast + model comparison + days forecast
            item(key = "current_card") {
                CurrentWeatherCard(
                    current = data.current,
                    model = selectedModel,
                    isOfflineCache = data.isOfflineCache,
                    cachedAtTimestamp = data.fetchedAtTimestamp,
                    lang = lang,
                    onRefresh = onRefresh
                )
            }

            item(key = "hourly_chart") {
                HourlyForecastChart(
                    hourly = data.hourly,
                    model = selectedModel,
                    lang = lang
                )
            }

            item(key = "model_comparison") {
                ModelComparisonSection(
                    comparisonHours = data.comparison,
                    selectedModel = selectedModel,
                    onSelectModel = onSelectModel,
                    lang = lang
                )
            }

            item(key = "daily_forecast_short") {
                DailyForecastList(
                    daily = data.daily,
                    extendedDaily = data.extendedDaily,
                    model = selectedModel,
                    category = selectedCategory,
                    lang = lang
                )
            }
        } else {
            // MEDIUM-TERM & LONG-TERM: Only the days forecast with detailed meteorological information in list-mode
            item(key = "daily_forecast_standalone") {
                DailyForecastList(
                    daily = data.daily,
                    extendedDaily = data.extendedDaily,
                    model = selectedModel,
                    category = selectedCategory,
                    lang = lang
                )
            }
        }

        // Bottom spacer to clear navigation bar
        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.navigationBarsPadding().height(16.dp))
        }
    }
}
