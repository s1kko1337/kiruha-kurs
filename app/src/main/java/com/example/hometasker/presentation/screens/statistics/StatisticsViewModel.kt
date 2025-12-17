package com.example.hometasker.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            getStatisticsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { statistics ->
                    _uiState.update { it.copy(statistics = statistics, isLoading = false) }
                }
        }
    }
}
