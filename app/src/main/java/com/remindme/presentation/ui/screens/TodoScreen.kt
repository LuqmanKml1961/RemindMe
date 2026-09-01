package com.remindme.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remindme.presentation.ui.components.BrutButton
import com.remindme.presentation.ui.components.BrutOutlinedButton
import com.remindme.presentation.ui.components.BrutPass
import com.remindme.presentation.ui.components.SectionHeader
import com.remindme.presentation.ui.components.TodoItemCard
import com.remindme.presentation.viewmodel.TodoViewModel

@Composable
fun TodoScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var newTodoText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "TODO",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTodoText,
                onValueChange = { newTodoText = it },
                label = { Text("ADD A TASK") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (newTodoText.isNotBlank()) {
                        viewModel.addTodo(newTodoText)
                        newTodoText = ""
                    }
                }),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BrutButton(
                text = "Add",
                enabled = newTodoText.isNotBlank(),
                onClick = {
                    viewModel.addTodo(newTodoText)
                    newTodoText = ""
                }
            )
        }

        val doneCount = uiState.todos.count { it.isCompleted }
        if (doneCount > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            BrutOutlinedButton(
                text = "Clear completed ($doneCount)",
                onClick = viewModel::deleteCompletedTodos,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        SectionHeader(
            title = "Tasks",
            trailing = "${uiState.todos.size} TOTAL"
        )
        Spacer(modifier = Modifier.height(2.dp))

        if (uiState.todos.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            BrutPass(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No tasks yet.\nAdd one above — or turn a reminder into a task when you create it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
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