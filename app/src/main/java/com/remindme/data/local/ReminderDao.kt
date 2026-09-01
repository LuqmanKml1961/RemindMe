package com.remindme.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE is_archived = 0 ORDER BY created_at DESC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE type = :type AND is_archived = 0 ORDER BY created_at DESC")
    fun getRemindersByType(type: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Long): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE share_id = :shareId")
    fun getReminderByShareId(shareId: String): Flow<ReminderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)

    @Query("UPDATE reminders SET is_completed = 1 WHERE id = :id")
    suspend fun completeReminder(id: Long)

    @Query("UPDATE reminders SET is_archived = 1 WHERE id = :id")
    suspend fun archiveReminder(id: Long)

    @Query("DELETE FROM reminders WHERE is_completed = 1 AND auto_delete = 1")
    suspend fun deleteCompletedReminders()

    @Query("SELECT * FROM reminders WHERE is_completed = 1")
    suspend fun getCompletedReminders(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE due_date IS NOT NULL AND is_completed = 0 AND is_archived = 0")
    suspend fun getAllScheduledReminders(): List<ReminderEntity>
}
