package com.example.hometasker.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "task_instances",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parent_task_id"]),
        Index(value = ["scheduled_date"])
    ]
)
data class TaskInstanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "parent_task_id")
    val parentTaskId: Long,

    @ColumnInfo(name = "scheduled_date")
    val scheduledDate: LocalDate,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime? = null,

    @ColumnInfo(name = "actual_minutes")
    val actualMinutes: Int? = null
)
