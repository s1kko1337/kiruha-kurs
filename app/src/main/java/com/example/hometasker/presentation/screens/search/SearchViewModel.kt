package com.example.hometasker.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.usecase.task.SearchTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTasksUseCase: SearchTasksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }

        // Debounce search
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300)
                search()
            }
        } else {
            _uiState.update { it.copy(results = emptyList(), hasSearched = false) }
        }
    }

    fun search() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasSearched = true) }

            searchTasksUseCase(query)
                .catch {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { results ->
                    _uiState.update { it.copy(results = results, isLoading = false) }
                }
        }
    }
}
