package com.example.mobilepopmasterr.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(
    private val context: Context,
) {

    // Classic stats
    private val TOTAL_SCORE = intPreferencesKey("total_score")
    private val GAMES_PLAYED = intPreferencesKey("games_played")
    private val PERFECT_GUESSES = intPreferencesKey("perfect_guesses")

    // Streak stats
    private val CURRENT_STREAK = intPreferencesKey("current_streak")
    private val HIGHEST_STREAK = intPreferencesKey("highest_streak")

    // Game settings
    private val MAP_TYPE = intPreferencesKey("map_type")

    private suspend fun getIntKey(key: Preferences.Key<Int>): Int {
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: 0
            }
            .first()
    }

    private suspend fun writeInt(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    // Classic stats increasing functions
    suspend fun increaseTotalScore(score: Int) {
        val currentScore = getIntKey(TOTAL_SCORE)
        writeInt(TOTAL_SCORE, currentScore + score)
    }

    suspend fun increaseGamesPlayed() {
        val currentGamesPlayed = getIntKey(GAMES_PLAYED)
        writeInt(GAMES_PLAYED, currentGamesPlayed + 1)
    }

    suspend fun increasePerfectGuesses() {
        val currentPerfectGuesses = getIntKey(PERFECT_GUESSES)
        writeInt(PERFECT_GUESSES, currentPerfectGuesses + 1)
    }

    // Classic getters
    suspend fun getTotalScore(): Int {
        return getIntKey(TOTAL_SCORE)
    }
    suspend fun getGamesPlayed(): Int {
        return getIntKey(GAMES_PLAYED)
    }
    suspend fun getAverageScore(): Int {
        val totalScore = getTotalScore()
        val gamesPlayed = getGamesPlayed()
        if( gamesPlayed < 0){
            return 0
        } else {
            return totalScore / gamesPlayed
        }
    }
    suspend fun getPerfectGuesses(): Int {
        return getIntKey(PERFECT_GUESSES)
    }

    // --------------------------------------
    // Streak stats increasing functions

    // (increase current and maybe highest)
    suspend fun increaseCurrentStreak() {
        val currentStreak = getIntKey(CURRENT_STREAK)
        val highestStreak = getIntKey(HIGHEST_STREAK)
        if( currentStreak + 1 > highestStreak) {
            writeInt(HIGHEST_STREAK, currentStreak + 1)
        }

        writeInt(CURRENT_STREAK, currentStreak + 1)
    }
    suspend fun resetCurrentStreak() {
        writeInt(CURRENT_STREAK, 0)
    }

    // Streak getters
    suspend fun getCurrentStreak(): Int {
        return getIntKey(CURRENT_STREAK)
    }
    suspend fun getHighestStreak(): Int {
        return getIntKey(HIGHEST_STREAK)
    }

    // --------------------------------------
    // Game settings
    /* I dislike that I decode and encode into ints. In the future I'd like to figure out a better
     solution to store enum types in DataStore.*/
    suspend fun getMapType(): MapType {
        val mapType = getIntKey(MAP_TYPE)
        return when(mapType){
            0 -> MapType.NORMAL
            1 -> MapType.SATELLITE
            2 -> MapType.HYBRID
            3 -> MapType.TERRAIN
            else -> MapType.NORMAL
        }
    }

    suspend fun setMapType(mapType: MapType) {
        val intMapType = when(mapType) {
            MapType.NORMAL -> 0
            MapType.SATELLITE -> 1
            MapType.HYBRID -> 2
            MapType.TERRAIN -> 3
            else -> 0
        }
        writeInt(MAP_TYPE, intMapType)
    }


}