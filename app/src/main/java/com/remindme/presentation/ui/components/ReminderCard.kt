package com.remindme.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import com.remindme.presentation.ui.theme.AccentAmber
import java.time.LocalDateTime

@Composable
fun ReminderCard(
    reminder: Reminder,
    onComplete: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onShare: (Reminder) -> Unit
) {
    val isDone = reminder.isCompleted
    val accent = typeAccent(reminder.type)
    val isOverdue = !isDone && reminder.dueDate != null && reminder.dueDate!!.isBefore(LocalDateTime.now())
    val container = if (isDone) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface

    BrutCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shadow = !isDone,
        containerColor = container
    ) {
        Column {
            // Top accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(if (isOverdue) AccentAmber else accent)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TypeTag(type = reminder.type)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isOverdue) {
                        Text(
                            text = "OVERDUE",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    reminder.dueDate?.let { due ->
                        Text(
                            text = dueLabel(due),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOverdue) AccentAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleLarge,
                    textDecoration = if (isDone) TextDecoration.LineThrough else null,
                    color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else onSurface
                )

                if (reminder.description.isNotBlank()) {
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (reminder.type == ReminderType.MEDICAL && reminder.medicineName != null) {
                    Text(
                        text = "${reminder.medicineName}${reminder.dosage?.let { " · $it" } ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    reminder.instructions?.let {
                        if (it.isNotBlank()) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (reminder.type == ReminderType.MONTHLY && reminder.amount != null) {
                    Text(
                        text = "RM ${reminder.amount} per cycle",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(thickness = 1.dp, color = onSurface)

            Row(modifier = Modifier.fillMaxWidth()) {
                ActionCell("DONE", Modifier.weight(1f)) { onComplete(reminder.id) }
                ActionCell("EDIT", Modifier.weight(1f)) { onEdit(reminder.id) }
                ActionCell("SHARE", Modifier.weight(1f)) { onShare(reminder) }
                ActionCell("DELETE", Modifier.weight(1f), danger = true) { onDelete(reminder.id) }
            }
        }
    }
}

@Composable
private fun ActionCell(
    label: String,
    modifier: Modifier = Modifier,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val tint = when {
        danger -> MaterialTheme.colorScheme.error
        label == "DONE" -> MaterialTheme.colorScheme.primary
        else -> onSurface
    }
    Box(
        modifier = modifier
            .border(start = BorderStroke(1.dp, onSurface))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = tint
        )
    }
}