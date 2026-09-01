package com.remindme.data.repository

import com.remindme.data.local.TodoDao
import com.remindme.data.local.toDomain
import com.remindme.data.local.toEntity
import com.remindme.domain.model.TodoItem
import com.remindme.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoRepository {

    override fun getAllTodos(): Flow<List<TodoItem>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTodosByReminderId(reminderId: Long): Flow<List<TodoItem>> {
        return todoDao.getTodosByReminderId(reminderId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTodo(todo: TodoItem): Long {
        return todoDao.insertTodo(todo.toEntity())
    }

    override suspend fun updateTodo(todo: TodoItem) {
        todoDao.updateTodo(todo.toEntity())
    }

    override suspend fun deleteTodo(id: Long) {
        todoDao.deleteTodo(id)
    }

    override suspend fun completeTodo(id: Long) {
        todoDao.completeTodo(id)
    }

    override suspend fun deleteCompletedTodos() {
        todoDao.deleteCompletedTodos()
    }
}
