package com.example.hometasker.presentation.screens.task_detail

import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.model.TimeTrackingSession

data class TaskDetailUiState(
    val task: Task? = null,
    val categories: List<Category> = emptyList(),
    val trackingSessions: List<TimeTrackingSession> = emptyList(),
    val activeSession: TimeTrackingSession? = null,
    val elapsedSeconds: Long = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)
