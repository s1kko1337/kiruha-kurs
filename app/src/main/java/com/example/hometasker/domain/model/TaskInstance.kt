package com.example.hometasker.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Экземпляр повторяющейся задачи на конкретную дату
 */
data class TaskInstance(
    val id: Long = 0,
    val parentTaskId: Long,
    val scheduledDate: LocalDate,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null,
    val actualMinutes: Int? = null
)
