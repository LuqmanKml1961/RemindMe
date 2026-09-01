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
    // Medical specific — one entry can hold multiple medications
    val medications: List<Medication> = emptyList(),
    // Monthly (bill) specific
    val amount: Double? = null,
    // Repeat — applies to any type
    val recurrence: RecurrenceRule? = null,
    // Sharing
    val shareId: String? = null,
    val sharedBy: String? = null
)