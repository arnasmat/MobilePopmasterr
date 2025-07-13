package com.example.mobilepopmasterr.ui.screens.streakGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobilepopmasterr.data.DataStoreManager
import com.example.mobilepopmasterr.network.getRectangleAndPopulation
import com.example.mobilepopmasterr.ui.Rectangle
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class RectangleColor{
    BLUE,
    RED
}

//inconsistent, but I also don't wanna have 4 vars there and make it super confusing to read
data class RectangleWithPopulation(
    val rectangle: Rectangle,
    val population: Long
)

data class StreakGameState(
    val blueRectangle: RectangleWithPopulation? = null,
    val redRectangle: RectangleWithPopulation? = null,
    val isLoadingRectangles: Boolean = false,
    val isMapLoaded: Boolean = false,
    val currentStreak: Int = 0,
    val gameEnded: Boolean = false,
    val guessCorrect: Boolean? = null,
    val showResult: Boolean = false,
    val errorMessage: String? = null,
    val isWaitingForNextRound: Boolean = false,
    val mapType: MapType = MapType.NORMAL,
)

class StreakGameViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _gameState = MutableStateFlow(StreakGameState())
    val gameState = _gameState.asStateFlow()

    init {
        loadCurrentStreak()
        loadRectangles()
        loadMapType()
    }

    private fun loadCurrentStreak() {
        viewModelScope.launch {
            try {
                val currentStreak = dataStoreManager.getCurrentStreak()
                _gameState.value = _gameState.value.copy(
                    currentStreak = currentStreak
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _gameState.value = _gameState.value.copy(
                    errorMessage = "Failed to load current streak: ${e.message}"
                )
            }
        }
    }

    private fun loadRectangles() {
        _gameState.value = _gameState.value.copy(
            isLoadingRectangles = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val (blueRectangle, bluePopulation) = getRectangleAndPopulation()
                val (redRectangle, redPopulation) = getRectangleAndPopulation()

                if (bluePopulation == null || redPopulation == null) {
                    throw IllegalStateException("Failed to load rectangle populations")
                }

                val blueRectWithPop = RectangleWithPopulation(
                    rectangle = blueRectangle,
                    population = bluePopulation.toLongOrNull() ?: 0L
                )

                val redRectWithPop = RectangleWithPopulation(
                    rectangle = redRectangle,
                    population = redPopulation.toLongOrNull() ?: 0L
                )

                _gameState.value = _gameState.value.copy(
                    blueRectangle = blueRectWithPop,
                    redRectangle = redRectWithPop,
                    isLoadingRectangles = false
                )

            } catch (e: Exception) {
                e.printStackTrace()
                _gameState.value = _gameState.value.copy(
                    isLoadingRectangles = false,
                    errorMessage = "Failed to load rectangles: ${e.message}"
                )
            }
        }
    }

    private fun loadMapType() {
        viewModelScope.launch {
            try {
                val mapType = dataStoreManager.getMapType()
                _gameState.value = _gameState.value.copy(
                    mapType = mapType
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // if loading fails -> use default type
                _gameState.value = _gameState.value.copy(
                    mapType = MapType.NORMAL
                )
            }
        }
    }

    fun submitGuess(chosenRectangle: RectangleColor) {
        val state = _gameState.value

        if (state.blueRectangle == null || state.redRectangle == null || state.gameEnded) {
            return
        }

        val isCorrect = when (chosenRectangle) {
            RectangleColor.BLUE -> state.blueRectangle.population >= state.redRectangle.population
            RectangleColor.RED -> state.redRectangle.population >= state.blueRectangle.population
        }

        handleDataStoreGuess(isCorrect, state)
    }

    // Note that this handles it more than just for the data store, also updates game state things
    private fun handleDataStoreGuess(isCorrect: Boolean, state: StreakGameState) {
        viewModelScope.launch {
            try {
                if (isCorrect) {
                    dataStoreManager.increaseCurrentStreak()
                    val newStreak = state.currentStreak + 1
                    _gameState.value = state.copy(
                        currentStreak = newStreak,
                        guessCorrect = true,
                        showResult = true,
                        isWaitingForNextRound = true
                    )
                } else {
                    dataStoreManager.resetCurrentStreak()
                    _gameState.value = state.copy(
                        gameEnded = true,
                        guessCorrect = false,
                        showResult = true
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _gameState.value = _gameState.value.copy(
                    errorMessage = "Failed to handle storing game: ${e.message}"
                )
            }
        }
    }

    fun playAgain() {
        _gameState.value = StreakGameState(
            isMapLoaded = _gameState.value.isMapLoaded,
            mapType = _gameState.value.mapType,
        )
        loadCurrentStreak()
        loadRectangles()
    }

    fun updateMapLoadedState(isLoaded: Boolean) {
        _gameState.value = _gameState.value.copy(
            isMapLoaded = isLoaded
        )
    }

    fun resetErrorMessage() {
        _gameState.value = _gameState.value.copy(
            errorMessage = null
        )
    }

    fun isGuessEnabled(): Boolean {
        val state = _gameState.value
        return !state.isLoadingRectangles &&
               !state.gameEnded &&
               !state.showResult &&
               state.blueRectangle != null &&
               state.redRectangle != null &&
               state.isMapLoaded
    }
}

class StreakGameViewModelFactory(
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreakGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StreakGameViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}