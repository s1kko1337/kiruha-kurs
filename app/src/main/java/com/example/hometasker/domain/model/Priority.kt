package com.example.hometasker.domain.model

/**
 * Уровни приоритета задачи
 */
enum class Priority {
    NONE,   // Без приоритета (серый)
    LOW,    // Низкий (синий)
    MEDIUM, // Средний (жёлтый/оранжевый)
    HIGH    // Высокий (красный)
}
