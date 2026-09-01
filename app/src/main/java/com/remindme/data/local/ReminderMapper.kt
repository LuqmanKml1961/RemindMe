package com.remindme.data.local

import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun ReminderEntity.toDomain(): Reminder {
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
        medicineName = medicineName,
        dosage = dosage,
        instructions = instructions,
        amount = amount,
        recurrenceDays = recurrenceDays,
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
        medicineName = medicineName,
        dosage = dosage,
        instructions = instructions,
        amount = amount,
        recurrenceDays = recurrenceDays,
        shareId = shareId,
        sharedBy = sharedBy
    )
}
