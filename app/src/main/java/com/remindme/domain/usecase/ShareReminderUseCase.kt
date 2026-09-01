package com.remindme.domain.usecase

import com.remindme.domain.model.Reminder
import com.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ShareReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend fun generateShareLink(reminder: Reminder): String {
        return "remindme://reminder/${reminder.shareId}"
    }

    suspend fun importReminder(shareId: String, sharedBy: String): Boolean {
        val reminder = repository.getReminderByShareId(shareId).firstOrNull()
            ?: return false

        val importedReminder = reminder.copy(
            id = 0,
            shareId = null,
            sharedBy = sharedBy,
            isCompleted = false,
            isArchived = false
        )

        repository.insertReminder(importedReminder)
        return true
    }
}
