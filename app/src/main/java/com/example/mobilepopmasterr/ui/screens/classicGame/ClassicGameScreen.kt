package com.example.mobilepopmasterr.ui.screens.classicGame

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilepopmasterr.R
import com.example.mobilepopmasterr.data.DataStoreManager
import com.example.mobilepopmasterr.ui.Rectangle
import com.example.mobilepopmasterr.ui.components.BackToGameSelectionButton
import com.example.mobilepopmasterr.ui.components.LoadingIndicator
import com.example.mobilepopmasterr.ui.components.PopulationFormatTransformation
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


/*
*                                       CLASSIC GAME SCREEN
* This is the main screen for the classic game mode.
* The user is given a rectangle on the map and has to guess the population of that rectangle
*
*/

/*
 TODO: KNOWN ISSUES:

- Google maps makes the rectangle mess up around the date line, so sometimes large rectangles
  are not displayed correctly.

 - Backend uses WGS84, this uses web mercator, so the population given here currently is not always reliable.
 (specifically around map distortions)

*/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassicGameScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val viewModel: ClassicGameViewModel = viewModel(
        factory = ClassicGameViewModelFactory(dataStoreManager)
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
    var userGuess by rememberSaveable { mutableStateOf(gameState.currentInputGuess) }

    /*
     Game display.
     Note that the bottom sheet content (i.e. population guess input/results)
     goes first in the code in the BottomSheetScaffold's sheetContent section so reading this may
     be confusing.
     */
    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 80.dp,
        modifier = modifier
            .fillMaxSize(),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp)
                    .navigationBarsPadding()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.Top
            ) {
                if(!gameState.isGuessClicked){
                    if (gameState.isRectangleLoadStarted && !gameState.isRectangleLoaded) {
                        LoadingIndicator()
                    } else {
                        PopulationGuessInputSection(
                            viewModel = viewModel,
                            userGuess = userGuess,
                            onUserGuessChange = { newValue ->
                                userGuess = newValue
                                viewModel.updateUserGuess(newValue)
                            },
                            cameraPositionState = cameraPositionState,
                            gameState = gameState
                        )
                    }
                } else {
                    PopulationResultSection(
                        gameState = gameState,
                        viewModel = viewModel,
                        onUserGuessChange = { newValue ->
                            userGuess = newValue
                            viewModel.updateUserGuess(newValue)
                        }
                    )
                }
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            MapWithRectangle(
                viewModel = viewModel,
                gameState = gameState,
                cameraPositionState = cameraPositionState,
            )

            BackToGameSelectionButton(
                onBackClick = onBackClick
            )

            // ! Helper effects

            LaunchedEffect(gameState.isMapLoaded, gameState.isRectangleLoaded) {
                if(gameState.isMapLoaded && gameState.isRectangleLoaded && gameState.rectangle != null){
                    moveCameraToRectangle(cameraPositionState, gameState.rectangle!!)
                }
            }

            val context = LocalContext.current
            LaunchedEffect(gameState.errorMessage) {
                gameState.errorMessage?.let { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    viewModel.resetErrorMessage()
                    if(!gameState.isRectangleLoadStarted && !gameState.isRectangleLoaded ) {
                        // force navigation back if rectangle load fails
                        onBackClick()
                    }
                }
            }

        }
    }}

@Composable
private fun PopulationGuessInputSection(
    viewModel: ClassicGameViewModel,
    userGuess: String,
    onUserGuessChange: (String) -> Unit,
    cameraPositionState: CameraPositionState,
    gameState: ClassicGameState,
){
    val coroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        value = userGuess,
        onValueChange = { newValue ->
            if ((newValue.all { it.isDigit() } && newValue.length <= ClassicGameConstants.MAX_INPUT_DIGITS) || newValue.isEmpty()) {
                onUserGuessChange(newValue)
                viewModel.updateUserGuess(newValue)
            }
        },
        label = { Text(stringResource(R.string.guess_the_population)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        visualTransformation = PopulationFormatTransformation(),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Button(
        enabled = viewModel.isGuessEnabled(),
        onClick = {
            viewModel.submitGuess(userGuess)
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(stringResource(R.string.submit))
    }
    if(gameState.rectangle != null) {
        Text(
            text = stringResource(R.string.can_t_find_the_rectangle_click_here),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    coroutineScope.launch {
                        moveCameraToRectangle(cameraPositionState, gameState.rectangle)
                    }
                }
        )
    }
}

@Composable
private fun PopulationResultSection(
    gameState: ClassicGameState,
    viewModel: ClassicGameViewModel,
    onUserGuessChange: (String) -> Unit
){
    ResultDisplay(gameState.guessedPopulation ,gameState.actualPopulation, viewModel.calculateScore())
    Button(
        onClick = {
            onUserGuessChange("")
            viewModel.playAgain()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
    ) {
        Text(stringResource(R.string.play_again))
    }
}

@Composable
fun ResultDisplay(
    guessedPopulation: Long?,
    actualPopulation: Long?,
    score: Int?,
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
            Text(
                text = stringResource(R.string.score, score ?: 0),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    (score ?: 0) >= 4000 -> Color(0xFF4CAF50)
                    (score ?: 0) >= 3000 -> Color(0xFF2196F3)
                    (score ?: 0) >= 2000 -> Color(0xFFFF9800)
                    else -> Color(0xFFE91E63)
                },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.you_guessed))
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(formatPopulationString(guessedPopulation.toString()))
                    }
                    append(stringResource(R.string.the_actual_population_is))
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        append(formatPopulationString(actualPopulation.toString()))
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MapWithRectangle(
    viewModel: ClassicGameViewModel,
    gameState: ClassicGameState,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
){

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            rotationGesturesEnabled = false,
            tiltGesturesEnabled = false,
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            onMapLoaded = {
                viewModel.updateMapLoadedState(true)
            },
            properties = MapProperties(
                mapType = gameState.mapType,
            ),
        ) {
            gameState.rectangle?.let { rectangle ->
                Polygon(
                    points = rectangle.getRectangle(),
                    fillColor = Color(0x803db0cc),
                    strokeColor = Color.Blue,
                    strokeWidth = 4f,
                    geodesic = false
                )
            }
        }

    }
}

private suspend fun moveCameraToRectangle(
    cameraPositionState: CameraPositionState,
    rectangle: Rectangle
) {
    val bounds = LatLngBounds.builder()
        .include(rectangle.pos1)
        .include(rectangle.pos2)
        .build()
    cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
}

@Preview
@Composable
private fun PreviewClassicGameScreen(){
    ClassicGameScreen(modifier = Modifier.fillMaxSize())
}