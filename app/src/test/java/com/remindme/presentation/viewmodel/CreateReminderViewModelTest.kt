package com.remindme.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.remindme.FakeReminderRepository
import com.remindme.FakeTodoRepository
import com.remindme.MainDispatcherRule
import com.remindme.data.local.AlarmScheduler
import com.remindme.domain.model.Medication
import com.remindme.domain.model.RecurrenceRule
import com.remindme.domain.model.RecurrenceUnit
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import com.remindme.domain.usecase.CreateReminderUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CreateReminderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val alarmScheduler = mock<AlarmScheduler>()

    private fun newViewModel(
        reminderRepository: FakeReminderRepository = FakeReminderRepository(),
        todoRepository: FakeTodoRepository = FakeTodoRepository(),
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ) = CreateReminderViewModel(
        createReminderUseCase = CreateReminderUseCase(reminderRepository),
        repository = reminderRepository,
        todoRepository = todoRepository,
        alarmScheduler = alarmScheduler,
        savedStateHandle = savedStateHandle
    )

    @Test
    fun `saveReminder inserts reminder and schedules alarm`() = runTest {
        val reminderRepository = FakeReminderRepository()
        val vm = newViewModel(reminderRepository = reminderRepository)

        vm.updateTitle("Buy milk")
        vm.saveReminder()

        assertEquals(1, reminderRepository.reminders.value.size)
        val saved = reminderRepository.reminders.value.first()
        assertEquals("Buy milk", saved.title)
        assertNotNull(saved.shareId)
        verify(alarmScheduler).schedule(saved)
    }

    @Test
    fun `saveReminder without title does nothing`() = runTest {
        val reminderRepository = FakeReminderRepository()
        val vm = newViewModel(reminderRepository = reminderRepository)

        vm.saveReminder()

        assertTrue(reminderRepository.reminders.value.isEmpty())
    }

    @Test
    fun `saveReminder with alsoAddTodo creates linked todo`() = runTest {
        val reminderRepository = FakeReminderRepository()
        val todoRepository = FakeTodoRepository()
        val vm = newViewModel(reminderRepository, todoRepository)

        vm.updateTitle("Pay rent")
        vm.updateAlsoAddTodo(true)
        vm.saveReminder()

        assertEquals(1, todoRepository.todos.value.size)
        val todo = todoRepository.todos.value.first()
        assertEquals(reminderRepository.reminders.value.first().id, todo.reminderId)
    }

    @Test
    fun `editing preserves shareId and share source`() = runTest {
        val reminderRepository = FakeReminderRepository()
        reminderRepository.reminders.value = listOf(
            Reminder(
                id = 1L,
                title = "Old title",
                type = ReminderType.GENERAL,
                shareId = "keep-share",
                sharedBy = "Link"
            )
        )
        val saveState = SavedStateHandle(mapOf("reminderId" to 1L))
        val vm = newViewModel(reminderRepository, savedStateHandle = saveState)

        assertTrue(vm.uiState.value.isEditing)
        vm.updateTitle("New title")
        vm.saveReminder()

        val edited = reminderRepository.reminders.value.first()
        assertEquals("New title", edited.title)
        assertEquals(1L, edited.id)
        assertEquals("keep-share", edited.shareId)
        assertEquals("Link", edited.sharedBy)
    }

    @Test
    fun `medical type with medications saved`() = runTest {
        val reminderRepository = FakeReminderRepository()
        val vm = newViewModel(reminderRepository)

        vm.updateTitle("Medicine")
        vm.updateType(ReminderType.MEDICAL)
        vm.addMedication()
        vm.updateMedicationName(0, "Metformin")
        vm.updateMedicationDosage(0, "500mg")
        vm.updateMedicationInstructions(0, "After food")
        vm.saveReminder()

        val saved = reminderRepository.reminders.value.first()
        assertEquals(1, saved.medications.size)
        assertEquals("Metformin", saved.medications[0].name)
        assertEquals("500mg", saved.medications[0].dosage)
        assertEquals("After food", saved.medications[0].instructions)
    }

    @Test
    fun `non medical type leaves medications empty`() = runTest {
        val reminderRepository = FakeReminderRepository()
        val vm = newViewModel(reminderRepository)

        vm.updateTitle("Charge phone")
        vm.saveReminder()

        val saved = reminderRepository.reminders.value.first()
        assertTrue(saved.medications.isEmpty())
        assertNull(saved.recurrence)
    }

    @Test
    fun `monthly type with recurrence saved`() = runTest {
        val reminderRepository = FakeReminderRepository()
        val vm = newViewModel(reminderRepository)

        vm.updateTitle("Rent")
        vm.updateType(ReminderType.MONTHLY)
        vm.updateAmount("1200")
        vm.selectRecurrence(RecurrenceRule(RecurrenceUnit.MONTHLY, 1))
        vm.saveReminder()

        val saved = reminderRepository.reminders.value.first()
        assertEquals(1200.0, saved.amount!!, 0.0)
        assertEquals(RecurrenceUnit.MONTHLY, saved.recurrence?.unit)
    }
}