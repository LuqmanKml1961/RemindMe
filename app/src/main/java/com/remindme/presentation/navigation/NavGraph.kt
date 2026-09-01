package com.remindme.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {
        navController.navigate(Screen.CreateReminderScreen.createRoute())
    }
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
        modifier = modifier
    ) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(
                onAddClick = onAddClick,
                onQuickCreate = { minutes ->
                    navController.navigate(Screen.CreateReminderScreen.createRoute(minutes))
                },
                onEditClick = { reminderId ->
                    navController.navigate(Screen.EditReminderScreen.createRoute(reminderId))
                }
            )
        }

        composable(
            route = Screen.CreateReminderScreen.route,
            arguments = listOf(
                navArgument("presetMin") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val presetMin = backStackEntry.arguments?.getInt("presetMin")
            CreateReminderScreen(
                reminderId = null,
                presetMinutes = presetMin.takeIf { it in listOf(5, 15, 30, 60) },
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