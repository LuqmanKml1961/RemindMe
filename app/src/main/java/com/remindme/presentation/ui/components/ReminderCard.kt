package com.remindme.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Check
import androidx.compose.material3.icons.filled.Edit
import androidx.compose.material3.icons.filled.Delete
import androidx.compose.material3.icons.filled.MedicalServices
import androidx.compose.material3.icons.filled.Payments
import androidx.compose.material3.icons.filled.Notifications
import androidx.compose.material3.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import java.time.format.DateTimeFormatter

@Composable
fun ReminderCard(
    reminder: Reminder,
    onComplete: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onShare: (Reminder) -> Unit
) {
    val typeIcon: ImageVector = when (reminder.type) {
        ReminderType.MEDICAL -> Icons.Filled.MedicalServices
        ReminderType.MONTHLY -> Icons.Filled.Payments
        ReminderType.GENERAL -> Icons.Filled.Notifications
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = typeIcon,
                contentDescription = reminder.type.name,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                if (reminder.description.isNotBlank()) {
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                reminder.dueDate?.let { date ->
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (reminder.type == ReminderType.MEDICAL && reminder.medicineName != null) {
                    Text(
                        text = "${reminder.medicineName} ${reminder.dosage ?: ""}".trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp
                    )
                }

                if (reminder.type == ReminderType.MONTHLY && reminder.amount != null) {
                    Text(
                        text = "Amount: RM${reminder.amount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(onClick = { onComplete(reminder.id) }) {
                Icon(Icons.Filled.Check, contentDescription = "Complete")
            }
            IconButton(onClick = { onEdit(reminder.id) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { onShare(reminder) }) {
                Icon(Icons.Filled.Share, contentDescription = "Share")
            }
            IconButton(onClick = { onDelete(reminder.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}
