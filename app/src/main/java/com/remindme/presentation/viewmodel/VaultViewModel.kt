package com.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remindme.domain.model.VaultCategory
import com.remindme.domain.model.VaultReference
import com.remindme.domain.repository.VaultReferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VaultUiState(
    val search: String = "",
    val filter: VaultCategory? = null,
    val references: List<VaultReference> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val repository: VaultReferenceRepository
) : ViewModel() {

    private var allReferences: List<VaultReference> = emptyList()

    private val _uiState = MutableStateFlow(VaultUiState(isLoading = true))
    val uiState: StateFlow<VaultUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.getAllReferences().collect { all ->
                allReferences = all
                _uiState.update {
                    it.copy(
                        references = filterAll(all, it.search, it.filter),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun filterAll(
        all: List<VaultReference>,
        search: String,
        filter: VaultCategory?
    ): List<VaultReference> {
        val query = search.trim()
        return all.filter { reference ->
            (filter == null || reference.category == filter) &&
                (query.isEmpty() ||
                    reference.title.contains(query, ignoreCase = true) ||
                    reference.note.contains(query, ignoreCase = true))
        }
    }

    fun setSearch(value: String) {
        val state = _uiState.value
        _uiState.update {
            it.copy(search = value, references = filterAll(allReferences, value, state.filter))
        }
    }

    fun setFilter(value: VaultCategory?) {
        val state = _uiState.value
        _uiState.update {
            it.copy(filter = value, references = filterAll(allReferences, state.search, value))
        }
    }

    fun saveReference(
        id: Long,
        category: VaultCategory,
        title: String,
        note: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.upsertReference(
                VaultReference(
                    id = id,
                    category = category,
                    title = title.trim(),
                    note = note.trim()
                )
            )
        }
    }

    fun deleteReference(id: Long) {
        viewModelScope.launch {
            repository.deleteReference(id)
        }
    }
}