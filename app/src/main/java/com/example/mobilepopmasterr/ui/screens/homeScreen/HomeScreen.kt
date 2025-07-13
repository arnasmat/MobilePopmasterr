package com.example.mobilepopmasterr.ui.screens.homeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilepopmasterr.R
import com.example.mobilepopmasterr.data.DataStoreManager
import com.example.mobilepopmasterr.ui.screens.signIn.UserData

/*
*           HOME AND GAMEMODE SELECTION SCREEN
* The main screen for the game, which just says hi to the user and lets them choose the gamemode
*
*/

@Composable
fun HomeScreen(
    userData: UserData?,
    onNavigateToClassicGame: () -> Unit = {},
    onNavigateToStreakGame: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    var totalScore by remember { mutableIntStateOf(0) }
    var currentStreak by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        totalScore = dataStoreManager.getTotalScore()
        currentStreak = dataStoreManager.getCurrentStreak()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            WelcomeSection(
                userData = userData,
                onNavigateToProfile = onNavigateToProfile
            )
        }

        item {
            GameModeSelectionSection(
                totalScore = totalScore,
                currentStreak = currentStreak,
                onNavigateToClassicGame = onNavigateToClassicGame,
                onNavigateToStreakGame = onNavigateToStreakGame
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WelcomeSection(
    userData: UserData?,
    onNavigateToProfile: () -> Unit,
) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                // (For when I add anonymous sign in)
                text = if (userData?.username != null) {
                    stringResource(R.string.welcome_back_to_popmasterr, userData.username)
                } else {
                    stringResource(R.string.welcome_to_popmasterr)
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )

            if (userData?.username != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.tap_to_view_your_profile),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable {
                        onNavigateToProfile()
                    }
                )
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameModeSelectionSection(
    totalScore: Int,
    currentStreak: Int,
    onNavigateToClassicGame: () -> Unit,
    onNavigateToStreakGame: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.choose_your_game_mode),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GameModeCard(
                title = stringResource(R.string.classic_mode),
                description = stringResource(R.string.guess_the_population_within_a_given_rectangle),
                icon = Icons.Default.EmojiEvents,
                statLabel = stringResource(R.string.total_score_homescreen),
                statValue = totalScore.toString(),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = onNavigateToClassicGame
            )

            GameModeCard(
                title = stringResource(R.string.streak_mode),
                description = stringResource(R.string.guess_which_rectangle_has_a_higher_population),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                statLabel = stringResource(R.string.current_streak_home),
                statValue = currentStreak.toString(),
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = onNavigateToStreakGame
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    statLabel: String,
    statValue: String,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statLabel,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statValue,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )

            }
        }
    }
}


@Preview (showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        userData = UserData(
            userId = "123",
            username = "John Doe",
            profilePictureUrl = null
        ),
    )
}