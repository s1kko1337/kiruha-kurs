package com.example.hometasker.presentation.screens.categories

import com.example.hometasker.domain.model.Category

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val editingCategory: Category? = null,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val categoryToDelete: Category? = null
)

// Предустановленные цвета для категорий
val predefinedColors = listOf(
    "#4CAF50", // Green
    "#2196F3", // Blue
    "#FF9800", // Orange
    "#E91E63", // Pink
    "#9C27B0", // Purple
    "#795548", // Brown
    "#607D8B", // Blue Grey
    "#F44336", // Red
    "#00BCD4", // Cyan
    "#FFEB3B", // Yellow
    "#3F51B5", // Indigo
    "#009688", // Teal
    "#FF5722", // Deep Orange
    "#673AB7", // Deep Purple
    "#8BC34A", // Light Green
    "#03A9F4"  // Light Blue
)
