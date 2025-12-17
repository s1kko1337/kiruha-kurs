package com.example.hometasker.domain.usecase.category

import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase для получения списка категорий
 */
class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(parentId: Long? = null): Flow<List<Category>> {
        return if (parentId == null) {
            categoryRepository.getRootCategories()
        } else {
            categoryRepository.getSubcategories(parentId)
        }
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryRepository.getAllCategories()
    }
}
