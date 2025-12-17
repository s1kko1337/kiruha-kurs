package com.example.hometasker.domain.model

import java.time.LocalDateTime

/**
 * Сессия трекинга времени выполнения задачи
 */
data class TimeTrackingSession(
    val id: Long = 0,
    val taskId: Long,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime? = null,
    val pausedAt: LocalDateTime? = null,
    val totalSeconds: Long = 0,
    val status: TrackingStatus = TrackingStatus.IN_PROGRESS
)
