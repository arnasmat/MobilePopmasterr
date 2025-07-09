// app/src/main/java/com/example/mobilepopmasterr/data/StatisticsData.kt
package com.example.mobilepopmasterr.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
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
    // random icons for now, may change later
    fun toStatisticItems(): List<StatisticItem> = listOf(
        StatisticItem(
            title = "Games Played",
            value = gamesPlayed.toString(),
            icon = Icons.Default.Games
        ),
        StatisticItem(
            title = "Total Score",
            value = totalScore.toString(),
            icon = Icons.Default.Star
        ),
        StatisticItem(
            title = "Average Score",
            value = averageScore.toString(),
            icon = Icons.AutoMirrored.Filled.TrendingUp
        ),
        StatisticItem(
            title = "Perfect Guesses",
            value = perfectGuesses.toString(),
            icon = Icons.Default.EmojiEvents
        ),
        StatisticItem(
            title = "Current Streak",
            value = currentStreak.toString(),
            icon = Icons.Default.Timeline,
            color = LightPink.toArgb()
        ),
        StatisticItem(
            title = "Best Streak",
            value = highestStreak.toString(),
            icon = Icons.Default.Psychology,
            color = LightPink.toArgb()
        )
    )
}