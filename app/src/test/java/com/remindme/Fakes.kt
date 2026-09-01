package com.remindme

import com.remindme.domain.model.Reminder
import com.remindme.domain.model.TodoItem
import com.remindme.domain.repository.ReminderRepository
import com.remindme.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeReminderRepository : ReminderRepository {

    val reminders = MutableStateFlow<List<Reminder>>(emptyList())
    private var nextId = 1L

    override fun getAllReminders(): Flow<List<Reminder>> = reminders

    override fun getRemindersByType(type: String): Flow<List<Reminder>> =
        reminders.map { list -> list.filter { it.type.name == type } }

    override fun getReminderById(id: Long): Flow<Reminder?> =
        reminders.map { list -> list.firstOrNull { it.id == id } }

    override fun getReminderByShareId(shareId: String): Flow<Reminder?> =
        reminders.map { list -> list.firstOrNull { it.shareId == shareId } }

    override suspend fun insertReminder(reminder: Reminder): Long {
        val withId = reminder.copy(id = nextId++)
        reminders.value = reminders.value + withId
        return withId.id
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminders.value = reminders.value.map {
            if (it.id == reminder.id) reminder else it
        }
    }

    override suspend fun deleteReminder(id: Long) {
        reminders.value = reminders.value.filterNot { it.id == id }
    }

    override suspend fun completeReminder(id: Long) {
        reminders.value = reminders.value.map {
            if (it.id == id) it.copy(isCompleted = true) else it
        }
    }

    override suspend fun archiveReminder(id: Long) {
        reminders.value = reminders.value.map {
            if (it.id == id) it.copy(isArchived = true) else it
        }
    }

    override suspend fun deleteCompletedReminders() {
        reminders.value = reminders.value.filterNot { it.isCompleted && it.autoDelete }
    }

    override suspend fun getCompletedReminders(): List<Reminder> =
        reminders.value.filter { it.isCompleted }

    override suspend fun getAllScheduledReminders(): List<Reminder> =
        reminders.value.filter { it.dueDate != null && !it.isCompleted && !it.isArchived }
}

class FakeTodoRepository : TodoRepository {

    val todos = MutableStateFlow<List<TodoItem>>(emptyList())
    private var nextId = 1L

    override fun getAllTodos(): Flow<List<TodoItem>> = todos

    override fun getTodosByReminderId(reminderId: Long): Flow<List<TodoItem>> =
        todos.map { list -> list.filter { it.reminderId == reminderId } }

    override suspend fun insertTodo(todo: TodoItem): Long {
        val withId = todo.copy(id = nextId++)
        todos.value = todos.value + withId
        return withId.id
    }

    override suspend fun updateTodo(todo: TodoItem) {
        todos.value = todos.value.map {
            if (it.id == todo.id) todo else it
        }
    }

    override suspend fun deleteTodo(id: Long) {
        todos.value = todos.value.filterNot { it.id == id }
    }

    override suspend fun completeTodo(id: Long) {
        todos.value = todos.value.map {
            if (it.id == id) it.copy(isCompleted = true) else it
        }
    }

    override suspend fun deleteCompletedTodos() {
        todos.value = todos.value.filterNot { it.isCompleted }
    }
}