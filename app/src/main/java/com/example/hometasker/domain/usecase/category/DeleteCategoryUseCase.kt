package com.example.hometasker.domain.usecase.category

import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * UseCase для удаления категории
 */
class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        categoryRepository.deleteCategory(category)
    }
}
