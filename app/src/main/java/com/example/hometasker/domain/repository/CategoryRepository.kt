package com.example.hometasker.domain.repository

import com.example.hometasker.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория категорий
 */
interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getRootCategories(): Flow<List<Category>>
    fun getSubcategories(parentId: Long): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun insertDefaultCategories()
}
