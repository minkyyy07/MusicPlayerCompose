package com.example.musicplayer

/**
 * Sealed class representing the different screens in the app
 */
sealed class Screen(val route: String) {
    object Player : Screen("player")
    object Search : Screen("search")
    object Library : Screen("library")
    object Settings : Screen("settings")
    
    /**
     * Returns a user-friendly name for the screen
     */
    val name: String
        get() = when (this) {
            is Player -> "Player"
            is Search -> "Search"
            is Library -> "Library"
            is Settings -> "Settings"
        }
    
    companion object {
        /**
         * Get a screen by its route name
         */
        fun fromRoute(route: String?): Screen = when (route?.substringBefore("/")) {
            Player.route -> Player
            Search.route -> Search
            Library.route -> Library
            Settings.route -> Settings
            null -> Library
            else -> throw IllegalArgumentException("Route $route is not recognized.")
        }
    }
}
