package com.example.hometasker.presentation.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.usecase.category.CreateCategoryUseCase
import com.example.hometasker.domain.usecase.category.DeleteCategoryUseCase
import com.example.hometasker.domain.usecase.category.GetCategoriesUseCase
import com.example.hometasker.domain.usecase.category.UpdateCategoryUseCase
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
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
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

    fun onShowAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, editingCategory = null) }
    }

    fun onHideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun onShowEditDialog(category: Category) {
        _uiState.update { it.copy(showEditDialog = true, editingCategory = category) }
    }

    fun onHideEditDialog() {
        _uiState.update { it.copy(showEditDialog = false, editingCategory = null) }
    }

    fun onShowDeleteConfirmDialog(category: Category) {
        _uiState.update { it.copy(showDeleteConfirmDialog = true, categoryToDelete = category) }
    }

    fun onHideDeleteConfirmDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false, categoryToDelete = null) }
    }

    fun onCreateCategory(name: String, colorHex: String, parentId: Long?) {
        viewModelScope.launch {
            try {
                val category = Category(
                    name = name.trim(),
                    colorHex = colorHex,
                    parentId = parentId,
                    isDefault = false
                )
                createCategoryUseCase(category)
                _uiState.update { it.copy(showAddDialog = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onUpdateCategory(category: Category) {
        viewModelScope.launch {
            try {
                updateCategoryUseCase(category)
                _uiState.update { it.copy(showEditDialog = false, editingCategory = null, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onDeleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                deleteCategoryUseCase(category)
                _uiState.update { it.copy(showDeleteConfirmDialog = false, categoryToDelete = null, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onConfirmDelete() {
        _uiState.value.categoryToDelete?.let { category ->
            onDeleteCategory(category)
        }
    }
}
