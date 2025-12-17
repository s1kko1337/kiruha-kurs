package com.example.hometasker.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Доменная модель задачи
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val categoryId: Long? = null,
    val priority: Priority = Priority.NONE,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val estimatedMinutes: Int? = null,
    val actualMinutes: Int? = null,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatConfig: String? = null, // JSON конфигурация повторения
    val repeatEndDate: LocalDate? = null,
    val repeatCount: Int? = null,
    val reminderEnabled: Boolean = false,
    val reminderOffsetMinutes: Int? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
