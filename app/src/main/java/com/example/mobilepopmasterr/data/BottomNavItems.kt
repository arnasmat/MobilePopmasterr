package com.example.mobilepopmasterr.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.mobilepopmasterr.R
import com.example.mobilepopmasterr.ui.navigation.BottomNavItem
import com.example.mobilepopmasterr.ui.navigation.Screens

val bottomNavScreens = setOf(
    Screens.Home.name,
    Screens.Profile.name,
    Screens.Settings.name
)

// workaround to get it to work w/ stringresources
@Composable
fun getBottomNavItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            route = Screens.Home.name,
            title = stringResource(R.string.home),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = Screens.Profile.name,
            title = stringResource(R.string.profile),
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        ),
        BottomNavItem(
            route = Screens.Settings.name,
            title = stringResource(R.string.settings),
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )
}