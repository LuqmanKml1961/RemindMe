package com.remindme.domain.usecase

import com.remindme.domain.model.TodoItem
import com.remindme.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    fun getTodosByReminderId(reminderId: Long): Flow<List<TodoItem>> {
        return repository.getTodosByReminderId(reminderId)
    }

    suspend fun createTodoForReminder(reminderId: Long, text: String): Long {
        val todo = TodoItem(
            text = text,
            reminderId = reminderId
        )
        return repository.insertTodo(todo)
    }
}
