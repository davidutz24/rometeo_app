package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferencesManager
import com.example.data.remote.AnmRemoteDataSource
import com.example.data.repository.WeatherRepository
import com.example.model.AnmProductItem
import com.example.model.AnmRadarFrame
import com.example.model.AnmWarningItem
import com.example.model.AppLanguage
import com.example.model.AppNavTab
import com.example.model.EumetsatLayerMode
import com.example.model.EumetsatSatelliteFrame
import com.example.model.ForecastCategory
import com.example.model.LocationItem
import com.example.model.MeteosatChannelInfo
import com.example.model.MeteosatFrame
import com.example.model.RadarSatelliteSubTab
import com.example.model.RadarStationFileItem
import com.example.model.RadarStationInfo
import com.example.model.RainViewerRadarFrame
import com.example.model.WeatherForecastResult
import com.example.model.WeatherModel
import com.example.util.LocationTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

sealed interface ForecastUiState {
    data object Loading : ForecastUiState
    data class Success(val data: WeatherForecastResult) : ForecastUiState
    data class Error(val message: String, val cachedData: WeatherForecastResult? = null) : ForecastUiState
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = WeatherRepository(database)
    private val preferencesManager = UserPreferencesManager(application)
    private val locationTracker = LocationTracker(application)

    val savedLocations: StateFlow<List<LocationItem>> = repository.getSavedLocations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocationItem.DEFAULT_LOCATIONS.take(3)
        )

    private val _currentLocation = MutableStateFlow(
        preferencesManager.getLastLocation() ?: LocationItem.DEFAULT_LOCATIONS.first()
    )
    val currentLocation: StateFlow<LocationItem> = _currentLocation.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ForecastCategory.FORECAST)
    val selectedCategory: StateFlow<ForecastCategory> = _selectedCategory.asStateFlow()

    private val _selectedModel = MutableStateFlow(WeatherModel.ICON_EU_FLASH)
    val selectedModel: StateFlow<WeatherModel> = _selectedModel.asStateFlow()

    private val _forecastState = MutableStateFlow<ForecastUiState>(ForecastUiState.Loading)
    val forecastState: StateFlow<ForecastUiState> = _forecastState.asStateFlow()

    private val _language = MutableStateFlow(preferencesManager.getLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(preferencesManager.getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isLocatingGps = MutableStateFlow(false)
    val isLocatingGps: StateFlow<Boolean> = _isLocatingGps.asStateFlow()

    private val _gpsMessage = MutableStateFlow<String?>(null)
    val gpsMessage: StateFlow<String?> = _gpsMessage.asStateFlow()

    private val _currentNavTab = MutableStateFlow(AppNavTab.FORECASTS)
    val currentNavTab: StateFlow<AppNavTab> = _currentNavTab.asStateFlow()

    private val _generalWarnings = MutableStateFlow<List<AnmWarningItem>>(emptyList())
    val generalWarnings: StateFlow<List<AnmWarningItem>> = _generalWarnings.asStateFlow()

    private val _nowcastingWarnings = MutableStateFlow<List<AnmWarningItem>>(emptyList())
    val nowcastingWarnings: StateFlow<List<AnmWarningItem>> = _nowcastingWarnings.asStateFlow()

    private val _isLoadingAnm = MutableStateFlow(false)
    val isLoadingAnm: StateFlow<Boolean> = _isLoadingAnm.asStateFlow()

    val anmProducts: List<AnmProductItem> = AnmRemoteDataSource.getAnmClimatologyProducts()

    // --- Radar & Satellite States ---
    private val _radarSubTab = MutableStateFlow(RadarSatelliteSubTab.NATIONAL_RADAR)
    val radarSubTab: StateFlow<RadarSatelliteSubTab> = _radarSubTab.asStateFlow()

    private val _nationalRadarFrames = MutableStateFlow<List<AnmRadarFrame>>(emptyList())
    val nationalRadarFrames: StateFlow<List<AnmRadarFrame>> = _nationalRadarFrames.asStateFlow()

    private val _currentRadarFrameIndex = MutableStateFlow(0)
    val currentRadarFrameIndex: StateFlow<Int> = _currentRadarFrameIndex.asStateFlow()

    private val _isRadarPlaying = MutableStateFlow(false)
    val isRadarPlaying: StateFlow<Boolean> = _isRadarPlaying.asStateFlow()

    private val _radarOpacity = MutableStateFlow(0.85f)
    val radarOpacity: StateFlow<Float> = _radarOpacity.asStateFlow()

    private val _radarPlaybackSpeedMs = MutableStateFlow(400L)
    val radarPlaybackSpeedMs: StateFlow<Long> = _radarPlaybackSpeedMs.asStateFlow()

    private val _isLoadingRadar = MutableStateFlow(false)
    val isLoadingRadar: StateFlow<Boolean> = _isLoadingRadar.asStateFlow()

    val radarStations: List<RadarStationInfo> = AnmRemoteDataSource.getRadarStations()

    private val _selectedRadarStation = MutableStateFlow(radarStations.first())
    val selectedRadarStation: StateFlow<RadarStationInfo> = _selectedRadarStation.asStateFlow()

    private val _stationFiles = MutableStateFlow<List<RadarStationFileItem>>(emptyList())
    val stationFiles: StateFlow<List<RadarStationFileItem>> = _stationFiles.asStateFlow()

    private val _isLoadingStationFiles = MutableStateFlow(false)
    val isLoadingStationFiles: StateFlow<Boolean> = _isLoadingStationFiles.asStateFlow()

    val meteosatChannels: List<MeteosatChannelInfo> = AnmRemoteDataSource.getMeteosatChannels()

    private val _selectedMeteosatChannel = MutableStateFlow(meteosatChannels.first())
    val selectedMeteosatChannel: StateFlow<MeteosatChannelInfo> = _selectedMeteosatChannel.asStateFlow()

    private val _meteosatFrames = MutableStateFlow<List<MeteosatFrame>>(emptyList())
    val meteosatFrames: StateFlow<List<MeteosatFrame>> = _meteosatFrames.asStateFlow()

    private val _selectedMeteosatFrame = MutableStateFlow<MeteosatFrame?>(null)
    val selectedMeteosatFrame: StateFlow<MeteosatFrame?> = _selectedMeteosatFrame.asStateFlow()

    private val _isLoadingMeteosat = MutableStateFlow(false)
    val isLoadingMeteosat: StateFlow<Boolean> = _isLoadingMeteosat.asStateFlow()

    private val _rainViewerFrames = MutableStateFlow<List<RainViewerRadarFrame>>(emptyList())
    val rainViewerFrames: StateFlow<List<RainViewerRadarFrame>> = _rainViewerFrames.asStateFlow()

    private val _currentRainViewerIndex = MutableStateFlow(0)
    val currentRainViewerIndex: StateFlow<Int> = _currentRainViewerIndex.asStateFlow()

    private val _isRainViewerPlaying = MutableStateFlow(false)
    val isRainViewerPlaying: StateFlow<Boolean> = _isRainViewerPlaying.asStateFlow()

    private val _isLoadingRainViewer = MutableStateFlow(false)
    val isLoadingRainViewer: StateFlow<Boolean> = _isLoadingRainViewer.asStateFlow()

    // EUMETSAT / MTG Satellite State
    private val _selectedEumetsatLayerMode = MutableStateFlow(EumetsatLayerMode.VISIBLE)
    val selectedEumetsatLayerMode: StateFlow<EumetsatLayerMode> = _selectedEumetsatLayerMode.asStateFlow()

    private val _eumetsatFrames = MutableStateFlow<List<EumetsatSatelliteFrame>>(emptyList())
    val eumetsatFrames: StateFlow<List<EumetsatSatelliteFrame>> = _eumetsatFrames.asStateFlow()

    private val _currentEumetsatIndex = MutableStateFlow(0)
    val currentEumetsatIndex: StateFlow<Int> = _currentEumetsatIndex.asStateFlow()

    private val _isEumetsatPlaying = MutableStateFlow(false)
    val isEumetsatPlaying: StateFlow<Boolean> = _isEumetsatPlaying.asStateFlow()

    private val _isLoadingEumetsat = MutableStateFlow(false)
    val isLoadingEumetsat: StateFlow<Boolean> = _isLoadingEumetsat.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LocationItem>>(emptyList())
    val searchResults: StateFlow<List<LocationItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var fetchJob: Job? = null
    private var searchJob: Job? = null
    private var anmJob: Job? = null
    private var radarJob: Job? = null
    private var stationFilesJob: Job? = null
    private var meteosatJob: Job? = null
    private var rainViewerJob: Job? = null
    private var eumetsatJob: Job? = null

    init {
        loadForecast(forceRefresh = false)
        loadAnmWarnings()
        loadNationalRadar()
        loadStationFiles(radarStations.first().code)
        loadMeteosatFrames(meteosatChannels.first().tipNumber)
        loadRainViewerFrames()
        loadEumetsatFrames(EumetsatLayerMode.VISIBLE)
    }

    fun selectLocation(location: LocationItem) {
        preferencesManager.saveLastLocation(location)
        if (_currentLocation.value.latitude != location.latitude ||
            _currentLocation.value.longitude != location.longitude) {
            _currentLocation.value = location
            loadForecast(forceRefresh = false)
        }
    }

    fun requestGpsLocation(
        onSuccess: ((LocationItem) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (_isLocatingGps.value) return
        viewModelScope.launch {
            _isLocatingGps.value = true
            _gpsMessage.value = "gps_locating"
            try {
                val loc = locationTracker.getCurrentLocation()
                if (loc != null) {
                    selectLocation(loc)
                    // Also save to database so it stays in user's saved list
                    repository.saveLocation(loc)
                    _gpsMessage.value = null
                    onSuccess?.invoke(loc)
                } else {
                    _gpsMessage.value = "gps_error"
                    onError?.invoke("gps_error")
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "GPS lookup error", e)
                _gpsMessage.value = "gps_error"
                onError?.invoke("gps_error")
            } finally {
                _isLocatingGps.value = false
            }
        }
    }

    fun clearGpsMessage() {
        _gpsMessage.value = null
    }

    fun selectCategory(category: ForecastCategory) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            // Auto switch to first model of that category if current model is not in this category
            val modelsForCategory = WeatherModel.forCategory(category)
            if (!modelsForCategory.contains(_selectedModel.value)) {
                _selectedModel.value = modelsForCategory.firstOrNull() ?: WeatherModel.ECMWF_IFS
            }
            loadForecast(forceRefresh = false)
        }
    }

    fun selectModel(model: WeatherModel) {
        if (_selectedModel.value != model) {
            _selectedModel.value = model
            // Keep category aligned if model belongs to another category
            if (!model.categories.contains(_selectedCategory.value)) {
                model.categories.firstOrNull()?.let { _selectedCategory.value = it }
            }
            loadForecast(forceRefresh = false)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        preferencesManager.saveLanguage(lang)
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        preferencesManager.saveThemeMode(mode)
    }

    fun refreshForecast() {
        loadForecast(forceRefresh = true)
    }

    private fun loadForecast(forceRefresh: Boolean) {
        fetchJob?.cancel()
        val loc = _currentLocation.value
        val model = _selectedModel.value
        _forecastState.value = ForecastUiState.Loading

        fetchJob = viewModelScope.launch {
            repository.getWeatherForecast(loc, model, forceRefresh)
                .catch { e ->
                    _forecastState.value = ForecastUiState.Error(
                        message = e.localizedMessage ?: "Failed to fetch weather data"
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { data ->
                            _forecastState.value = ForecastUiState.Success(data)
                        },
                        onFailure = { e ->
                            _forecastState.value = ForecastUiState.Error(
                                message = e.localizedMessage ?: "Failed to load weather data"
                            )
                        }
                    )
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(350) // debounce
            val results = repository.searchLocations(query)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun addLocationToSaved(location: LocationItem) {
        viewModelScope.launch {
            repository.saveLocation(location.copy(isFavorite = true))
        }
    }

    fun deleteLocation(id: Long) {
        viewModelScope.launch {
            repository.deleteLocation(id)
        }
    }

    fun selectNavTab(tab: AppNavTab) {
        _currentNavTab.value = tab
    }

    fun selectRadarSubTab(tab: RadarSatelliteSubTab) {
        _radarSubTab.value = tab
    }

    fun setRadarFrameIndex(index: Int) {
        val total = _nationalRadarFrames.value.size
        if (total > 0) {
            _currentRadarFrameIndex.value = index.coerceIn(0, total - 1)
        }
    }

    fun stepRadarFrame(forward: Boolean) {
        val frames = _nationalRadarFrames.value
        if (frames.isEmpty()) return
        val current = _currentRadarFrameIndex.value
        val next = if (forward) {
            (current + 1) % frames.size
        } else {
            if (current - 1 < 0) frames.size - 1 else current - 1
        }
        _currentRadarFrameIndex.value = next
    }

    fun toggleRadarPlayback() {
        _isRadarPlaying.value = !_isRadarPlaying.value
    }

    fun setRadarPlaying(playing: Boolean) {
        _isRadarPlaying.value = playing
    }

    fun setRadarOpacity(opacity: Float) {
        _radarOpacity.value = opacity.coerceIn(0.1f, 1.0f)
    }

    fun setRadarPlaybackSpeed(speedMs: Long) {
        _radarPlaybackSpeedMs.value = speedMs
    }

    fun selectRadarStation(station: RadarStationInfo) {
        if (_selectedRadarStation.value.code != station.code) {
            _selectedRadarStation.value = station
            loadStationFiles(station.code)
        }
    }

    fun selectMeteosatChannel(channel: MeteosatChannelInfo) {
        if (_selectedMeteosatChannel.value.tipNumber != channel.tipNumber) {
            _selectedMeteosatChannel.value = channel
            loadMeteosatFrames(channel.tipNumber)
        }
    }

    fun selectMeteosatFrame(frame: MeteosatFrame) {
        _selectedMeteosatFrame.value = frame
    }

    fun setRainViewerIndex(index: Int) {
        val total = _rainViewerFrames.value.size
        if (total > 0) {
            _currentRainViewerIndex.value = index.coerceIn(0, total - 1)
        }
    }

    fun stepRainViewerFrame(forward: Boolean) {
        val frames = _rainViewerFrames.value
        if (frames.isEmpty()) return
        val current = _currentRainViewerIndex.value
        val next = if (forward) {
            (current + 1) % frames.size
        } else {
            if (current - 1 < 0) frames.size - 1 else current - 1
        }
        _currentRainViewerIndex.value = next
    }

    fun toggleRainViewerPlayback() {
        _isRainViewerPlaying.value = !_isRainViewerPlaying.value
    }

    fun setRainViewerPlaying(playing: Boolean) {
        _isRainViewerPlaying.value = playing
    }

    fun loadNationalRadar() {
        radarJob?.cancel()
        radarJob = viewModelScope.launch {
            _isLoadingRadar.value = true
            val frames = AnmRemoteDataSource.getNationalRadarFrames()
            _nationalRadarFrames.value = frames
            if (frames.isNotEmpty()) {
                _currentRadarFrameIndex.value = frames.size - 1
            }
            _isLoadingRadar.value = false
        }
    }

    fun loadStationFiles(stationCode: String) {
        stationFilesJob?.cancel()
        stationFilesJob = viewModelScope.launch {
            _isLoadingStationFiles.value = true
            val files = AnmRemoteDataSource.getStationLatestFiles(stationCode)
            _stationFiles.value = files
            _isLoadingStationFiles.value = false
        }
    }

    fun loadMeteosatFrames(tipNumber: Int) {
        meteosatJob?.cancel()
        meteosatJob = viewModelScope.launch {
            _isLoadingMeteosat.value = true
            val frames = AnmRemoteDataSource.getMeteosatFrames(tipNumber)
            _meteosatFrames.value = frames
            _selectedMeteosatFrame.value = frames.firstOrNull()
            _isLoadingMeteosat.value = false
        }
    }

    fun loadRainViewerFrames() {
        rainViewerJob?.cancel()
        rainViewerJob = viewModelScope.launch {
            _isLoadingRainViewer.value = true
            val frames = AnmRemoteDataSource.getRainViewerRadarFrames()
            _rainViewerFrames.value = frames
            if (frames.isNotEmpty()) {
                _currentRainViewerIndex.value = frames.size - 1
            }
            _isLoadingRainViewer.value = false
        }
    }

    fun loadAnmWarnings() {
        anmJob?.cancel()
        anmJob = viewModelScope.launch {
            _isLoadingAnm.value = true
            val nowcast = AnmRemoteDataSource.getNowcastingWarnings()
            val general = AnmRemoteDataSource.getGeneralWarnings()
            _nowcastingWarnings.value = nowcast
            _generalWarnings.value = general
            _isLoadingAnm.value = false
        }
    }

    // --- EUMETSAT / MTG Satellite Methods ---
    fun selectEumetsatLayer(mode: EumetsatLayerMode) {
        _selectedEumetsatLayerMode.value = mode
        loadEumetsatFrames(mode)
    }

    fun loadEumetsatFrames(mode: EumetsatLayerMode = _selectedEumetsatLayerMode.value) {
        eumetsatJob?.cancel()
        eumetsatJob = viewModelScope.launch {
            _isLoadingEumetsat.value = true
            val frames = AnmRemoteDataSource.getEumetsatFrames(mode)
            _eumetsatFrames.value = frames
            if (frames.isNotEmpty()) {
                _currentEumetsatIndex.value = frames.size - 1
            }
            _isLoadingEumetsat.value = false
        }
    }

    fun setEumetsatIndex(index: Int) {
        if (index in 0 until _eumetsatFrames.value.size) {
            _currentEumetsatIndex.value = index
        }
    }

    fun toggleEumetsatPlayback() {
        _isEumetsatPlaying.value = !_isEumetsatPlaying.value
    }

    fun stepEumetsatFrame(forward: Boolean) {
        val frames = _eumetsatFrames.value
        if (frames.isEmpty()) return
        val current = _currentEumetsatIndex.value
        val next = if (forward) {
            (current + 1) % frames.size
        } else {
            if (current - 1 < 0) frames.size - 1 else current - 1
        }
        _currentEumetsatIndex.value = next
    }
}
