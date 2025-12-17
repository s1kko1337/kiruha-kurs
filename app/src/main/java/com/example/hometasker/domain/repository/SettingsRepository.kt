package com.example.hometasker.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория настроек приложения
 */
interface SettingsRepository {
    // Тема
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)

    // Dynamic Color
    fun isDynamicColorEnabled(): Flow<Boolean>
    suspend fun setDynamicColorEnabled(enabled: Boolean)

    // Уведомления
    fun areNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)

    // Время напоминания по умолчанию (в минутах)
    fun getDefaultReminderOffset(): Flow<Int>
    suspend fun setDefaultReminderOffset(minutes: Int)

    // Сортировка по умолчанию
    fun getDefaultSortOption(): Flow<SortOption>
    suspend fun setDefaultSortOption(option: SortOption)

    // Показывать выполненные задачи
    fun shouldShowCompletedTasks(): Flow<Boolean>
    suspend fun setShowCompletedTasks(show: Boolean)

    // Первый день недели
    fun getFirstDayOfWeek(): Flow<FirstDayOfWeek>
    suspend fun setFirstDayOfWeek(day: FirstDayOfWeek)

    // Формат времени
    fun getTimeFormat(): Flow<TimeFormat>
    suspend fun setTimeFormat(format: TimeFormat)

    // Онбординг показан
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class SortOption {
    DATE, PRIORITY, STATUS, CATEGORY, NAME
}

enum class FirstDayOfWeek {
    MONDAY, SUNDAY
}

enum class TimeFormat {
    HOURS_12, HOURS_24
}
