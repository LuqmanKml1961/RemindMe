package com.remindme.data.local

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.remindme.domain.model.Reminder
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val appContext = context.applicationContext

    fun schedule(reminder: Reminder) {
        val dueDate = reminder.dueDate ?: return
        val triggerAtMillis = dueDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerAtMillis <= System.currentTimeMillis()) return

        val pendingIntent = createPendingIntent(reminder)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancel(reminder: Reminder) {
        val pendingIntent = createPendingIntent(reminder)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createPendingIntent(reminder: Reminder): PendingIntent {
        val intent = Intent(appContext, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(AlarmReceiver.EXTRA_REMINDER_TITLE, reminder.title)
            putExtra(AlarmReceiver.EXTRA_RECURRENCE_DAYS, reminder.recurrenceDays ?: 0)
            putExtra(
                AlarmReceiver.EXTRA_DUE_MILLIS,
                reminder.dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L
            )
        }
        return PendingIntent.getBroadcast(
            appContext,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}