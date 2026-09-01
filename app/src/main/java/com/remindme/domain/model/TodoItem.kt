package com.remindme.domain.model

import java.time.LocalDateTime

data class TodoItem(
    val id: Long = 0,
    val text: String,
    val isCompleted: Boolean = false,
    val priority: Int = 0,
    val reminderId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
