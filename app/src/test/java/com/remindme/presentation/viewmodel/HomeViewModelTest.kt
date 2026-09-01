package com.remindme.presentation.viewmodel

import com.remindme.FakeReminderRepository
import com.remindme.MainDispatcherRule
import com.remindme.data.local.AlarmScheduler
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import com.remindme.domain.usecase.ShareReminderUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val alarmScheduler = mock<AlarmScheduler>()

    private fun TestScope.collectState(vm: HomeViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.uiState.collect()
        }
    }

    private fun viewModel(repository: FakeReminderRepository) = HomeViewModel(
        repository = repository,
        alarmScheduler = alarmScheduler,
        shareReminderUseCase = ShareReminderUseCase(repository)
    )

    @Test
    fun `uiState reflects reminders from repository`() = runTest {
        val repository = FakeReminderRepository()
        repository.reminders.value = listOf(
            Reminder(id = 1L, title = "Buy milk", type = ReminderType.GENERAL)
        )

        val vm = viewModel(repository)
        collectState(vm)

        assertEquals(1, vm.uiState.value.reminders.size)
        assertEquals("Buy milk", vm.uiState.value.reminders[0].title)
        assertEquals(false, vm.uiState.value.isLoading)
    }

    @Test
    fun `completeReminder marks completed and cancels alarm`() = runTest {
        val repository = FakeReminderRepository()
        val reminder = Reminder(id = 1L, title = "Take medicine", type = ReminderType.MEDICAL)
        repository.reminders.value = listOf(reminder)

        val vm = viewModel(repository)
        vm.completeReminder(1L)

        assertTrue(repository.reminders.value.first().isCompleted)
        verify(alarmScheduler).cancel(reminder)
    }

    @Test
    fun `completeReminder auto-deletes reminders with autoDelete on`() = runTest {
        val repository = FakeReminderRepository()
        repository.reminders.value = listOf(
            Reminder(id = 1L, title = "Pay rent", type = ReminderType.MONTHLY, autoDelete = true)
        )

        val vm = viewModel(repository)
        vm.completeReminder(1L)

        assertTrue(repository.reminders.value.isEmpty())
    }

    @Test
    fun `deleteReminder removes reminder and cancels alarm`() = runTest {
        val repository = FakeReminderRepository()
        val reminder = Reminder(id = 1L, title = "Meeting", type = ReminderType.GENERAL)
        repository.reminders.value = listOf(reminder)

        val vm = viewModel(repository)
        vm.deleteReminder(1L)

        assertTrue(repository.reminders.value.isEmpty())
        verify(alarmScheduler).cancel(reminder)
    }

    @Test
    fun `toggleAutoDelete updates reminder`() = runTest {
        val repository = FakeReminderRepository()
        repository.reminders.value = listOf(
            Reminder(id = 1L, title = "Buy milk", type = ReminderType.GENERAL, autoDelete = false)
        )

        val vm = viewModel(repository)
        vm.toggleAutoDelete(1L, true)

        assertTrue(repository.reminders.value.first().autoDelete)
    }
}