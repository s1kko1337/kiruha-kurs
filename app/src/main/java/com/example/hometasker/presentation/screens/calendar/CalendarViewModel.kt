package com.example.hometasker.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTasksForMonth()
        loadTasksForSelectedDate()
    }

    private fun loadTasksForMonth() {
        viewModelScope.launch {
            val currentMonth = _uiState.value.currentMonth
            val startDate = currentMonth.atDay(1)
            val endDate = currentMonth.atEndOfMonth()

            taskRepository.getTasksByDateRange(startDate, endDate)
                .catch { /* ignore */ }
                .collect { tasks ->
                    val tasksPerDay = tasks
                        .filter { it.dueDate != null }
                        .groupBy { it.dueDate!! }
                        .mapValues { it.value.size }

                    _uiState.update { it.copy(tasksPerDay = tasksPerDay) }
                }
        }
    }

    private fun loadTasksForSelectedDate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            taskRepository.getTasksByDate(_uiState.value.selectedDate)
                .catch { e ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = e.message
                    )}
                }
                .collect { tasks ->
                    _uiState.update { it.copy(
                        tasksForSelectedDate = tasks,
                        isLoading = false,
                        error = null
                    )}
                }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadTasksForSelectedDate()
    }

    fun onMonthChanged(month: YearMonth) {
        _uiState.update { it.copy(currentMonth = month) }
        loadTasksForMonth()
    }

    fun onPreviousMonth() {
        val newMonth = _uiState.value.currentMonth.minusMonths(1)
        onMonthChanged(newMonth)
    }

    fun onNextMonth() {
        val newMonth = _uiState.value.currentMonth.plusMonths(1)
        onMonthChanged(newMonth)
    }
}
