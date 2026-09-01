package com.remindme.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remindme.data.local.AlarmScheduler
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import com.remindme.domain.model.TodoItem
import com.remindme.domain.repository.ReminderRepository
import com.remindme.domain.repository.TodoRepository
import com.remindme.domain.usecase.CreateReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class CreateReminderUiState(
    val title: String = "",
    val description: String = "",
    val type: ReminderType = ReminderType.GENERAL,
    val dueDate: LocalDateTime? = null,
    val autoDelete: Boolean = false,
    val alsoAddTodo: Boolean = false,
    val medicineName: String = "",
    val dosage: String = "",
    val instructions: String = "",
    val amount: String = "",
    val recurrenceDays: String = "",
    val isEditing: Boolean = false,
    val editingId: Long = 0L,
    val isValid: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class CreateReminderViewModel @Inject constructor(
    private val createReminderUseCase: CreateReminderUseCase,
    private val repository: ReminderRepository,
    private val todoRepository: TodoRepository,
    private val alarmScheduler: AlarmScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReminderUiState())
    val uiState: StateFlow<CreateReminderUiState> = _uiState

    init {
        val reminderId = savedStateHandle.get<Long>("reminderId")
        if (reminderId != null) {
            loadReminder(reminderId)
        }
    }

    private fun loadReminder(id: Long) {
        viewModelScope.launch {
            val reminder = repository.getReminderById(id).firstOrNull()
            reminder?.let { r ->
                _uiState.value = CreateReminderUiState(
                    title = r.title,
                    description = r.description,
                    type = r.type,
                    dueDate = r.dueDate,
                    autoDelete = r.autoDelete,
                    medicineName = r.medicineName ?: "",
                    dosage = r.dosage ?: "",
                    instructions = r.instructions ?: "",
                    amount = r.amount?.toString() ?: "",
                    recurrenceDays = r.recurrenceDays?.toString() ?: "",
                    isEditing = true,
                    editingId = r.id,
                    isValid = true
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            isValid = title.isNotBlank()
        )
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateType(type: ReminderType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun updateDueDate(date: LocalDateTime?) {
        _uiState.value = _uiState.value.copy(dueDate = date)
    }

    fun updateAutoDelete(autoDelete: Boolean) {
        _uiState.value = _uiState.value.copy(autoDelete = autoDelete)
    }

    fun updateAlsoAddTodo(alsoAddTodo: Boolean) {
        _uiState.value = _uiState.value.copy(alsoAddTodo = alsoAddTodo)
    }

    fun updateMedicineName(name: String) {
        _uiState.value = _uiState.value.copy(medicineName = name)
    }

    fun updateDosage(dosage: String) {
        _uiState.value = _uiState.value.copy(dosage = dosage)
    }

    fun updateInstructions(instructions: String) {
        _uiState.value = _uiState.value.copy(instructions = instructions)
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun updateRecurrenceDays(days: String) {
        _uiState.value = _uiState.value.copy(recurrenceDays = days)
    }

    fun saveReminder() {
        val state = _uiState.value
        if (!state.isValid) return

        viewModelScope.launch {
            val reminder = buildEditedReminder(state)

            if (state.isEditing) {
                repository.updateReminder(reminder)
                alarmScheduler.cancel(reminder)
                alarmScheduler.schedule(reminder)
            } else {
                val reminderId = createReminderUseCase(reminder)
                val saved = reminder.copy(id = reminderId)
                alarmScheduler.schedule(saved)
                if (state.alsoAddTodo) {
                    todoRepository.insertTodo(
                        TodoItem(text = "Reminder: ${reminder.title}", reminderId = reminderId)
                    )
                }
            }
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    private fun buildReminder(state: CreateReminderUiState): Reminder {
        return Reminder(
            id = if (state.isEditing) state.editingId else 0L,
            title = state.title,
            description = state.description,
            type = state.type,
            dueDate = state.dueDate,
            autoDelete = state.autoDelete,
            medicineName = if (state.type == ReminderType.MEDICAL) state.medicineName.ifBlank { null } else null,
            dosage = if (state.type == ReminderType.MEDICAL) state.dosage.ifBlank { null } else null,
            instructions = if (state.type == ReminderType.MEDICAL) state.instructions.ifBlank { null } else null,
            amount = if (state.type == ReminderType.MONTHLY) state.amount.toDoubleOrNull() else null,
            recurrenceDays = if (state.type == ReminderType.MONTHLY) state.recurrenceDays.toIntOrNull() else null
        )
    }

    private suspend fun buildEditedReminder(state: CreateReminderUiState): Reminder {
        if (!state.isEditing) return buildReminder(state)

        val existing = repository.getReminderById(state.editingId).firstOrNull()
        val draft = buildReminder(state)
        return existing?.copy(
            title = draft.title,
            description = draft.description,
            type = draft.type,
            dueDate = draft.dueDate,
            autoDelete = draft.autoDelete,
            medicineName = draft.medicineName,
            dosage = draft.dosage,
            instructions = draft.instructions,
            amount = draft.amount,
            recurrenceDays = draft.recurrenceDays
        ) ?: draft
    }
}