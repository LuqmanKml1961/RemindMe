package com.remindme.presentation.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ContentCopy
import androidx.compose.material3.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
        title = { Text("Share Reminder") },
        text = {
            Text("Share \"${reminder.title}\" with others. They can import it into their RemindMe app.")
        },
        confirmButton = {
            TextButton(onClick = {
                shareViaApps(context, shareText)
            }) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Text("  Share")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                clipboardManager.setText(AnnotatedString(shareText))
                onDismiss()
            }) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null)
                Text("  Copy")
            }
        }
    )
}

fun shareViaApps(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(sendIntent, "Share reminder via")
    ContextCompat.startActivity(context, chooser, null)
}