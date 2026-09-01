package com.remindme.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY priority DESC, created_at DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo_items WHERE reminder_id = :reminderId ORDER BY created_at DESC")
    fun getTodosByReminderId(reminderId: Long): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun deleteTodo(id: Long)

    @Query("UPDATE todo_items SET is_completed = 1 WHERE id = :id")
    suspend fun completeTodo(id: Long)

    @Query("DELETE FROM todo_items WHERE is_completed = 1")
    suspend fun deleteCompletedTodos()
}
