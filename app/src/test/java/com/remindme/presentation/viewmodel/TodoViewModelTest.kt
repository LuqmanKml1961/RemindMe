package com.remindme.presentation.viewmodel

import com.remindme.FakeTodoRepository
import com.remindme.MainDispatcherRule
import com.remindme.domain.model.TodoItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun TestScope.collectState(vm: TodoViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.uiState.collect()
        }
    }

    @Test
    fun `addTodo inserts todo`() = runTest {
        val repository = FakeTodoRepository()
        val vm = TodoViewModel(repository)

        vm.addTodo("Buy milk")

        assertEquals(1, repository.todos.value.size)
        assertEquals("Buy milk", repository.todos.value.first().text)
    }

    @Test
    fun `addTodo ignores blank text`() = runTest {
        val repository = FakeTodoRepository()
        val vm = TodoViewModel(repository)

        vm.addTodo("  ")
        vm.addTodo("")

        assertTrue(repository.todos.value.isEmpty())
    }

    @Test
    fun `toggleTodo flips completion state`() = runTest {
        val repository = FakeTodoRepository()
        repository.todos.value = listOf(TodoItem(id = 1L, text = "Call mom"))
        val vm = TodoViewModel(repository)

        vm.toggleTodo(1L, true)

        assertTrue(repository.todos.value.first().isCompleted)
    }

    @Test
    fun `deleteTodo removes todo`() = runTest {
        val repository = FakeTodoRepository()
        repository.todos.value = listOf(TodoItem(id = 1L, text = "Clean room"))
        val vm = TodoViewModel(repository)

        vm.deleteTodo(1L)

        assertTrue(repository.todos.value.isEmpty())
    }

    @Test
    fun `deleteCompletedTodos removes only completed`() = runTest {
        val repository = FakeTodoRepository()
        repository.todos.value = listOf(
            TodoItem(id = 1L, text = "Done task", isCompleted = true),
            TodoItem(id = 2L, text = "Pending task", isCompleted = false)
        )
        val vm = TodoViewModel(repository)

        vm.deleteCompletedTodos()

        assertEquals(1, repository.todos.value.size)
        assertEquals("Pending task", repository.todos.value.first().text)
    }

    @Test
    fun `uiState lists todos`() = runTest {
        val repository = FakeTodoRepository()
        repository.todos.value = listOf(TodoItem(id = 1L, text = "One"))
        val vm = TodoViewModel(repository)

        collectState(vm)

        assertEquals(1, vm.uiState.value.todos.size)
        assertEquals("One", vm.uiState.value.todos[0].text)
    }
}