package com.example.hometasker.presentation.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.usecase.category.DeleteCategoryUseCase
import com.example.hometasker.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase.getAllCategories()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { categories ->
                    _uiState.update { it.copy(categories = categories, isLoading = false) }
                }
        }
    }

    fun onDeleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category)
        }
    }
}
