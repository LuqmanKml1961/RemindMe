package com.remindme.domain.model

data class Schedule(
    val reminderId: Long,
    val triggerTimeMillis: Long,
    val isRecurring: Boolean = false,
    val intervalDays: Int = 0
)
