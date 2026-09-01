package com.remindme.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class ReminderWithMeds(
    @Embedded val reminder: ReminderEntity,
    @Relation(parentColumn = "id", entityColumn = "reminder_id")
    val medications: List<MedicationEntity>
)