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
    val categoryIds: List<Long> = emptyList(),
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
) {
    /**
     * Время выполнения задачи (alias для dueTime)
     */
    val scheduledTime: LocalTime?
        get() = dueTime

    /**
     * Дата и время напоминания
     * Вычисляется на основе dueDate, dueTime и reminderOffsetMinutes
     */
    val reminderDateTime: LocalDateTime?
        get() {
            if (!reminderEnabled) return null
            val date = dueDate ?: return null
            val time = dueTime ?: LocalTime.of(9, 0) // Default 9:00 if no time set
            val dateTime = LocalDateTime.of(date, time)
            val offset = reminderOffsetMinutes ?: 30 // Default 30 minutes before
            return dateTime.minusMinutes(offset.toLong())
        }

    /**
     * Проверка, просрочена ли задача
     */
    val isOverdue: Boolean
        get() {
            if (isCompleted) return false
            val date = dueDate ?: return false
            val now = LocalDateTime.now()
            val dueDateTime = if (dueTime != null) {
                LocalDateTime.of(date, dueTime)
            } else {
                LocalDateTime.of(date, LocalTime.MAX)
            }
            return now.isAfter(dueDateTime)
        }

    /**
     * Проверка, запланирована ли задача на сегодня
     */
    val isToday: Boolean
        get() = dueDate == LocalDate.now()

    /**
     * Проверка, является ли задача повторяющейся
     */
    val isRepeating: Boolean
        get() = repeatType != RepeatType.NONE
}
