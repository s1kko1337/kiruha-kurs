package com.example.hometasker.presentation.screens.statistics

import com.example.hometasker.domain.usecase.statistics.Statistics

data class StatisticsUiState(
    val statistics: Statistics? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
