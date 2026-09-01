package com.remindme.data.local

import com.remindme.domain.model.Medication
import com.remindme.domain.model.RecurrenceRule
import com.remindme.domain.model.RecurrenceUnit
import com.remindme.domain.model.ReminderType
import com.remindme.domain.model.VaultCategory
import com.remindme.domain.model.VaultReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class ReminderMapperTest {

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
        recurrence = "MONTHLY",
        shareId = "xyz",
        sharedBy = "Link"
    )

    @Test
    fun `entity maps to domain preserving fields`() {
        val domain = sampleEntity().toDomain(emptyList())

        assertEquals(7L, domain.id)
        assertEquals("Pay rent", domain.title)
        assertEquals(ReminderType.MONTHLY, domain.type)
        assertEquals(1200.0, domain.amount!!, 0.0)
        assertEquals(RecurrenceRule(RecurrenceUnit.MONTHLY, 0), domain.recurrence)
        assertEquals("xyz", domain.shareId)
        assertEquals("Link", domain.sharedBy)
        assertEquals(true, domain.autoDelete)
    }

    @Test
    fun `domain maps to entity preserving fields`() {
        val domain = sampleEntity().toDomain(emptyList())
        val entity = domain.toEntity()

        assertEquals(7L, entity.id)
        assertEquals("Pay rent", entity.title)
        assertEquals(ReminderType.MONTHLY.name, entity.type)
        assertEquals(1200.0, entity.amount!!, 0.0)
        assertEquals("MONTHLY", entity.recurrence)
        assertEquals("xyz", entity.shareId)
    }

    @Test
    fun `general type maps without recurrence or medications`() {
        val entity = ReminderEntity(
            id = 1L,
            title = "Buy milk",
            type = ReminderType.GENERAL.name,
            createdAt = 1L,
            dueDate = null,
            isCompleted = false,
            isArchived = false,
            autoDelete = false,
            amount = null,
            recurrence = null,
            shareId = null,
            sharedBy = null
        )

        val domain = entity.toDomain(emptyList())

        assertEquals(1L, domain.id)
        assertEquals("Buy milk", domain.title)
        assertNull(domain.amount)
        assertNull(domain.recurrence)
        assertTrue(domain.medications.isEmpty())
    }

    @Test
    fun `entity with medications maps to domain`() {
        val entity = sampleEntity()
        val medEntities = listOf(
            MedicationEntity(reminderId = 7L, name = "Metformin", dosage = "500mg", instructions = "After food", sortOrder = 0),
            MedicationEntity(reminderId = 7L, name = "Aspirin", dosage = "100mg", instructions = "", sortOrder = 1)
        )

        val domain = entity.toDomain(medEntities)

        assertEquals(2, domain.medications.size)
        assertEquals("Metformin", domain.medications[0].name)
        assertEquals("500mg", domain.medications[0].dosage)
        assertEquals("After food", domain.medications[0].instructions)
        assertEquals("Aspirin", domain.medications[1].name)
    }

    @Test
    fun `entity with no medications returns empty list`() {
        val domain = sampleEntity().toDomain(emptyList())
        assertTrue(domain.medications.isEmpty())
    }

    @Test
    fun `vault reference maps correctly`() {
        val reference = VaultReference(
            id = 1L,
            category = VaultCategory.PEOPLE,
            title = "Dr. Tan",
            note = "Clinic: 03-1234567"
        )
        val entity = reference.toEntity()

        assertEquals(1L, entity.id)
        assertEquals(VaultCategory.PEOPLE.name, entity.category)
        assertEquals("Dr. Tan", entity.title)
        assertEquals("Clinic: 03-1234567", entity.note)
    }

    @Test
    fun `vault entity maps to domain`() {
        val entity = VaultReferenceEntity(
            id = 2L,
            category = VaultCategory.HOME_VEHICLE.name,
            title = "Tyre pressure",
            note = "33 PSI front, 30 PSI rear",
            createdAt = System.currentTimeMillis()
        )
        val domain = entity.toDomain()

        assertEquals(2L, domain.id)
        assertEquals(VaultCategory.HOME_VEHICLE, domain.category)
        assertEquals("Tyre pressure", domain.title)
    }
}