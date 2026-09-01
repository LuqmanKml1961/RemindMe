package com.remindme.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import com.remindme.domain.model.ReminderType
import com.remindme.presentation.ui.components.BrutButton
import com.remindme.presentation.ui.components.BrutChip
import com.remindme.presentation.ui.components.BrutOutlinedButton
import com.remindme.presentation.ui.components.SectionHeader
import com.remindme.presentation.ui.components.loudDateTime
import com.remindme.presentation.viewmodel.CreateReminderViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReminderScreen(
    reminderId: Long?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    presetMinutes: Int? = null,
    viewModel: CreateReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showWhenDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        presetMinutes?.let(viewModel::applyQuickPreset)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditing) "EDIT REMINDER" else "NEW REMINDER",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface, thickness = 2.dp)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("TITLE *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("DESCRIPTION") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(18.dp))

            TypeSelector(selected = uiState.type, onSelect = viewModel::updateType)

            Spacer(modifier = Modifier.height(12.dp))

            // Medical-specific fields
            if (uiState.type == ReminderType.MEDICAL) {
                OutlinedTextField(
                    value = uiState.medicineName,
                    onValueChange = viewModel::updateMedicineName,
                    label = { Text("MEDICINE NAME") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.dosage,
                    onValueChange = viewModel::updateDosage,
                    label = { Text("DOSAGE (E.G. 1 PILL DAILY)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.instructions,
                    onValueChange = viewModel::updateInstructions,
                    label = { Text("SPECIAL INSTRUCTIONS") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Monthly-specific fields
            if (uiState.type == ReminderType.MONTHLY) {
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = viewModel::updateAmount,
                    label = { Text("AMOUNT (RM)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.recurrenceDays,
                    onValueChange = viewModel::updateRecurrenceDays,
                    label = { Text("RECUR EVERY (DAYS)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            WhenSection(
                dueDate = uiState.dueDate,
                onQuick = viewModel::applyQuickPreset,
                onShowCustom = { showWhenDialog = true },
                onClear = { viewModel.updateDueDate(null) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.autoDelete,
                    onCheckedChange = viewModel::updateAutoDelete
                )
                Text(
                    text = "AUTO-DELETE WHEN DONE",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!uiState.isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.alsoAddTodo,
                        onCheckedChange = viewModel::updateAlsoAddTodo
                    )
                    Text(
                        text = "ALSO ADD TO TODO LIST",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BrutButton(
                text = if (uiState.isEditing) "Update Reminder" else "Create Reminder",
                enabled = uiState.isValid,
                onClick = viewModel::saveReminder,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showWhenDialog) {
        WhenDialog(
            initial = uiState.dueDate,
            onDismiss = { showWhenDialog = false },
            onApply = { date, hour, minute ->
                viewModel.applyDateTime(date, hour, minute)
                showWhenDialog = false
            }
        )
    }
}

@Composable
private fun TypeSelector(
    selected: ReminderType,
    onSelect: (ReminderType) -> Unit
) {
    SectionHeader(title = "Reminder type")
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReminderType.values().forEach { type ->
            BrutChip(
                text = when (type) {
                    ReminderType.GENERAL -> "GENERAL"
                    ReminderType.MEDICAL -> "MEDICAL"
                    ReminderType.MONTHLY -> "MONTHLY"
                },
                selected = selected == type,
                onClick = { onSelect(type) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WhenSection(
    dueDate: LocalDateTime?,
    onQuick: (Int) -> Unit,
    onShowCustom: () -> Unit,
    onClear: () -> Unit
) {
    SectionHeader(title = "When")
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BrutChip("5 MIN", Modifier.weight(1f)) { onQuick(5) }
        BrutChip("15 MIN", Modifier.weight(1f)) { onQuick(15) }
        BrutChip("30 MIN", Modifier.weight(1f)) { onQuick(30) }
        BrutChip("1 HR", Modifier.weight(1f)) { onQuick(60) }
    }

    Spacer(modifier = Modifier.height(8.dp))

    BrutOutlinedButton(
        text = "Custom date & time",
        onClick = onShowCustom,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (dueDate != null) loudDateTime(dueDate) else "NO TIME SET",
                style = MaterialTheme.typography.titleLarge,
                color = if (dueDate != null) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (dueDate != null) {
            TextButton(onClick = onClear) {
                Text("CLEAR", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WhenDialog(
    initial: LocalDateTime?,
    onDismiss: () -> Unit,
    onApply: (LocalDate?, Int, Int) -> Unit
) {
    val base = initial ?: LocalDateTime.now()
    var showTime by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = base.hour,
        initialMinute = base.minute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "SET REMINDER TIME",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BrutChip("DATE", Modifier.weight(1f), selected = !showTime) { showTime = false }
                    BrutChip("TIME", Modifier.weight(1f), selected = showTime) { showTime = true }
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (showTime) {
                    TimePicker(state = timePickerState)
                } else {
                    DatePicker(state = datePickerState)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val date = datePickerState.selectedDateMillis?.let { millis ->
                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                onApply(date, timePickerState.hour, timePickerState.minute)
            }) {
                Text("APPLY", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}