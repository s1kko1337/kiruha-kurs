package com.example.hometasker.presentation.screens.statistics

import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.usecase.statistics.Statistics

data class StatisticsUiState(
    val statistics: Statistics? = null,
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
