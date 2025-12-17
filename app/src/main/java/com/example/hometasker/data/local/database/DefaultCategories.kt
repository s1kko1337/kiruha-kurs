package com.example.hometasker.data.local.database

import com.example.hometasker.data.local.database.entity.CategoryEntity

/**
 * Предустановленные категории по умолчанию
 */
object DefaultCategories {

    /**
     * Возвращает список предустановленных категорий
     * Включает главные категории и их подкатегории
     */
    fun getDefaultCategories(): List<CategoryEntity> {
        val categories = mutableListOf<CategoryEntity>()
        var sortOrder = 0

        // 🎓 Учёба
        categories.add(
            CategoryEntity(
                id = 1,
                name = "Учёба",
                parentId = null,
                colorHex = "#4CAF50",
                iconName = "school",
                sortOrder = sortOrder++,
                isDefault = true
            )
        )
        categories.addAll(
            listOf(
                CategoryEntity(id = 2, name = "ВУЗ", parentId = 1, colorHex = "#4CAF50", iconName = "account_balance", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 3, name = "Курсы", parentId = 1, colorHex = "#4CAF50", iconName = "menu_book", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 4, name = "Стажировка", parentId = 1, colorHex = "#4CAF50", iconName = "work", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 5, name = "Самообразование", parentId = 1, colorHex = "#4CAF50", iconName = "psychology", sortOrder = sortOrder++, isDefault = true)
            )
        )

        // 🧹 Уборка
        categories.add(
            CategoryEntity(
                id = 6,
                name = "Уборка",
                parentId = null,
                colorHex = "#2196F3",
                iconName = "cleaning_services",
                sortOrder = sortOrder++,
                isDefault = true
            )
        )
        categories.addAll(
            listOf(
                CategoryEntity(id = 7, name = "Ежедневная", parentId = 6, colorHex = "#2196F3", iconName = "today", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 8, name = "Генеральная", parentId = 6, colorHex = "#2196F3", iconName = "home", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 9, name = "Стирка", parentId = 6, colorHex = "#2196F3", iconName = "local_laundry_service", sortOrder = sortOrder++, isDefault = true)
            )
        )

        // 🛒 Покупки
        categories.add(
            CategoryEntity(
                id = 10,
                name = "Покупки",
                parentId = null,
                colorHex = "#FF9800",
                iconName = "shopping_cart",
                sortOrder = sortOrder++,
                isDefault = true
            )
        )
        categories.addAll(
            listOf(
                CategoryEntity(id = 11, name = "Продукты", parentId = 10, colorHex = "#FF9800", iconName = "local_grocery_store", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 12, name = "Бытовое", parentId = 10, colorHex = "#FF9800", iconName = "inventory_2", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 13, name = "Одежда", parentId = 10, colorHex = "#FF9800", iconName = "checkroom", sortOrder = sortOrder++, isDefault = true)
            )
        )

        // 🍳 Готовка
        categories.add(
            CategoryEntity(
                id = 14,
                name = "Готовка",
                parentId = null,
                colorHex = "#E91E63",
                iconName = "restaurant",
                sortOrder = sortOrder++,
                isDefault = true
            )
        )
        categories.addAll(
            listOf(
                CategoryEntity(id = 15, name = "Завтрак", parentId = 14, colorHex = "#E91E63", iconName = "free_breakfast", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 16, name = "Обед", parentId = 14, colorHex = "#E91E63", iconName = "lunch_dining", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 17, name = "Ужин", parentId = 14, colorHex = "#E91E63", iconName = "dinner_dining", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 18, name = "Заготовки", parentId = 14, colorHex = "#E91E63", iconName = "kitchen", sortOrder = sortOrder++, isDefault = true)
            )
        )

        // 💪 Здоровье
        categories.add(
            CategoryEntity(
                id = 19,
                name = "Здоровье",
                parentId = null,
                colorHex = "#9C27B0",
                iconName = "favorite",
                sortOrder = sortOrder++,
                isDefault = true
            )
        )
        categories.addAll(
            listOf(
                CategoryEntity(id = 20, name = "Спорт", parentId = 19, colorHex = "#9C27B0", iconName = "fitness_center", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 21, name = "Медицина", parentId = 19, colorHex = "#9C27B0", iconName = "local_hospital", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 22, name = "Привычки", parentId = 19, colorHex = "#9C27B0", iconName = "self_improvement", sortOrder = sortOrder++, isDefault = true)
            )
        )

        // 🏠 Дом
        categories.add(
            CategoryEntity(
                id = 23,
                name = "Дом",
                parentId = null,
                colorHex = "#795548",
                iconName = "home",
                sortOrder = sortOrder++,
                isDefault = true
            )
        )
        categories.addAll(
            listOf(
                CategoryEntity(id = 24, name = "Ремонт", parentId = 23, colorHex = "#795548", iconName = "build", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 25, name = "Растения", parentId = 23, colorHex = "#795548", iconName = "local_florist", sortOrder = sortOrder++, isDefault = true),
                CategoryEntity(id = 26, name = "Питомцы", parentId = 23, colorHex = "#795548", iconName = "pets", sortOrder = sortOrder++, isDefault = true)
            )
        )

        // 📋 Прочее
        categories.add(
            CategoryEntity(
                id = 27,
                name = "Прочее",
                parentId = null,
                colorHex = "#607D8B",
                iconName = "more_horiz",
                sortOrder = sortOrder++,
                isDefault = true
            )
        )

        return categories
    }
}
