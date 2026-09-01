package com.remindme.domain.usecase

import com.remindme.FakeReminderRepository
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateReminderUseCaseTest {

    private val repository = FakeReminderRepository()
    private val useCase = CreateReminderUseCase(repository)

    @Test
    fun `new reminder gets shareId and returns new id`() = runBlocking {
        val reminder = Reminder(
            title = "Buy milk",
            type = ReminderType.GENERAL,
            shareId = null
        )

        val id = useCase(reminder)

        assertTrue(id > 0)
        val saved = repository.reminders.value.first()
        assertEquals("Buy milk", saved.title)
        assertEquals(id, saved.id)
        assertNotNull(saved.shareId)
    }

    @Test
    fun `creating multiple reminders generates unique share ids`() = runBlocking {
        val a = useCase(Reminder(title = "A", type = ReminderType.GENERAL))
        val b = useCase(Reminder(title = "B", type = ReminderType.GENERAL))

        assertEquals(2, repository.reminders.value.size)
        assertTrue(repository.reminders.value[0].shareId != repository.reminders.value[1].shareId)
        assertTrue(a != b)
    }
}