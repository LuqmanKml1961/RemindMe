package com.remindme.data.repository

import com.remindme.data.local.ReminderDao
import com.remindme.data.local.toDomain
import com.remindme.data.local.toEntity
import com.remindme.domain.model.Reminder
import com.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRemindersByType(type: String): Flow<List<Reminder>> {
        return reminderDao.getRemindersByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getReminderById(id: Long): Flow<Reminder?> {
        return reminderDao.getReminderById(id).map { it?.toDomain() }
    }

    override fun getReminderByShareId(shareId: String): Flow<Reminder?> {
        return reminderDao.getReminderByShareId(shareId).map { it?.toDomain() }
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder.toEntity())
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder.toEntity())
    }

    override suspend fun deleteReminder(id: Long) {
        reminderDao.deleteReminder(id)
    }

    override suspend fun completeReminder(id: Long) {
        reminderDao.completeReminder(id)
    }

    override suspend fun archiveReminder(id: Long) {
        reminderDao.archiveReminder(id)
    }

    override suspend fun deleteCompletedReminders() {
        reminderDao.deleteCompletedReminders()
    }

    override suspend fun getCompletedReminders(): List<Reminder> {
        return reminderDao.getCompletedReminders().map { it.toDomain() }
    }

    override suspend fun getAllScheduledReminders(): List<Reminder> {
        return reminderDao.getAllScheduledReminders().map { it.toDomain() }
    }
}
