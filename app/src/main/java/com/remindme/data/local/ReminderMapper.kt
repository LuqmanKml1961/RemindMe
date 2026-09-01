package com.remindme.data.local

import com.remindme.domain.model.Medication
import com.remindme.domain.model.RecurrenceRule
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import com.remindme.domain.model.VaultCategory
import com.remindme.domain.model.VaultReference
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun ReminderWithMeds.toDomain(): Reminder {
    return reminder.toDomain(medications.map { it.toDomain() })
}

fun ReminderEntity.toDomain(medications: List<Medication> = emptyList()): Reminder {
    return Reminder(
        id = id,
        title = title,
        description = description,
        type = ReminderType.valueOf(type),
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
        dueDate = dueDate?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) },
        isCompleted = isCompleted,
        isArchived = isArchived,
        autoDelete = autoDelete,
        medications = medications,
        amount = amount,
        recurrence = RecurrenceRule.fromStorage(recurrence),
        shareId = shareId,
        sharedBy = sharedBy
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        type = type.name,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        isCompleted = isCompleted,
        isArchived = isArchived,
        autoDelete = autoDelete,
        amount = amount,
        recurrence = recurrence?.toStorage(),
        shareId = shareId,
        sharedBy = sharedBy
    )
}

fun MedicationEntity.toDomain(): Medication {
    return Medication(id = id, name = name, dosage = dosage, instructions = instructions)
}

fun Medication.toEntity(reminderId: Long): MedicationEntity {
    return MedicationEntity(
        id = id,
        reminderId = reminderId,
        name = name,
        dosage = dosage,
        instructions = instructions
    )
}

fun VaultReferenceEntity.toDomain(): VaultReference {
    return VaultReference(
        id = id,
        category = VaultCategory.valueOf(category),
        title = title,
        note = note,
        createdAt = createdAt
    )
}

fun VaultReference.toEntity(): VaultReferenceEntity {
    return VaultReferenceEntity(
        id = id,
        category = category.name,
        title = title,
        note = note,
        createdAt = if (createdAt > 0L) createdAt else System.currentTimeMillis()
    )
}