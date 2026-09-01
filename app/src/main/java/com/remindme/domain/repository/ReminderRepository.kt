package com.remindme.domain.repository

import com.remindme.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun getRemindersByType(type: String): Flow<List<Reminder>>
    fun getReminderById(id: Long): Flow<Reminder?>
    fun getReminderByShareId(shareId: String): Flow<Reminder?>
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(id: Long)
    suspend fun completeReminder(id: Long)
    suspend fun archiveReminder(id: Long)
    suspend fun deleteCompletedReminders()
    suspend fun getCompletedReminders(): List<Reminder>
    suspend fun getAllScheduledReminders(): List<Reminder>
}
