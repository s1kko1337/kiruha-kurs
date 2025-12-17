package com.example.hometasker.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.hometasker.domain.model.Priority
import com.example.hometasker.domain.model.RepeatType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["due_date"]),
        Index(value = ["is_completed"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val description: String? = null,

    val priority: Priority = Priority.NONE,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime? = null,

    @ColumnInfo(name = "due_date")
    val dueDate: LocalDate? = null,

    @ColumnInfo(name = "due_time")
    val dueTime: LocalTime? = null,

    @ColumnInfo(name = "estimated_minutes")
    val estimatedMinutes: Int? = null,

    @ColumnInfo(name = "actual_minutes")
    val actualMinutes: Int? = null,

    @ColumnInfo(name = "repeat_type")
    val repeatType: RepeatType = RepeatType.NONE,

    @ColumnInfo(name = "repeat_config")
    val repeatConfig: String? = null,

    @ColumnInfo(name = "repeat_end_date")
    val repeatEndDate: LocalDate? = null,

    @ColumnInfo(name = "repeat_count")
    val repeatCount: Int? = null,

    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean = false,

    @ColumnInfo(name = "reminder_offset_minutes")
    val reminderOffsetMinutes: Int? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
