package com.remindme.domain.usecase

import com.remindme.domain.model.Reminder
import com.remindme.domain.repository.ReminderRepository
import java.util.UUID
import javax.inject.Inject

class CreateReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder): Long {
        val reminderWithShareId = reminder.copy(
            shareId = UUID.randomUUID().toString()
        )
        return repository.insertReminder(reminderWithShareId)
    }
}
