package com.remindme.domain.repository

import com.remindme.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<TodoItem>>
    fun getTodosByReminderId(reminderId: Long): Flow<List<TodoItem>>
    suspend fun insertTodo(todo: TodoItem): Long
    suspend fun updateTodo(todo: TodoItem)
    suspend fun deleteTodo(id: Long)
    suspend fun completeTodo(id: Long)
    suspend fun deleteCompletedTodos()
}
