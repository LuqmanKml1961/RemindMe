package com.remindme.domain.model

import java.time.LocalDateTime

data class Reminder(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val type: ReminderType,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val dueDate: LocalDateTime? = null,
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val autoDelete: Boolean = false,
    // Medical specific
    val medicineName: String? = null,
    val dosage: String? = null,
    val instructions: String? = null,
    // Monthly specific
    val amount: Double? = null,
    val recurrenceDays: Int? = null,
    // Sharing
    val shareId: String? = null,
    val sharedBy: String? = null
)
