package com.example.hometasker.data.mapper

import com.example.hometasker.data.local.database.entity.TaskInstanceEntity
import com.example.hometasker.domain.model.TaskInstance
import javax.inject.Inject

/**
 * Маппер для преобразования между TaskInstance и TaskInstanceEntity
 */
class TaskInstanceMapper @Inject constructor() {

    fun toDomain(entity: TaskInstanceEntity): TaskInstance {
        return TaskInstance(
            id = entity.id,
            parentTaskId = entity.parentTaskId,
            scheduledDate = entity.scheduledDate,
            isCompleted = entity.isCompleted,
            completedAt = entity.completedAt,
            actualMinutes = entity.actualMinutes
        )
    }

    fun toEntity(domain: TaskInstance): TaskInstanceEntity {
        return TaskInstanceEntity(
            id = domain.id,
            parentTaskId = domain.parentTaskId,
            scheduledDate = domain.scheduledDate,
            isCompleted = domain.isCompleted,
            completedAt = domain.completedAt,
            actualMinutes = domain.actualMinutes
        )
    }
}
