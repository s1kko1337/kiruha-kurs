package com.example.hometasker.data.mapper

import com.example.hometasker.data.local.database.entity.TimeTrackingSessionEntity
import com.example.hometasker.domain.model.TimeTrackingSession
import javax.inject.Inject

/**
 * Маппер для преобразования между TimeTrackingSession и TimeTrackingSessionEntity
 */
class TimeTrackingSessionMapper @Inject constructor() {

    fun toDomain(entity: TimeTrackingSessionEntity): TimeTrackingSession {
        return TimeTrackingSession(
            id = entity.id,
            taskId = entity.taskId,
            startedAt = entity.startedAt,
            endedAt = entity.endedAt,
            pausedAt = entity.pausedAt,
            totalSeconds = entity.totalSeconds,
            status = entity.status
        )
    }

    fun toEntity(domain: TimeTrackingSession): TimeTrackingSessionEntity {
        return TimeTrackingSessionEntity(
            id = domain.id,
            taskId = domain.taskId,
            startedAt = domain.startedAt,
            endedAt = domain.endedAt,
            pausedAt = domain.pausedAt,
            totalSeconds = domain.totalSeconds,
            status = domain.status
        )
    }
}
