package com.example.mobilepopmasterr.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.mobilepopmasterr.R
import com.example.mobilepopmasterr.ui.theme.LightPink

data class StatisticItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Int? = null
)

data class GameStatistics(
    val totalScore: Int = 0,
    val gamesPlayed: Int = 0,
    val averageScore: Int = 0,
    val perfectGuesses: Int = 0,
    val currentStreak: Int = 0,
    val highestStreak: Int = 0
) {
    // The icons currently are kinda random, may change!
    @Composable
    fun toStatisticItems(): List<StatisticItem> = listOf(
        StatisticItem(
            title = stringResource(R.string.games_played),
            value = gamesPlayed.toString(),
            icon = Icons.Default.Games
        ),
        StatisticItem(
            title = stringResource(R.string.total_score),
            value = totalScore.toString(),
            icon = Icons.Default.Star
        ),
        StatisticItem(
            title = stringResource(R.string.average_score),
            value = averageScore.toString(),
            icon = Icons.AutoMirrored.Filled.TrendingUp
        ),
        StatisticItem(
            title = stringResource(R.string.perfect_guesses),
            value = perfectGuesses.toString(),
            icon = Icons.Default.EmojiEvents
        ),
        StatisticItem(
            title = stringResource(R.string.current_streak),
            value = currentStreak.toString(),
            icon = Icons.Default.Timeline,
            color = LightPink.toArgb()
        ),
        StatisticItem(
            title = stringResource(R.string.best_streak),
            value = highestStreak.toString(),
            icon = Icons.Default.Psychology,
            color = LightPink.toArgb()
        )
    )
}