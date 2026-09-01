package com.remindme.data.local

import com.remindme.domain.model.ReminderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class ReminderMapperTest {

    private val now = LocalDateTime.of(2024, 3, 1, 9, 30)

    private fun sampleEntity() = ReminderEntity(
        id = 7L,
        title = "Pay rent",
        description = "Monthly rent",
        type = ReminderType.MONTHLY.name,
        createdAt = 1L,
        dueDate = 2L,
        isCompleted = false,
        isArchived = false,
        autoDelete = true,
        amount = 1200.0,
        recurrenceDays = 30,
        shareId = "xyz",
        sharedBy = "Link"
    )

    @Test
    fun `entity maps to domain preserving fields`() {
        val domain = sampleEntity().toDomain()

        assertEquals(7L, domain.id)
        assertEquals("Pay rent", domain.title)
        assertEquals(ReminderType.MONTHLY, domain.type)
        assertEquals(1200.0, domain.amount!!, 0.0)
        assertEquals(30, domain.recurrenceDays)
        assertEquals("xyz", domain.shareId)
        assertEquals("Link", domain.sharedBy)
        assertEquals(true, domain.autoDelete)
    }

    @Test
    fun `domain maps to entity preserving fields`() {
        val domain = sampleEntity().toDomain()
        val entity = domain.toEntity()

        assertEquals(7L, entity.id)
        assertEquals("Pay rent", entity.title)
        assertEquals(ReminderType.MONTHLY.name, entity.type)
        assertEquals(1200.0, entity.amount!!, 0.0)
        assertEquals(30, entity.recurrenceDays)
        assertEquals("xyz", entity.shareId)
    }

    @Test
    fun `medical fields are null for general reminders`() {
        val domain = Reminder(
            id = 1L,
            title = "Buy milk",
            type = ReminderType.GENERAL,
            createdAt = now
        )

        val entity = domain.toEntity()

        assertNull(entity.medicineName)
        assertNull(entity.dosage)
        assertNull(entity.instructions)
        assertNull(entity.amount)
        assertNull(entity.recurrenceDays)
    }
}