package com.example.mobilepopmasterr.ui.screens.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {}
) {
    // Placeholder for SettingsScreen content
    // This can include settings options, preferences, etc.
    // For now, we will just call the navigation functions passed as parameters
    onNavigateToProfile()
    onNavigateToSignIn()
}