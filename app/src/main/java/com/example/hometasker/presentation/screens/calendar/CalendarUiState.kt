package com.example.hometasker.presentation.screens.calendar

import com.example.hometasker.domain.model.Task
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val tasksForSelectedDate: List<Task> = emptyList(),
    val tasksPerDay: Map<LocalDate, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)
