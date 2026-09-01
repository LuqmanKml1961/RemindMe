package com.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remindme.data.local.AlarmScheduler
import com.remindme.domain.usecase.ShareReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ImportUiState(
    val processing: Boolean = false,
    val result: ImportResult? = null,
    val importedTitle: String? = null
)

enum class ImportResult {
    SUCCESS,
    NOT_FOUND
}

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val shareReminderUseCase: ShareReminderUseCase,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState

    fun importReminder(shareId: String) {
        viewModelScope.launch {
            _uiState.value = ImportUiState(processing = true)
            val imported = shareReminderUseCase.importReminder(shareId, "Link")
            imported?.let {
                alarmScheduler.schedule(it)
                _uiState.value = ImportUiState(
                    result = ImportResult.SUCCESS,
                    importedTitle = it.title
                )
            } ?: run {
                _uiState.value = ImportUiState(result = ImportResult.NOT_FOUND)
            }
        }
    }

    fun clearResult() {
        _uiState.value = ImportUiState()
    }
}