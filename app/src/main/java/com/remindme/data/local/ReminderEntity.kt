package com.remindme.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val type: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    @ColumnInfo(name = "auto_delete")
    val autoDelete: Boolean = false,
    // Medical specific
    @ColumnInfo(name = "medicine_name")
    val medicineName: String? = null,
    val dosage: String? = null,
    val instructions: String? = null,
    // Monthly specific
    val amount: Double? = null,
    @ColumnInfo(name = "recurrence_days")
    val recurrenceDays: Int? = null,
    // Sharing
    @ColumnInfo(name = "share_id")
    val shareId: String? = null,
    @ColumnInfo(name = "shared_by")
    val sharedBy: String? = null
)
