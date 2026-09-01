package com.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remindme.domain.model.Reminder
import com.remindme.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getAllReminders()
        .map { reminders ->
            HomeUiState(reminders = reminders, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )

    fun completeReminder(id: Long) {
        viewModelScope.launch {
            repository.completeReminder(id)
            cleanupCompleted()
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    fun toggleAutoDelete(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            val reminder = repository.getReminderById(id).first()
            reminder?.let {
                repository.updateReminder(it.copy(autoDelete = enabled))
            }
        }
    }

    private suspend fun cleanupCompleted() {
        val completed = repository.getCompletedReminders()
        val autoDeleteReminders = completed.filter { it.autoDelete }
        autoDeleteReminders.forEach { reminder ->
            repository.deleteReminder(reminder.id)
        }
    }
}
