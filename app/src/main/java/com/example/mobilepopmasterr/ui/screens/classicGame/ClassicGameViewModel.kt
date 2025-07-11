package com.example.mobilepopmasterr.ui.screens.classicGame

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
import kotlin.math.*


object ClassicGameConstants {
    const val MAX_POPULATION_GUESS = 10_000_000_001L
    const val MAX_INPUT_DIGITS = 11
}

data class ClassicGameState(
    val rectangle: Rectangle? = null,
    val isRectangleLoadStarted: Boolean = false,
    val isRectangleLoaded: Boolean = false,
    var isMapLoaded: Boolean = false,
    val isGuessClicked: Boolean = false,
    val actualPopulation: Long? = null,
    val guessedPopulation: Long? = null,
    val currentInputGuess: String = "",
    val errorMessage: String? = null,
    val mapType: MapType = MapType.NORMAL,
)

class ClassicGameViewModel(
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    private val _gameState = MutableStateFlow(ClassicGameState())
    val gameState = _gameState.asStateFlow()

    init {
        loadRectangle()
        loadMapType()
    }

    fun loadRectangle() {
        _gameState.value = _gameState.value.copy(
            isRectangleLoadStarted = true,
        )
        viewModelScope.launch {
            try {
                val (rectangle, population) = getRectangleAndPopulation()
                if(population == null){
                    throw IllegalStateException("Failed to load rectangle or population")
                }
                _gameState.value = _gameState.value.copy(
                    rectangle = rectangle,
                    isRectangleLoaded = true,
                    actualPopulation = population.toLongOrNull()
                )
            } catch (e: Exception){
                e.printStackTrace()
                _gameState.value = _gameState.value.copy(
                    isRectangleLoadStarted = false,
                    isRectangleLoaded = false,
                    errorMessage = e.message
                )
            }
        }
    }


    private fun loadMapType() {
        viewModelScope.launch {
            val mapType = dataStoreManager.getMapType()
            _gameState.value = _gameState.value.copy(
                mapType = mapType
            )
        }
    }

    fun submitGuess(guessedPopulation: String?) {
        //theoretically the button shouldn't be clickable if these are equal to null, but just in case ig
        if (_gameState.value.rectangle == null || _gameState.value.actualPopulation == null) {
            _gameState.value = _gameState.value.copy(
                errorMessage = "Map isn't loaded. How did you even click the button?"
            )
            return
        }

        val guessedPopulationLong = try {
            guessedPopulation?.toLongOrNull()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            _gameState.value = _gameState.value.copy(
                errorMessage = "Please enter only numbers"
            )
            return
        }

        if (guessedPopulationLong == null || guessedPopulationLong < 0) {
            _gameState.value = _gameState.value.copy(
                errorMessage = "Please enter non-zero and positive numbers."
            )
            return
        }
        if (guessedPopulationLong > ClassicGameConstants.MAX_POPULATION_GUESS) {
            _gameState.value = _gameState.value.copy(
                errorMessage = "Population guess is too large. How did you even type that?"
            )
            return
        }

        _gameState.value = _gameState.value.copy(
            isGuessClicked = true,
            guessedPopulation = guessedPopulationLong,
        )

        handleDataStoreGuess()
    }

// Handle storing the guess in the local data storage/DataStore
    private fun handleDataStoreGuess(){
        viewModelScope.launch {
            val score = calculateScore()
            dataStoreManager.increaseTotalScore(score)
            dataStoreManager.increaseGamesPlayed()

            if (score == 5000) {
                dataStoreManager.increasePerfectGuesses()
            }
        }
    }


    fun playAgain() {
        val currentMapLoadedState = _gameState.value.isMapLoaded
        _gameState.value = ClassicGameState(
            isMapLoaded = currentMapLoadedState,
            currentInputGuess = "",
            mapType = _gameState.value.mapType,
        )
        loadRectangle()
    }

    fun updateUserGuess(guess: String) {
        if (guess.all { it.isDigit() } || guess.isEmpty()) {
            _gameState.value = _gameState.value.copy(
                currentInputGuess = guess
            )
        }
    }

    fun updateMapLoadedState(isLoaded: Boolean) {
        _gameState.value = _gameState.value.copy(
            isMapLoaded = isLoaded
        )
    }

    fun calculateScore(): Int {
        val guessed = _gameState.value.guessedPopulation ?: return 0
        val actual = _gameState.value.actualPopulation ?: return 0
        return calculateScore(guessed, actual)
    }

    fun isGuessEnabled(): Boolean {
        return _gameState.value.isRectangleLoaded &&
                _gameState.value.isMapLoaded &&
                !_gameState.value.isGuessClicked &&
                _gameState.value.currentInputGuess.isNotEmpty()
    }

    fun resetErrorMessage() {
        _gameState.value = _gameState.value.copy(
            errorMessage = null
        )
    }

}

// Temporary version written by claude, works well enough for now. Will change later.
fun calculateScore(guessedPopulation: Long, actualPopulation: Long): Int {
    // Handle edge cases
    if (actualPopulation <= 0) return 0
    if (guessedPopulation <= 0) return 0

    // Calculate the relative error (percentage difference)
    val relativeError = abs(guessedPopulation.toDouble() - actualPopulation.toDouble()) / actualPopulation.toDouble()

    // If the guess is exactly correct, return maximum score
    if (relativeError == 0.0) return 5000

    // Use logarithmic decay for scoring
    // The score decreases as the relative error increases
    // log(1 + relativeError) provides a smooth decay curve
    val accuracyScore = 1.0 / (1.0 + ln(1.0 + relativeError * 10.0))

    // Apply exponential curve to reward high accuracy more
    val curvedScore = accuracyScore.pow(0.5)

    // Scale to 0-5000 range and round
    val finalScore = (curvedScore * 5000).roundToInt()

    // Ensure minimum score of 1 for any valid guess
    return maxOf(1, minOf(5000, finalScore))
}

class ClassicGameViewModelFactory(
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClassicGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClassicGameViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}