package com.remindme.domain.model

enum class RecurrenceUnit {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    EVERY_N_DAYS
}

data class RecurrenceRule(
    val unit: RecurrenceUnit,
    val interval: Int = 1
) {
    val label: String
        get() = when (unit) {
            RecurrenceUnit.DAILY -> "Daily"
            RecurrenceUnit.WEEKLY -> "Weekly"
            RecurrenceUnit.MONTHLY -> "Monthly"
            RecurrenceUnit.YEARLY -> "Yearly"
            RecurrenceUnit.EVERY_N_DAYS -> "Every $interval days"
        }

    fun toStorage(): String = when (unit) {
        RecurrenceUnit.DAILY,
        RecurrenceUnit.WEEKLY,
        RecurrenceUnit.MONTHLY,
        RecurrenceUnit.YEARLY -> unit.name
        RecurrenceUnit.EVERY_N_DAYS -> "EVERY_$interval"
    }

    companion object {
        fun fromStorage(value: String?): RecurrenceRule? {
            if (value.isNullOrBlank()) return null
            return if (value.startsWith("EVERY_")) {
                RecurrenceRule(
                    unit = RecurrenceUnit.EVERY_N_DAYS,
                    interval = value.removePrefix("EVERY_").toIntOrNull()?.coerceAtLeast(1) ?: 1
                )
            } else {
                RecurrenceUnit.entries.firstOrNull { it.name == value }?.let { RecurrenceRule(it) }
            }
        }
    }
}