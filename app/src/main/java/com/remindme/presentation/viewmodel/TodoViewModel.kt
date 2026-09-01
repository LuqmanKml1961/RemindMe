package com.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remindme.domain.model.TodoItem
import com.remindme.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    val uiState: StateFlow<TodoUiState> = repository.getAllTodos()
        .map { todos ->
            TodoUiState(todos = todos, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TodoUiState(isLoading = true)
        )

    fun addTodo(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.insertTodo(TodoItem(text = text))
        }
    }

    fun toggleTodo(id: Long, completed: Boolean) {
        viewModelScope.launch {
            val todos = repository.getAllTodos().first()
            val todo = todos.firstOrNull { it.id == id }
            todo?.let {
                repository.updateTodo(it.copy(isCompleted = completed))
            }
        }
    }

    fun deleteTodo(id: Long) {
        viewModelScope.launch {
            repository.deleteTodo(id)
        }
    }

    fun deleteCompletedTodos() {
        viewModelScope.launch {
            repository.deleteCompletedTodos()
        }
    }
}
