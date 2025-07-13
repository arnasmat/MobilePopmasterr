package com.example.mobilepopmasterr.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mobilepopmasterr.R
import com.example.mobilepopmasterr.data.getBottomNavItems

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/*           BOTTOM NAVIGATION SCAFFOLD
* The thingy on the bottom of the screen that lets the user navigate between important screens
* (like on most social medias lol), here they can go to home screen, profile and settings
*
* */

@Composable
fun BottomNavigationScaffold(
    navController: NavController,
    showBottomBar: Boolean = true,
    content: @Composable (modifier: Modifier) -> Unit
) {

    val bottomNavItems = getBottomNavItems()

    if (showBottomBar) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.route == item.route

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    // the restorestates and savestates are to solve an odd bug. may not be the ideal solution for it
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            content(Modifier.padding(paddingValues))
        }
    } else {
        // if hidden, no padding (messes things up in game screens)
        content(Modifier)
    }
}
