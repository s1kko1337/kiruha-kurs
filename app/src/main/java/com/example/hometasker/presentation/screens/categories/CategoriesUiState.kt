package com.example.hometasker.presentation.screens.categories

import com.example.hometasker.domain.model.Category

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
