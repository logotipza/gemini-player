package com.gravitymusic.presentation.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object Player : Screen("player_screen")
    object Folders : Screen("folders_screen")
    object Playlists : Screen("playlists_screen")
    object Settings : Screen("settings_screen")
}
