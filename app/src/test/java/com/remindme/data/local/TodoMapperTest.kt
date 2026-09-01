package com.remindme.data.local

import com.remindme.domain.model.TodoItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TodoMapperTest {

    @Test
    fun `todo entity maps to domain`() {
        val entity = TodoEntity(
            id = 3L,
            text = "Buy groceries",
            isCompleted = true,
            priority = 2,
            reminderId = 9L,
            createdAt = 123L
        )

        val domain = entity.toDomain()

        assertEquals(3L, domain.id)
        assertEquals("Buy groceries", domain.text)
        assertEquals(true, domain.isCompleted)
        assertEquals(2, domain.priority)
        assertEquals(9L, domain.reminderId)
    }

    @Test
    fun `todo domain maps to entity`() {
        val domain = TodoItem(
            id = 5L,
            text = "Call mom",
            isCompleted = false,
            priority = 1,
            reminderId = null
        )

        val entity = domain.toEntity()

        assertEquals(5L, entity.id)
        assertEquals("Call mom", entity.text)
        assertEquals(false, entity.isCompleted)
        assertNull(entity.reminderId)
    }
}