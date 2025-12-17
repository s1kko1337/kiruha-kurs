package com.example.hometasker.data.mapper

import com.example.hometasker.data.local.database.entity.TaskEntity
import com.example.hometasker.domain.model.Task
import javax.inject.Inject

/**
 * Маппер для преобразования между Task и TaskEntity
 */
class TaskMapper @Inject constructor() {

    fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            categoryId = entity.categoryId,
            priority = entity.priority,
            isCompleted = entity.isCompleted,
            completedAt = entity.completedAt,
            dueDate = entity.dueDate,
            dueTime = entity.dueTime,
            estimatedMinutes = entity.estimatedMinutes,
            actualMinutes = entity.actualMinutes,
            repeatType = entity.repeatType,
            repeatConfig = entity.repeatConfig,
            repeatEndDate = entity.repeatEndDate,
            repeatCount = entity.repeatCount,
            reminderEnabled = entity.reminderEnabled,
            reminderOffsetMinutes = entity.reminderOffsetMinutes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Task): TaskEntity {
        return TaskEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            categoryId = domain.categoryId,
            priority = domain.priority,
            isCompleted = domain.isCompleted,
            completedAt = domain.completedAt,
            dueDate = domain.dueDate,
            dueTime = domain.dueTime,
            estimatedMinutes = domain.estimatedMinutes,
            actualMinutes = domain.actualMinutes,
            repeatType = domain.repeatType,
            repeatConfig = domain.repeatConfig,
            repeatEndDate = domain.repeatEndDate,
            repeatCount = domain.repeatCount,
            reminderEnabled = domain.reminderEnabled,
            reminderOffsetMinutes = domain.reminderOffsetMinutes,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
