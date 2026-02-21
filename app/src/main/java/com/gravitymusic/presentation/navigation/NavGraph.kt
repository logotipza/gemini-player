package com.gravitymusic.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gravitymusic.presentation.screens.folders.FoldersScreen
import com.gravitymusic.presentation.screens.main.MainScreen
import com.gravitymusic.presentation.screens.player.PlayerScreen
import com.gravitymusic.presentation.screens.playlists.PlaylistsScreen
import com.gravitymusic.presentation.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Main.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Main.route) {
            MainScreen(rootNavController = navController)
        }
        composable(route = Screen.Player.route) {
            PlayerScreen(navController = navController)
        }
        composable(route = Screen.Folders.route) {
            FoldersScreen(navController = navController)
        }
        composable(route = Screen.Playlists.route) {
            PlaylistsScreen(navController = navController)
        }
        composable(route = Screen.Settings.route) {
            com.gravitymusic.presentation.screens.cloud.CloudSyncScreen(navController = navController)
        }
    }
}
