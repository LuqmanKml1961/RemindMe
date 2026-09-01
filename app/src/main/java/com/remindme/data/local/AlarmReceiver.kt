package com.remindme.data.local

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.remindme.R
import com.remindme.RemindMeApp
import com.remindme.domain.model.RecurrenceRule
import com.remindme.domain.model.RecurrenceUnit
import com.remindme.domain.model.Reminder
import com.remindme.domain.repository.ReminderRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: ReminderRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        val recurrenceDays = intent.getIntExtra(EXTRA_RECURRENCE_DAYS, 0)
        val dueMillis = intent.getLongExtra(EXTRA_DUE_MILLIS, 0L)
        val title = intent.getStringExtra(EXTRA_REMINDER_TITLE) ?: "Reminder"
        val description = intent.getStringExtra(EXTRA_REMINDER_DESCRIPTION)?.takeIf { it.isNotBlank() }
        val recurrence = RecurrenceRule.fromStorage(intent.getStringExtra(EXTRA_RECURRENCE_RULE))

        showNotification(context, reminderId, title, description, dueMillis)

        if (recurrence != null && dueMillis > 0) {
            val result = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    rescheduleRecurring(reminderId, recurrence, dueMillis)
                } finally {
                    result.finish()
                }
            }
        }
    }

    private suspend fun rescheduleRecurring(
        reminderId: Long,
        recurrence: RecurrenceRule,
        lastDueMillis: Long
    ) {
        val reminder = repository.getReminderById(reminderId).first()
            ?: return

        val nextDue = computeNextDue(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(lastDueMillis), ZoneId.systemDefault()),
            recurrence
        )

        val updated = reminder.copy(
            dueDate = nextDue,
            isCompleted = false,
            isArchived = false
        )
        repository.updateReminder(updated)
        alarmScheduler.schedule(updated)
    }

    private fun computeNextDue(lastDue: LocalDateTime, rule: RecurrenceRule): LocalDateTime {
        return when (rule.unit) {
            RecurrenceUnit.DAILY -> lastDue.plusDays(1)
            RecurrenceUnit.WEEKLY -> lastDue.plusWeeks(1)
            RecurrenceUnit.MONTHLY -> lastDue.plusMonths(1)
            RecurrenceUnit.YEARLY -> lastDue.plusYears(1)
            RecurrenceUnit.EVERY_N_DAYS -> lastDue.plusDays(rule.interval.toLong())
        }
    }

    private fun showNotification(
        context: Context,
        reminderId: Long,
        title: String,
        description: String?,
        dueMillis: Long
    ) {
        val contentIntent = Intent(context, com.remindme.presentation.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dueTime = if (dueMillis > 0) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(dueMillis), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("h:mm a"))
        } else {
            null
        }

        val body = buildString {
            append("Your reminder is due")
            dueTime?.let { append(" at $it") }
        }

        val style = NotificationCompat.BigTextStyle().bigText(
            buildString {
                append("\u201C$title\u201D")
                if (description != null) append("\n\n$description")
                if (dueTime != null) append("\nTime: $dueTime")
            }
        )

        val notification = NotificationCompat.Builder(context, RemindMeApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("\u201C$title\u201D")
            .setContentText(body)
            .setStyle(style)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(reminderId.toInt(), notification)
    }

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_REMINDER_TITLE = "reminder_title"
        const val EXTRA_REMINDER_DESCRIPTION = "reminder_description"
        const val EXTRA_RECURRENCE_DAYS = "recurrence_days"
        const val EXTRA_RECURRENCE_RULE = "recurrence_rule"
        const val EXTRA_DUE_MILLIS = "due_millis"
    }
}