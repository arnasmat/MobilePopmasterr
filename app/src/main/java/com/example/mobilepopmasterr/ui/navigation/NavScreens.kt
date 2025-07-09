package com.example.mobilepopmasterr.ui.navigation

import androidx.annotation.StringRes
import com.example.mobilepopmasterr.R

enum class Screens(@StringRes val title: Int) {
    Home(title = R.string.home),
    SignIn(title = R.string.signin),
    Settings(title = R.string.settings),
    GameModeSelection(title = R.string.game_mode_selection),
    ClassicGame(title = R.string.classic_game),
    StreakGame(title = R.string.streak_game),
    Profile(title = R.string.profile),
    Leaderboard(title = R.string.leaderboard),
    About(title = R.string.about),
}