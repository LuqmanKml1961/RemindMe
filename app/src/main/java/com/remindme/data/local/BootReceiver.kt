package com.remindme.data.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.remindme.domain.repository.ReminderRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: ReminderRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            return
        }

        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduled = repository.getAllScheduledReminders()
                scheduled.forEach { reminder ->
                    if (reminder.dueDate != null && !reminder.isCompleted && !reminder.isArchived) {
                        alarmScheduler.schedule(reminder)
                    }
                }
            } finally {
                result.finish()
            }
        }
    }
}