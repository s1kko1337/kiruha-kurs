package com.example.hometasker.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Связующая таблица для связи многие-ко-многим между задачами и категориями
 */
@Entity(
    tableName = "task_category_cross_ref",
    primaryKeys = ["task_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["task_id"]),
        Index(value = ["category_id"])
    ]
)
data class TaskCategoryCrossRef(
    @ColumnInfo(name = "task_id")
    val taskId: Long,

    @ColumnInfo(name = "category_id")
    val categoryId: Long
)
