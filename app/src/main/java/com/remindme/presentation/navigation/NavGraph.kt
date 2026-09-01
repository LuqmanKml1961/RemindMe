package com.remindme.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.remindme.presentation.ui.screens.CreateReminderScreen
import com.remindme.presentation.ui.screens.HomeScreen
import com.remindme.presentation.ui.screens.SettingsScreen
import com.remindme.presentation.ui.screens.TodoScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    onAddClick: () -> Unit = {
        navController.navigate(Screen.CreateReminderScreen.route)
    }
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(
                onAddClick = onAddClick,
                onEditClick = { reminderId ->
                    navController.navigate(Screen.EditReminderScreen.createRoute(reminderId))
                }
            )
        }

        composable(Screen.CreateReminderScreen.route) {
            CreateReminderScreen(
                reminderId = null,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditReminderScreen.route,
            arguments = listOf(navArgument("reminderId") {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getLong("reminderId")
            CreateReminderScreen(
                reminderId = reminderId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TodoScreen.route) {
            TodoScreen()
        }

        composable(Screen.SettingsScreen.route) {
            SettingsScreen()
        }
    }
}
