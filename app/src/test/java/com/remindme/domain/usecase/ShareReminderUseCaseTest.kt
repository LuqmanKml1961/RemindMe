package com.remindme.domain.usecase

import com.remindme.FakeReminderRepository
import com.remindme.domain.model.Medication
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareReminderUseCaseTest {

    private val repository = FakeReminderRepository()
    private val useCase = ShareReminderUseCase(repository)

    private fun reminderWithShareId() = Reminder(
        id = 1L,
        title = "Grandma medicine",
        type = ReminderType.MEDICAL,
        medications = listOf(
            Medication(name = "Metformin", dosage = "500mg daily")
        ),
        shareId = "abc-123"
    )

    @Test
    fun `generateShareLink builds remindme url with share id`() {
        val link = useCase.generateShareLink(reminderWithShareId())

        assertEquals("remindme://reminder/abc-123", link)
    }

    @Test
    fun `generateShareLink returns empty for reminder without shareId`() {
        val link = useCase.generateShareLink(reminderWithShareId().copy(shareId = null))

        assertTrue(link.isEmpty())
    }

    @Test
    fun `generateShareText includes title medicine details and link`() {
        val text = useCase.generateShareText(reminderWithShareId())

        assertTrue(text.contains("Grandma medicine"))
        assertTrue(text.contains("Metformin"))
        assertTrue(text.contains("500mg daily"))
        assertTrue(text.contains("remindme://reminder/abc-123"))
    }

    @Test
    fun `generateShareText includes recurrence label`() {
        val reminder = reminderWithShareId().copy(
            recurrence = com.remindme.domain.model.RecurrenceRule(
                unit = com.remindme.domain.model.RecurrenceUnit.DAILY,
                interval = 1
            )
        )
        val text = useCase.generateShareText(reminder)

        assertTrue(text.contains("Repeats: Daily"))
    }

    @Test
    fun `importReminder returns null when shareId not found`() = runBlocking {
        repository.reminders.value = listOf(reminderWithShareId())

        val imported = useCase.importReminder("not-exists", "Link")

        assertNull(imported)
        assertEquals(1, repository.reminders.value.size)
    }

    @Test
    fun `importReminder copies reminder with new share and clears shareId`() = runBlocking {
        repository.reminders.value = listOf(reminderWithShareId())

        val imported = useCase.importReminder("abc-123", "Link")

        assertNotNull(imported)
        assertEquals(2, repository.reminders.value.size)
        val copy = repository.reminders.value.last()
        assertNull(copy.shareId)
        assertEquals("Link", copy.sharedBy)
        assertTrue(copy.id != 1L)
        assertEquals("Grandma medicine", copy.title)
    }

    @Test
    fun `importReminder resets completed and archived state`() = runBlocking {
        repository.reminders.value = listOf(
            reminderWithShareId().copy(isCompleted = true, isArchived = true)
        )

        val imported = useCase.importReminder("abc-123", "Link")

        assertFalse(imported!!.isCompleted)
        assertFalse(imported.isArchived)
    }
}