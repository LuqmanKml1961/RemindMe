package com.remindme.data.local

import com.remindme.domain.model.TodoItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun TodoEntity.toDomain(): TodoItem {
    return TodoItem(
        id = id,
        text = text,
        isCompleted = isCompleted,
        priority = priority,
        reminderId = reminderId,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
    )
}

fun TodoItem.toEntity(): TodoEntity {
    return TodoEntity(
        id = id,
        text = text,
        isCompleted = isCompleted,
        priority = priority,
        reminderId = reminderId,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
