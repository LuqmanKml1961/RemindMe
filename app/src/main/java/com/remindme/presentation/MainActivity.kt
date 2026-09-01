package com.remindme.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.remindme.presentation.navigation.MainScaffold
import com.remindme.presentation.navigation.NavGraph
import com.remindme.presentation.navigation.Screen
import com.remindme.presentation.ui.theme.RemindMeTheme
import com.remindme.presentation.viewmodel.ImportResult
import com.remindme.presentation.viewmodel.ImportViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val importShareId = mutableStateOf<String?>(null)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* ignored */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        requestNotificationPermissionIfNeeded()

        setContent {
            RemindMeTheme {
                RemindMeApp(importShareId) {
                    importShareId.value = null
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.let { uri ->
            if (uri.scheme == "remindme" && uri.host == "reminder") {
                uri.lastPathSegment?.let { shareId ->
                    importShareId.value = shareId
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun RemindMeApp(
    importShareId: State<String?> = remember { mutableStateOf(null) },
    onImportCleared: () -> Unit = {}
) {
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

    importShareId.value?.let { shareId ->
        ImportDialog(
            shareId = shareId,
            onDismiss = onImportCleared
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportDialog(
    shareId: String,
    onDismiss: () -> Unit
) {
    val viewModel: ImportViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {
            onDismiss()
            viewModel.clearResult()
        }
    }

    when {
        uiState.processing -> {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                text = { CircularProgressIndicator() }
            )
        }
        uiState.result != null -> {}
        else -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Import Reminder") },
                text = { Text("A reminder was shared with you. Import it into RemindMe?") },
                confirmButton = {
                    TextButton(onClick = { viewModel.importReminder(shareId) }) {
                        Text("Import")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}