package com.example.hometasker.domain.model

/**
 * Типы повторения задачи
 */
enum class RepeatType {
    NONE,           // Без повторения
    DAILY,          // Каждый день
    WEEKLY,         // Каждую неделю
    MONTHLY,        // Каждый месяц
    CUSTOM_DAYS,    // По дням недели (пн, ср, пт)
    EVERY_N_DAYS,   // Каждые N дней
    BIWEEKLY_ODD,   // По нечётным неделям
    BIWEEKLY_EVEN   // По чётным неделям
}
