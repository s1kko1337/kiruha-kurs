package com.example.hometasker.data.mapper

import com.example.hometasker.data.local.database.entity.CategoryEntity
import com.example.hometasker.domain.model.Category
import javax.inject.Inject

/**
 * Маппер для преобразования между Category и CategoryEntity
 */
class CategoryMapper @Inject constructor() {

    fun toDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            colorHex = entity.colorHex,
            iconName = entity.iconName,
            sortOrder = entity.sortOrder,
            isDefault = entity.isDefault,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: Category): CategoryEntity {
        return CategoryEntity(
            id = domain.id,
            name = domain.name,
            parentId = domain.parentId,
            colorHex = domain.colorHex,
            iconName = domain.iconName,
            sortOrder = domain.sortOrder,
            isDefault = domain.isDefault,
            createdAt = domain.createdAt
        )
    }
}
