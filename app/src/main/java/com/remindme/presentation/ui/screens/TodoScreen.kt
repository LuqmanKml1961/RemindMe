package com.remindme.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import com.remindme.presentation.ui.components.TodoItemCard
import com.remindme.presentation.viewmodel.TodoViewModel

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var newTodoText by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Add todo input
        OutlinedTextField(
            value = newTodoText,
            onValueChange = { newTodoText = it },
            label = { Text("Add todo...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.addTodo(newTodoText)
                newTodoText = ""
            }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Clear completed button
        if (uiState.todos.any { it.isCompleted }) {
            TextButton(
                onClick = viewModel::deleteCompletedTodos,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Clear completed")
            }
        }

        if (uiState.todos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "No todos yet.\nAdd a task above.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.todos, key = { it.id }) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onToggle = viewModel::toggleTodo,
                        onDelete = viewModel::deleteTodo
                    )
                }
            }
        }
    }
}
