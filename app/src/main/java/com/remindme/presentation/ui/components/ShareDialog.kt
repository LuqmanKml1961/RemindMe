package com.remindme.presentation.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.remindme.domain.model.Reminder

@Composable
fun ShareDialog(
    reminder: Reminder,
    shareText: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "SHARE REMINDER",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "Share \"${reminder.title}\" with others. They can import it into their RemindMe app via a link.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                ShareActionCell("COPY LINK", Modifier.weight(1f), fill = false) {
                    clipboardManager.setText(AnnotatedString(shareText))
                    onDismiss()
                }
                Spacer(modifier = Modifier.height(0.dp))
                ShareActionCell("SHARE", Modifier.weight(1f), fill = true) {
                    shareViaApps(context, shareText)
                }
            }
        },
        dismissButton = {}
    )
}

@Composable
private fun ShareActionCell(
    label: String,
    modifier: Modifier = Modifier,
    fill: Boolean,
    onClick: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val bg = if (fill) onSurface else MaterialTheme.colorScheme.surface
    val contentTint = if (fill) MaterialTheme.colorScheme.background else onSurface
    val icon = if (fill) Icons.Filled.Share else Icons.Filled.ContentCopy

    Box(
        modifier = modifier
            .background(bg)
            .border(1.dp, onSurface)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentTint,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = contentTint
            )
        }
    }
}

fun shareViaApps(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(sendIntent, "Share reminder via")
    ContextCompat.startActivity(context, chooser, null)
}