package com.example.hometasker.domain.model

import java.time.LocalDateTime

/**
 * Доменная модель категории
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val parentId: Long? = null, // null = главная категория
    val colorHex: String = "#6200EE",
    val iconName: String? = null,
    val sortOrder: Int = 0,
    val isDefault: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
