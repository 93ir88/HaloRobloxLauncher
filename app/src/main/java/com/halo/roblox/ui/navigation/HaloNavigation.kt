package com.halo.roblox.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.halo.roblox.ui.screens.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home       : Screen("home",            "Home",    Icons.Default.Home)
    object Discover   : Screen("discover",        "Discover",Icons.Default.Search)
    object Settings   : Screen("settings",        "Settings",Icons.Default.Settings)
    object GameDetail : Screen("game/{universeId}/{placeId}", "Detail", Icons.Default.Home)
}

@Composable
fun HaloNavHost() {
    val navController = rememberNavController()

    val tabs = listOf(Screen.Home, Screen.Discover, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val current = navBackStackEntry?.destination
                tabs.forEach { screen ->
                    NavigationBarItem(
                        selected  = current?.hierarchy?.any { it.route == screen.route } == true,
                        onClick   = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon      = { Icon(screen.icon, contentDescription = screen.label) },
                        label     = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(padding),
            enterTransition  = { fadeIn() + slideInHorizontally { it / 8 } },
            exitTransition   = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition  = { fadeOut() + slideOutHorizontally { it / 8 } }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onGameClick = { game ->
                        navController.navigate(
                            "game/${game.universeId}/${game.placeId}"
                        )
                    }
                )
            }
            composable(Screen.Discover.route) {
                DiscoverScreen(
                    onGameClick = { game ->
                        navController.navigate(
                            "game/${game.universeId}/${game.placeId}"
                        )
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(
                route     = Screen.GameDetail.route,
                arguments = listOf(
                    navArgument("universeId") { type = NavType.LongType },
                    navArgument("placeId")    { type = NavType.LongType }
                )
            ) { back ->
                val universeId = back.arguments?.getLong("universeId") ?: 0L
                val placeId    = back.arguments?.getLong("placeId")    ?: 0L
                GameDetailScreen(
                    universeId = universeId,
                    placeId    = placeId,
                    onBack     = { navController.popBackStack() }
                )
            }
        }
    }
}
