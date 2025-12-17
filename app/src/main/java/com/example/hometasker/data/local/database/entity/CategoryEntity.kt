package com.example.hometasker.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parent_id"])
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#6200EE",

    @ColumnInfo(name = "icon_name")
    val iconName: String? = null,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
