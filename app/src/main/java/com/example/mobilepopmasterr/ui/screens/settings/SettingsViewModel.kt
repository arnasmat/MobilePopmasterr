package com.example.mobilepopmasterr.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.mobilepopmasterr.data.DataStoreManager

data class SettingsState(
    val mapType: MapType = MapType.NORMAL
)

class SettingsViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    init{
        loadSettings()
    }

    private fun loadSettings(){
        viewModelScope.launch {
            try{
                _settingsState.value = _settingsState.value.copy(
                    mapType = dataStoreManager.getMapType()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateMapType(mapType: MapType) {
        viewModelScope.launch {
            try {
                dataStoreManager.setMapType(mapType)
                _settingsState.value = _settingsState.value.copy(mapType = mapType)
            } catch (e: Exception) {
                e.printStackTrace()
                loadSettings()
            }
        }
    }
}

class SettingsViewModelFactory(
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}