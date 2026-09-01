package com.remindme.data.local

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.remindme.R
import com.remindme.RemindMeApp
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

        showNotification(context, reminderId, title)

        if (recurrenceDays > 0 && dueMillis > 0) {
            val result = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    rescheduleRecurring(reminderId, recurrenceDays, dueMillis)
                } finally {
                    result.finish()
                }
            }
        }
    }

    private suspend fun rescheduleRecurring(reminderId: Long, recurrenceDays: Int, lastDueMillis: Long) {
        val reminder = repository.getReminderById(reminderId).first()
            ?: return

        val nextDue = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(lastDueMillis),
            ZoneId.systemDefault()
        ).plusDays(recurrenceDays.toLong())

        val updated = reminder.copy(
            dueDate = nextDue,
            isCompleted = false,
            isArchived = false
        )
        repository.updateReminder(updated)
        alarmScheduler.schedule(updated)
    }

    private fun showNotification(context: Context, reminderId: Long, title: String) {
        val contentIntent = Intent(context, com.remindme.presentation.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, RemindMeApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("Your reminder is due")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(reminderId.toInt(), notification)
    }

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_REMINDER_TITLE = "reminder_title"
        const val EXTRA_RECURRENCE_DAYS = "recurrence_days"
        const val EXTRA_DUE_MILLIS = "due_millis"
    }
}