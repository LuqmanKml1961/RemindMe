package com.remindme.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.remindme.presentation.navigation.MainScaffold
import com.remindme.presentation.navigation.NavGraph
import com.remindme.presentation.navigation.Screen
import com.remindme.presentation.ui.theme.RemindMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemindMeTheme {
                RemindMeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindMeApp() {
    val navController = rememberNavController()

    MainScaffold(
        navController = navController,
        onAddClick = {
            navController.navigate(Screen.CreateReminderScreen.route)
        }
    ) { padding ->
        NavGraph(
            navController = navController,
            onAddClick = {
                navController.navigate(Screen.CreateReminderScreen.route)
            }
        )
    }
}
