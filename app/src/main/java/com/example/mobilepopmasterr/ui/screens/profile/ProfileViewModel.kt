package com.example.mobilepopmasterr.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobilepopmasterr.data.DataStoreManager
import com.example.mobilepopmasterr.data.GameStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _statistics = MutableStateFlow(GameStatistics())
    val statistics = _statistics.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                _statistics.value = GameStatistics(
                    totalScore = dataStoreManager.getTotalScore(),
                    gamesPlayed = dataStoreManager.getGamesPlayed(),
                    averageScore = dataStoreManager.getAverageScore(),
                    perfectGuesses = dataStoreManager.getPerfectGuesses(),
                    currentStreak = dataStoreManager.getCurrentStreak(),
                    highestStreak = dataStoreManager.getHighestStreak()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reloadStatistics() {
        loadStatistics()
    }
}

// I don't get factories, this was with claude
class ProfileViewModelFactory(
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}