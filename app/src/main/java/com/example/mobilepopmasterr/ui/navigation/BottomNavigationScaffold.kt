package com.example.mobilepopmasterr.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mobilepopmasterr.data.bottomNavItems

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BottomNavigationScaffold(
    navController: NavController,
    showBottomBar: Boolean = true,
    content: @Composable (modifier: Modifier) -> Unit
) {
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
                                    // makes it so clicking back button doesn't go to the previous navbar screen cuz that feels weird lol
                                    navController.navigate(item.route) {
                                        popUpTo(Screens.Home.name)
                                        launchSingleTop = true
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
