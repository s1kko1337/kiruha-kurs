package com.example.hometasker.presentation.screens.home

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.SortOption
import com.example.hometasker.domain.usecase.task.TaskFilter

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val filter: TaskFilter = TaskFilter.ALL,
    val sortOption: SortOption = SortOption.DATE,
    val showCompletedTasks: Boolean = true
)
