package com.example.mobilepopmasterr

//import com.example.mobilepopmasterr.ui.navigation.PopmasterrApp
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilepopmasterr.ui.navigation.Screens
import com.example.mobilepopmasterr.ui.screens.profile.ProfileScreen
import com.example.mobilepopmasterr.ui.screens.classicGame.ClassicGameScreen
import com.example.mobilepopmasterr.ui.screens.signIn.GoogleAuthUIClient
import com.example.mobilepopmasterr.ui.screens.signIn.SignInScreen
import com.example.mobilepopmasterr.ui.screens.signIn.SignInViewmodel
import com.example.mobilepopmasterr.ui.screens.streakGame.StreakGameScreen
import com.example.mobilepopmasterr.ui.theme.MobilePopmasterrTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobilePopmasterrTheme {
                val navController = rememberNavController()
                val googleAuthUIClient by lazy {
                    GoogleAuthUIClient(
                        context = applicationContext,
                        oneTapClient = Identity.getSignInClient(applicationContext)
                    )
                }

                // TODO: make a bottom navigation bar with a scaffold
                // TODO: streak gamemode, settings, gamemodeselection
                // if I have time: leaderboard? that is if I do it with db

                NavHost(
                    navController = navController,
                    startDestination = Screens.SignIn.name,
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    composable(route = Screens.Home.name) {}
                    composable(route = Screens.SignIn.name) {
                        val viewModel = viewModel<SignInViewmodel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(
                            key1 = Unit
                        ) {
                            if (googleAuthUIClient.getSignedInUser() != null) {
                                navController.navigate(Screens.Profile.name)
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUIClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        viewModel.onSignInResult(signInResult)

                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if(state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate(Screens.Profile.name)
                                viewModel.resetState()
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUIClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )

                    }


                    composable(route = Screens.Settings.name) {}
                    composable(route = Screens.GameModeSelection.name) {}
                    composable(route = Screens.ClassicGame.name) {
                        ClassicGameScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(route = Screens.StreakGame.name) {
                        StreakGameScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(route = Screens.Profile.name) {
                        ProfileScreen(
                            userData = googleAuthUIClient.getSignedInUser(),
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUIClient.signOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Signed out successfully",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.popBackStack()
                                }
                            },
                            onGameStart = { navController.navigate(Screens.ClassicGame.name) },
                            onStreakStart = { navController.navigate(Screens.StreakGame.name) },
                        )
                    }
                    composable(route = Screens.Leaderboard.name) {}
                    composable(route = Screens.About.name) {}
                }
            }
        }
                }
            }