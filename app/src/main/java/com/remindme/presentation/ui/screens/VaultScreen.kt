package com.remindme.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remindme.domain.model.VaultCategory
import com.remindme.domain.model.VaultReference
import com.remindme.presentation.ui.components.BrutChip
import com.remindme.presentation.ui.components.BrutPass
import com.remindme.presentation.ui.components.SectionHeader
import com.remindme.presentation.viewmodel.VaultViewModel

@Composable
fun VaultScreen(
    modifier: Modifier = Modifier,
    viewModel: VaultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditor by remember { mutableStateOf(false) }
    var editingRef by remember { mutableStateOf<VaultReference?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "VAULT",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "ZERO-ALERT REFERENCE DATA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
                    .clickable {
                        editingRef = null
                        showEditor = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New vault entry",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.search,
            onValueChange = viewModel::setSearch,
            label = { Text("SEARCH VAULT") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BrutChip(
                "All",
                Modifier.weight(1f),
                selected = uiState.filter == null
            ) { viewModel.setFilter(null) }
            BrutChip(
                catShortLabel(VaultCategory.PEOPLE),
                Modifier.weight(1f),
                selected = uiState.filter == VaultCategory.PEOPLE
            ) { viewModel.setFilter(VaultCategory.PEOPLE) }
            BrutChip(
                catShortLabel(VaultCategory.HOME_VEHICLE),
                Modifier.weight(1f),
                selected = uiState.filter == VaultCategory.HOME_VEHICLE
            ) { viewModel.setFilter(VaultCategory.HOME_VEHICLE) }
            BrutChip(
                catShortLabel(VaultCategory.PROPERTY),
                Modifier.weight(1f),
                selected = uiState.filter == VaultCategory.PROPERTY
            ) { viewModel.setFilter(VaultCategory.PROPERTY) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(
            title = "Your vault",
            trailing = "${uiState.references.size} ENTRIES"
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.references.isEmpty()) {
            BrutPass(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nothing here yet.\n\nTap + to save a detail you need on hand — " +
                        "sizes, tyre pressure, filter dimensions. Quiet storage, zero alerts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp)
            ) {
                items(uiState.references, key = { it.id }) { reference ->
                    VaultCard(
                        reference = reference,
                        onClick = {
                            editingRef = reference
                            showEditor = true
                        }
                    )
                }
            }
        }
    }

    if (showEditor) {
        VaultEditorDialog(
            reference = editingRef,
            onDismiss = { showEditor = false },
            onSave = { category, title, note ->
                viewModel.saveReference(editingRef?.id ?: 0L, category, title, note)
                showEditor = false
            },
            onDelete = editingRef?.let { ref ->
                {
                    viewModel.deleteReference(ref.id)
                    showEditor = false
                }
            }
        )
    }
}

@Composable
private fun VaultCard(
    reference: VaultReference,
    onClick: () -> Unit
) {
    BrutPass(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reference.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = reference.category.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (reference.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reference.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun VaultEditorDialog(
    reference: VaultReference?,
    onDismiss: () -> Unit,
    onSave: (VaultCategory, String, String) -> Unit,
    onDelete: (() -> Unit)?
) {
    var category by remember { mutableStateOf(reference?.category ?: VaultCategory.PEOPLE) }
    var title by rememberSaveable { mutableStateOf(reference?.title ?: "") }
    var note by rememberSaveable { mutableStateOf(reference?.note ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (reference == null) "NEW VAULT ENTRY" else "EDIT ENTRY",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VaultCategory.entries.forEach { cat ->
                        BrutChip(
                            catShortLabel(cat),
                            Modifier.weight(1f),
                            selected = category == cat
                        ) { category = cat }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("TITLE") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("DETAILS (OPTIONAL)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (onDelete != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDelete) {
                            Text(
                                text = "DELETE",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = { onSave(category, title, note) }
            ) {
                Text("SAVE", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

private fun catShortLabel(category: VaultCategory): String = when (category) {
    VaultCategory.PEOPLE -> "People"
    VaultCategory.HOME_VEHICLE -> "Vehicle"
    VaultCategory.PROPERTY -> "Property"
}