package com.remindme.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "todo_items",
    foreignKeys = [
        ForeignKey(
            entity = ReminderEntity::class,
            parentColumns = ["id"],
            childColumns = ["reminder_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["reminder_id"])]
)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    val priority: Int = 0,
    @ColumnInfo(name = "reminder_id")
    val reminderId: Long? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
