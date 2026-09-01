package com.remindme.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remindme.data.local.AlarmScheduler
import com.remindme.domain.model.Medication
import com.remindme.domain.model.RecurrenceRule
import com.remindme.domain.model.RecurrenceUnit
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
    val medications: List<Medication> = emptyList(),
    val amount: String = "",
    val recurrence: RecurrenceRule? = null,
    val everyNDaysText: String = "",
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
                    medications = r.medications,
                    amount = r.amount?.toString() ?: "",
                    recurrence = r.recurrence,
                    everyNDaysText =
                        r.recurrence?.takeIf { it.unit == RecurrenceUnit.EVERY_N_DAYS }?.interval?.toString()
                            ?: "",
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

    fun applyQuickPreset(minutes: Int) {
        _uiState.value = _uiState.value.copy(dueDate = LocalDateTime.now().plusMinutes(minutes.toLong()))
    }

    fun updateTime(hour: Int, minute: Int) {
        val base = _uiState.value.dueDate ?: LocalDateTime.now()
        _uiState.value = _uiState.value.copy(
            dueDate = LocalDateTime.of(base.year, base.month, base.dayOfMonth, hour, minute)
        )
    }

    fun applyDateTime(date: java.time.LocalDate?, hour: Int, minute: Int) {
        val base = _uiState.value.dueDate ?: LocalDateTime.now()
        val resolvedDate = date ?: base.toLocalDate()
        _uiState.value = _uiState.value.copy(
            dueDate = LocalDateTime.of(resolvedDate, java.time.LocalTime.of(hour, minute))
        )
    }

    fun updateAutoDelete(autoDelete: Boolean) {
        _uiState.value = _uiState.value.copy(autoDelete = autoDelete)
    }

    fun updateAlsoAddTodo(alsoAddTodo: Boolean) {
        _uiState.value = _uiState.value.copy(alsoAddTodo = alsoAddTodo)
    }

    fun addMedication() {
        _uiState.value = _uiState.value.copy(medications = _uiState.value.medications + Medication())
    }

    fun updateMedicationName(index: Int, name: String) {
        _uiState.value = _uiState.value.copy(
            medications = _uiState.value.medications.mapIndexed { i, med ->
                if (i == index) med.copy(name = name) else med
            }
        )
    }

    fun updateMedicationDosage(index: Int, dosage: String) {
        _uiState.value = _uiState.value.copy(
            medications = _uiState.value.medications.mapIndexed { i, med ->
                if (i == index) med.copy(dosage = dosage) else med
            }
        )
    }

    fun updateMedicationInstructions(index: Int, instructions: String) {
        _uiState.value = _uiState.value.copy(
            medications = _uiState.value.medications.mapIndexed { i, med ->
                if (i == index) med.copy(instructions = instructions) else med
            }
        )
    }

    fun removeMedication(index: Int) {
        _uiState.value = _uiState.value.copy(
            medications = _uiState.value.medications.filterIndexed { i, _ -> i != index }
        )
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun selectRecurrence(unit: RecurrenceUnit?) {
        val state = _uiState.value
        val recurrence = when (unit) {
            null, RecurrenceUnit.EVERY_N_DAYS -> null
            else -> RecurrenceRule(unit)
        }
        _uiState.value = if (recurrence == null) {
            if (unit == RecurrenceUnit.EVERY_N_DAYS) {
                state.copy(
                    recurrence = RecurrenceRule(
                        RecurrenceUnit.EVERY_N_DAYS,
                        state.everyNDaysText.toIntOrNull()?.takeIf { it > 0 } ?: 1
                    )
                )
            } else {
                state.copy(recurrence = null)
            }
        } else {
            state.copy(recurrence = recurrence)
        }
    }

    fun updateEveryNDays(text: String) {
        val state = _uiState.value
        val interval = text.toIntOrNull()?.takeIf { it > 0 } ?: 1
        val newText = text.filter { it.isDigit() }.take(3)
        _uiState.value = state.copy(
            everyNDaysText = newText,
            recurrence = state.recurrence?.takeIf { it.unit == RecurrenceUnit.EVERY_N_DAYS }
                ?.copy(interval = interval)
                ?: state.recurrence
        )
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
        val medications = if (state.type == ReminderType.MEDICAL) {
            state.medications
                .map { it.copy(name = it.name.trim()) }
                .filter { it.name.isNotBlank() }
        } else {
            emptyList()
        }
        val recurrence = state.recurrence?.let {
            if (it.unit == RecurrenceUnit.EVERY_N_DAYS) {
                it.copy(interval = it.interval.coerceAtLeast(1))
            } else {
                it
            }
        }
        return Reminder(
            id = if (state.isEditing) state.editingId else 0L,
            title = state.title,
            description = state.description,
            type = state.type,
            dueDate = state.dueDate,
            autoDelete = state.autoDelete,
            medications = medications,
            amount = if (state.type == ReminderType.MONTHLY) state.amount.toDoubleOrNull() else null,
            recurrence = recurrence
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
            medications = draft.medications,
            amount = draft.amount,
            recurrence = draft.recurrence
        ) ?: draft
    }
}