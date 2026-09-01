package com.remindme.presentation.navigation

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home")
    data object TodoScreen : Screen("todo")
    data object SettingsScreen : Screen("settings")

    data object CreateReminderScreen : Screen("create_reminder?presetMin={presetMin}") {
        fun createRoute(presetMin: Int? = null): String =
            "create_reminder?presetMin=${presetMin ?: -1}"
    }

    data object EditReminderScreen : Screen("edit_reminder/{reminderId}") {
        fun createRoute(reminderId: Long) = "edit_reminder/$reminderId"
    }
}