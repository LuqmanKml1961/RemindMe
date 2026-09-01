package com.remindme.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remindme.presentation.ui.components.BrutCard
import com.remindme.presentation.ui.components.BrutOutlinedButton
import com.remindme.presentation.ui.components.SectionHeader
import com.remindme.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(title = "Reminders")
        Spacer(modifier = Modifier.height(10.dp))

        BrutCard(modifier = Modifier.fillMaxWidth(), shadow = false) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DEFAULT AUTO-DELETE",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Automatically delete completed reminders by default",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.autoDeleteDefault,
                    onCheckedChange = viewModel::setAutoDeleteDefault
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        SectionHeader(title = "Guide")
        Spacer(modifier = Modifier.height(10.dp))

        BrutCard(modifier = Modifier.fillMaxWidth(), shadow = false) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "WELCOME GUIDE",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Show the 3-step how-to guide again on next launch.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                BrutOutlinedButton(
                    text = "Replay guide",
                    onClick = viewModel::replayGuide,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        SectionHeader(title = "About")
        Spacer(modifier = Modifier.height(10.dp))

        BrutCard(modifier = Modifier.fillMaxWidth(), shadow = false) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "REMINDME",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "v2.0 · Brutalist Edition\nLow, low battery use. All data stays on your device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}