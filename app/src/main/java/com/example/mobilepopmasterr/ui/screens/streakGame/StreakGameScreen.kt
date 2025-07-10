package com.example.mobilepopmasterr.ui.screens.streakGame

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilepopmasterr.data.DataStoreManager
import com.example.mobilepopmasterr.ui.Rectangle
import com.example.mobilepopmasterr.ui.components.BackToGameSelectionButton
import com.example.mobilepopmasterr.ui.components.LoadingIndicator
import com.example.mobilepopmasterr.ui.components.formatPopulationString
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

/*
*                                       STREAK GAME SCREEN
* This is the main screen for the streak game mode.
* The user is given two rectangles on the map and must select which one has a higher population
* and collect as high of a streak (correct guesses in a row) as possible
*
*/

//TODO: improve the code here, make a game selection menu screen thing!
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakGameScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val viewModel: StreakGameViewModel = viewModel(
        factory = StreakGameViewModelFactory(dataStoreManager)
    )

    val gameState by viewModel.gameState.collectAsState()

    val defaultLocation = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 0f)
    }

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 80.dp,
        modifier = modifier.fillMaxSize(),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp)
                    .navigationBarsPadding()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.Top
            ) {
                if (!gameState.showResult) {
                    if (gameState.isLoadingRectangles) {
                        LoadingIndicator()
                    } else {
                        StreakGameChoiceSection(
                            viewModel = viewModel,
                            gameState = gameState,
                            cameraPositionState = cameraPositionState
                        )
                    }
                } else {
                    StreakGameResultSection(
                        gameState = gameState,
                        viewModel = viewModel
                    )
                }
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            MapWithTwoRectangles(
                viewModel = viewModel,
                gameState = gameState,
                cameraPositionState = cameraPositionState,
            )

            BackToGameSelectionButton(
                onBackClick = onBackClick
            )

            // Current streak thingy in top right
            StreakCounter(
                currentStreak = gameState.currentStreak,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
            )

            // ! Helper effects

            // Move camera when both map and rectangles are loaded
            LaunchedEffect(gameState.isMapLoaded, gameState.blueRectangle, gameState.redRectangle) {
                if (gameState.isMapLoaded && gameState.blueRectangle != null && gameState.redRectangle != null) {
                    moveCameraToShowBothRectangles(
                        cameraPositionState,
                        gameState.blueRectangle!!.rectangle,
                        gameState.redRectangle!!.rectangle
                    )
                }
            }

            // Show errors via toast
            val context = LocalContext.current
            LaunchedEffect(gameState.errorMessage) {
                gameState.errorMessage?.let { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    viewModel.resetErrorMessage()
                }
            }
        }
    }
}


@Composable
private fun StreakGameChoiceSection(
    viewModel: StreakGameViewModel,
    gameState: StreakGameState,
    cameraPositionState: CameraPositionState,
) {

    Text(
        text = "Which rectangle has a higher population?",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GuessButton(
            viewModel = viewModel,
            onClick = { viewModel.submitGuess(RectangleColor.BLUE) },
            colorText = "Blue",
            containerColor = Color(0xFF3db0cc),
            modifier = Modifier.weight(1f)
        )

        GuessButton(
            viewModel = viewModel,
            onClick = { viewModel.submitGuess(RectangleColor.RED) },
            colorText = "Red",
            containerColor = Color(0xFFFF6B35),
            modifier = Modifier.weight(1f)
        )
    }
    if(gameState.redRectangle != null && gameState.blueRectangle != null) {
        CantFindRectangleClickableText(
            colorString = "Blue",
            color = Color(0xFF3db0cc),
            rectangle = gameState.blueRectangle.rectangle,
            cameraPositionState = cameraPositionState,
        )

        CantFindRectangleClickableText(
            colorString = "Red",
            color = Color(0xFFFF6B35),
            rectangle = gameState.redRectangle.rectangle,
            cameraPositionState = cameraPositionState,
        )
    }
}

@Composable
private fun CantFindRectangleClickableText(
    colorString: String,
    color: Color,
    rectangle: Rectangle,
    cameraPositionState: CameraPositionState,
){
    val coroutineScope = rememberCoroutineScope()

    Text(
        text = "Can't find the $colorString rectangle? Click here!",
        color = color,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    moveCameraToShowOneRectangle(cameraPositionState, rectangle)
                }
            }
    )
}

@Composable
private fun GuessButton(
    viewModel: StreakGameViewModel,
    onClick: () -> Unit,
    colorText: String,
    containerColor: Color ,
    modifier: Modifier = Modifier,
){
    Button(
        enabled = viewModel.isGuessEnabled(),
        onClick = onClick,
        modifier = modifier
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        )
    ) {
        Text(
            text = colorText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun StreakGameResultSection(
    gameState: StreakGameState,
    viewModel: StreakGameViewModel
) {
    StreakResultDisplay(
        gameState = gameState
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (gameState.gameEnded) {
        Button(
            onClick = { viewModel.playAgain() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp)
        ) {
            Text("Start New Game")
        }
    } else {
        Button(
            onClick = { viewModel.playAgain() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp)
        ) {
            Text("Next Round")
        }
    }
}

@Composable
private fun StreakResultDisplay(
    gameState: StreakGameState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (gameState.guessCorrect == true) {
                Text(
                    text = "Correct! 🎉",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Wrong! :(",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                // the string building was written w/ ai, didn't even know you could do it like that. quite cool
                text = buildAnnotatedString {
                    append("Blue: ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3db0cc)
                        )
                    ) {
                        append(formatPopulationString(gameState.blueRectangle?.population.toString()))
                    }
                    append("\nRed: ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B35)
                        )
                    ) {
                        append(formatPopulationString(gameState.redRectangle?.population.toString()))
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            if (gameState.gameEnded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Final Streak: ${gameState.currentStreak}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StreakCounter(
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Timeline,
                contentDescription = "Streak",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = currentStreak.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MapWithTwoRectangles(
    viewModel: StreakGameViewModel,
    gameState: StreakGameState,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = false,
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = MapProperties(
                mapType = gameState.mapType,
            ),
            onMapLoaded = {
                viewModel.updateMapLoadedState(true)
            },
        ) {
            gameState.blueRectangle?.let { rectangleWithPop ->
                Polygon(
                    points = rectangleWithPop.rectangle.getRectangle(),
                    fillColor = Color(0x803db0cc),
                    strokeColor = Color(0xFF3db0cc),
                    strokeWidth = 4f,
                    geodesic = false
                )
            }

            gameState.redRectangle?.let { rectangleWithPop ->
                Polygon(
                    points = rectangleWithPop.rectangle.getRectangle(),
                    fillColor = Color(0x80FF6B35),
                    strokeColor = Color(0xFFFF6B35),
                    strokeWidth = 4f,
                    geodesic = false
                )
            }
        }
    }
}

private suspend fun moveCameraToShowBothRectangles(
    cameraPositionState: CameraPositionState,
    rectangleA: Rectangle,
    rectangleB: Rectangle
) {
    val bounds = LatLngBounds.builder()
        .include(rectangleA.pos1)
        .include(rectangleA.pos2)
        .include(rectangleB.pos1)
        .include(rectangleB.pos2)
        .build()

    // Calculate dynamic padding based on bounds size
    val northeast = bounds.northeast
    val southwest = bounds.southwest
    val latSpan = northeast.latitude - southwest.latitude
    val lngSpan = northeast.longitude - southwest.longitude
    val maxSpan = maxOf(latSpan, lngSpan)

    // Use percentage-based padding that scales with rectangle size
    val padding = when {
        maxSpan > 50 -> 50  // Very large rectangles - minimal padding
        maxSpan > 20 -> 100 // Large rectangles - small padding
        maxSpan > 5 -> 150  // Medium rectangles - medium padding
        else -> 200         // Small rectangles - more padding for better visibility
    }

    cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, padding))
}

private suspend fun moveCameraToShowOneRectangle(
    cameraPositionState: CameraPositionState,
    rectangle: Rectangle
) {
    val bounds = LatLngBounds.builder()
        .include(rectangle.pos1)
        .include(rectangle.pos2)
        .build()

    cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 200))
}

@Preview
@Composable
private fun PreviewStreakGameScreen() {
    StreakGameScreen(modifier = Modifier.fillMaxSize())
}
