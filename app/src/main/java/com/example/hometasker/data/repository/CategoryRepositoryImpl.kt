package com.example.hometasker.data.repository

import com.example.hometasker.data.local.database.dao.CategoryDao
import com.example.hometasker.data.local.database.entity.CategoryEntity
import com.example.hometasker.data.mapper.CategoryMapper
import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val mapper: CategoryMapper
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getRootCategories(): Flow<List<Category>> {
        return categoryDao.getRootCategories().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getSubcategories(parentId: Long): Flow<List<Category>> {
        return categoryDao.getSubcategories(parentId).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insert(mapper.toEntity(category))
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.update(mapper.toEntity(category))
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.delete(mapper.toEntity(category))
    }

    override suspend fun insertDefaultCategories() {
        if (categoryDao.getCategoriesCount() > 0) return

        val defaultCategories = listOf(
            // Учёба
            CategoryEntity(name = "Учёба", colorHex = "#4CAF50", iconName = "school", sortOrder = 0, isDefault = true),
            // Уборка
            CategoryEntity(name = "Уборка", colorHex = "#2196F3", iconName = "cleaning_services", sortOrder = 1, isDefault = true),
            // Покупки
            CategoryEntity(name = "Покупки", colorHex = "#FF9800", iconName = "shopping_cart", sortOrder = 2, isDefault = true),
            // Готовка
            CategoryEntity(name = "Готовка", colorHex = "#E91E63", iconName = "restaurant", sortOrder = 3, isDefault = true),
            // Здоровье
            CategoryEntity(name = "Здоровье", colorHex = "#9C27B0", iconName = "fitness_center", sortOrder = 4, isDefault = true),
            // Дом
            CategoryEntity(name = "Дом", colorHex = "#795548", iconName = "home", sortOrder = 5, isDefault = true),
            // Прочее
            CategoryEntity(name = "Прочее", colorHex = "#607D8B", iconName = "more_horiz", sortOrder = 6, isDefault = true)
        )

        categoryDao.insertAll(defaultCategories)

        // Получаем ID вставленных категорий для подкатегорий
        val studyId = categoryDao.getCategoriesCount().toLong() - 6
        val cleaningId = studyId + 1
        val shoppingId = studyId + 2
        val cookingId = studyId + 3
        val healthId = studyId + 4
        val homeId = studyId + 5

        val subcategories = listOf(
            // Подкатегории Учёбы
            CategoryEntity(name = "ВУЗ", parentId = studyId, colorHex = "#4CAF50", sortOrder = 0, isDefault = true),
            CategoryEntity(name = "Курсы", parentId = studyId, colorHex = "#4CAF50", sortOrder = 1, isDefault = true),
            CategoryEntity(name = "Стажировка", parentId = studyId, colorHex = "#4CAF50", sortOrder = 2, isDefault = true),
            CategoryEntity(name = "Самообразование", parentId = studyId, colorHex = "#4CAF50", sortOrder = 3, isDefault = true),

            // Подкатегории Уборки
            CategoryEntity(name = "Ежедневная", parentId = cleaningId, colorHex = "#2196F3", sortOrder = 0, isDefault = true),
            CategoryEntity(name = "Генеральная", parentId = cleaningId, colorHex = "#2196F3", sortOrder = 1, isDefault = true),
            CategoryEntity(name = "Стирка", parentId = cleaningId, colorHex = "#2196F3", sortOrder = 2, isDefault = true),

            // Подкатегории Покупок
            CategoryEntity(name = "Продукты", parentId = shoppingId, colorHex = "#FF9800", sortOrder = 0, isDefault = true),
            CategoryEntity(name = "Бытовое", parentId = shoppingId, colorHex = "#FF9800", sortOrder = 1, isDefault = true),
            CategoryEntity(name = "Одежда", parentId = shoppingId, colorHex = "#FF9800", sortOrder = 2, isDefault = true),

            // Подкатегории Готовки
            CategoryEntity(name = "Завтрак", parentId = cookingId, colorHex = "#E91E63", sortOrder = 0, isDefault = true),
            CategoryEntity(name = "Обед", parentId = cookingId, colorHex = "#E91E63", sortOrder = 1, isDefault = true),
            CategoryEntity(name = "Ужин", parentId = cookingId, colorHex = "#E91E63", sortOrder = 2, isDefault = true),
            CategoryEntity(name = "Заготовки", parentId = cookingId, colorHex = "#E91E63", sortOrder = 3, isDefault = true),

            // Подкатегории Здоровья
            CategoryEntity(name = "Спорт", parentId = healthId, colorHex = "#9C27B0", sortOrder = 0, isDefault = true),
            CategoryEntity(name = "Медицина", parentId = healthId, colorHex = "#9C27B0", sortOrder = 1, isDefault = true),
            CategoryEntity(name = "Привычки", parentId = healthId, colorHex = "#9C27B0", sortOrder = 2, isDefault = true),

            // Подкатегории Дома
            CategoryEntity(name = "Ремонт", parentId = homeId, colorHex = "#795548", sortOrder = 0, isDefault = true),
            CategoryEntity(name = "Растения", parentId = homeId, colorHex = "#795548", sortOrder = 1, isDefault = true),
            CategoryEntity(name = "Питомцы", parentId = homeId, colorHex = "#795548", sortOrder = 2, isDefault = true)
        )

        categoryDao.insertAll(subcategories)
    }
}
