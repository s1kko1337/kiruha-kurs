package com.example.hometasker.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.hometasker.domain.model.TrackingStatus
import java.time.LocalDateTime

@Entity(
    tableName = "time_tracking_sessions",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["task_id"]),
        Index(value = ["status"])
    ]
)
data class TimeTrackingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "task_id")
    val taskId: Long,

    @ColumnInfo(name = "started_at")
    val startedAt: LocalDateTime,

    @ColumnInfo(name = "ended_at")
    val endedAt: LocalDateTime? = null,

    @ColumnInfo(name = "paused_at")
    val pausedAt: LocalDateTime? = null,

    @ColumnInfo(name = "total_seconds")
    val totalSeconds: Long = 0,

    val status: TrackingStatus = TrackingStatus.IN_PROGRESS
)
