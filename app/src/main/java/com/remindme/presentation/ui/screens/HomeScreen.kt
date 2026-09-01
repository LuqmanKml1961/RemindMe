package com.remindme.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.remindme.domain.model.Reminder
import com.remindme.presentation.ui.components.ReminderCard
import com.remindme.presentation.ui.components.ShareDialog
import com.remindme.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var sharingReminder by remember { mutableStateOf<Reminder?>(null) }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.reminders.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No reminders yet.\nTap + to create one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.reminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onComplete = viewModel::completeReminder,
                        onEdit = onEditClick,
                        onDelete = viewModel::deleteReminder,
                        onShare = { sharingReminder = it }
                    )
                }
            }
        }
    }

    sharingReminder?.let { reminder ->
        ShareDialog(
            reminder = reminder,
            shareText = viewModel.generateShareText(reminder),
            onDismiss = { sharingReminder = null }
        )
    }
}