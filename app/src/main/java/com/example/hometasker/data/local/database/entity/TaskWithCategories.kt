package com.example.hometasker.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Data class для получения задачи с её категориями
 */
data class TaskWithCategories(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskCategoryCrossRef::class,
            parentColumn = "task_id",
            entityColumn = "category_id"
        )
    )
    val categories: List<CategoryEntity>
)
