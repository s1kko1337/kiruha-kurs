package com.example.hometasker.presentation.screens.task_edit

import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.model.Priority
import com.example.hometasker.domain.model.RepeatType
import java.time.LocalDate
import java.time.LocalTime

data class TaskEditUiState(
    val isEditMode: Boolean = false,
    val title: String = "",
    val description: String = "",
    val selectedCategoryIds: List<Long> = emptyList(),
    val priority: Priority = Priority.NONE,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val estimatedMinutes: Int? = null,
    val repeatType: RepeatType = RepeatType.NONE,
    val reminderEnabled: Boolean = false,
    val reminderOffsetMinutes: Int = 30,
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
