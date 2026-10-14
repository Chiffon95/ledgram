package com.gwolbisa.ledgram.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gwolbisa.ledgram.data.EditorPreferences
import com.gwolbisa.ledgram.data.EditorPreferencesRepository
import com.gwolbisa.ledgram.data.toEditorPreferences
import com.gwolbisa.ledgram.data.toEditorUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val PERSISTENCE_DEBOUNCE_MILLIS = 300L

@OptIn(FlowPreview::class)
class EditorViewModel(
    private val repository: EditorPreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditorUiState?>(null)
    val uiState: StateFlow<EditorUiState?> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = repository.preferences.first().toEditorUiState()

            _uiState
                .filterNotNull()
                .drop(1)
                .debounce(PERSISTENCE_DEBOUNCE_MILLIS)
                .collectLatest { state ->
                    repository.save(state.toEditorPreferences())
                }
        }
    }

    fun setUiState(state: EditorUiState) {
        if (_uiState.value != null) {
            _uiState.value = state
        }
    }

    fun applyCanonicalDefaultsAfterReset() {
        if (_uiState.value != null) {
            _uiState.value = EditorPreferences().toEditorUiState()
        }
    }

    class Factory(
        private val repository: EditorPreferencesRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(EditorViewModel::class.java))
            return EditorViewModel(repository) as T
        }
    }
}
