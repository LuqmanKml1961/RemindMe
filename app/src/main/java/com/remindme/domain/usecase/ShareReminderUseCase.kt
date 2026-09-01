package com.remindme.domain.usecase

import com.remindme.domain.model.Reminder
import com.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ShareReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    fun generateShareLink(reminder: Reminder): String {
        val shareId = reminder.shareId ?: return ""
        return "remindme://reminder/$shareId"
    }

    fun generateShareText(reminder: Reminder): String {
        val link = generateShareLink(reminder)
        return buildString {
            append("RemindMe: ${reminder.title}")
            when (reminder.type) {
                com.remindme.domain.model.ReminderType.MEDICAL -> {
                    reminder.medicineName?.let { append("\nMedicine: $it") }
                    reminder.dosage?.let { append("\nDosage: $it") }
                    reminder.instructions?.let { append("\nInstructions: $it") }
                }
                com.remindme.domain.model.ReminderType.MONTHLY -> {
                    reminder.amount?.let { append("\nAmount: RM$it") }
                    reminder.recurrenceDays?.let { append("\nEvery $it days") }
                }
                else -> {}
            }
            reminder.dueDate?.let {
                append("\nDue: ${it.toString().substring(0, 16)}")
            }
            if (link.isNotBlank()) {
                append("\nTap to import: $link")
            }
        }
    }

    suspend fun importReminder(shareId: String, sharedBy: String): Reminder? {
        val reminder = repository.getReminderByShareId(shareId).firstOrNull()
            ?: return null

        val importedReminder = reminder.copy(
            id = 0,
            shareId = null,
            sharedBy = sharedBy,
            isCompleted = false,
            isArchived = false
        )

        val newId = repository.insertReminder(importedReminder)
        return importedReminder.copy(id = newId)
    }
}