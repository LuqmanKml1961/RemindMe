package com.remindme.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remindme.domain.model.Reminder
import com.remindme.presentation.ui.components.*
import com.remindme.presentation.ui.theme.AccentAmber
import com.remindme.presentation.ui.theme.AccentGreen
import com.remindme.presentation.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onQuickCreate: (Int) -> Unit,
    onEditClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var sharingReminder by remember { mutableStateOf<Reminder?>(null) }

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val today = LocalDate.now()
    val active = uiState.reminders.filter { !it.isCompleted && !it.isArchived }
    val dueToday = active.count { it.dueDate?.toLocalDate() == today }
    val doneCount = uiState.reminders.count { it.isCompleted }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "header") {
            HomeHeader(
                activeCount = active.size,
                onAddClick = onAddClick
            )
        }

        item(key = "stats") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(
                    value = active.size.toString(),
                    label = "Active",
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    value = dueToday.toString(),
                    label = "Due today",
                    modifier = Modifier.weight(1f),
                    accent = AccentAmber
                )
                StatBox(
                    value = doneCount.toString(),
                    label = "Done",
                    modifier = Modifier.weight(1f),
                    accent = AccentGreen
                )
            }
        }

        item(key = "quick") {
            QuickRemindRow(onQuickCreate)
        }

        item(key = "reminders-title") {
            SectionHeader(
                title = "Reminders",
                trailing = "${active.size} ACTIVE",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (active.isEmpty()) {
            item(key = "guide") {
                HowToGuide(onAddClick)
            }
        } else {
            items(active, key = { it.id }) { reminder ->
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

    sharingReminder?.let { reminder ->
        ShareDialog(
            reminder = reminder,
            shareText = viewModel.generateShareText(reminder),
            onDismiss = { sharingReminder = null }
        )
    }
}

@Composable
private fun HomeHeader(
    activeCount: Int,
    onAddClick: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val background = MaterialTheme.colorScheme.background
    val todayLabel = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("EEE, MMM d"))
        .uppercase()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "REMINDME",
                style = MaterialTheme.typography.displayMedium,
                maxLines = 1
            )
            Text(
                text = "$todayLabel · $activeCount ACTIVE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .size(54.dp)
                .background(onSurface)
                .clickable(onClick = onAddClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Create reminder",
                tint = background,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun QuickRemindRow(onQuickCreate: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "QUICK REMIND",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BrutChip("5 MIN", Modifier.weight(1f)) { onQuickCreate(5) }
            BrutChip("30 MIN", Modifier.weight(1f)) { onQuickCreate(30) }
            BrutChip("1 HR", Modifier.weight(1f)) { onQuickCreate(60) }
        }
    }
}

@Composable
private fun HowToGuide(onAddClick: () -> Unit) {
    BrutPass(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "HOW TO USE REMINDME",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            GuideStep("1", "Tap + or a quick time above")
            GuideStep("2", "Type what to be reminded of")
            GuideStep("3", "Done — we'll notify you at the right moment")
            Spacer(modifier = Modifier.height(12.dp))
            BrutButton(
                text = "Create first reminder",
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GuideStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(MaterialTheme.colorScheme.onSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.background
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}