package com.example.hometasker.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.SortOption
import com.example.hometasker.domain.usecase.task.CreateTaskUseCase
import com.example.hometasker.domain.usecase.task.DeleteTaskUseCase
import com.example.hometasker.domain.usecase.task.GetTasksUseCase
import com.example.hometasker.domain.usecase.task.TaskFilter
import com.example.hometasker.domain.usecase.task.ToggleTaskCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    // Хранение удалённой задачи для возможности отмены
    private var lastDeletedTask: Task? = null

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getTasksUseCase(
                filter = _uiState.value.filter,
                sortOption = _uiState.value.sortOption
            )
                .catch { e ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки задач"
                    )}
                }
                .collect { tasks ->
                    val filteredTasks = if (_uiState.value.showCompletedTasks) {
                        tasks
                    } else {
                        tasks.filter { !it.isCompleted }
                    }
                    _uiState.update { it.copy(
                        tasks = filteredTasks,
                        isLoading = false,
                        error = null
                    )}
                }
        }
    }

    fun onFilterChange(filter: TaskFilter) {
        _uiState.update { it.copy(filter = filter) }
        loadTasks()
    }

    fun onSortOptionChange(sortOption: SortOption) {
        _uiState.update { it.copy(sortOption = sortOption) }
        loadTasks()
    }

    fun onToggleShowCompletedTasks() {
        _uiState.update { it.copy(showCompletedTasks = !it.showCompletedTasks) }
        loadTasks()
    }

    fun onTaskCheckedChange(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            toggleTaskCompletionUseCase(taskId, isCompleted)
        }
    }

    fun onDeleteTask(taskId: Long) {
        viewModelScope.launch {
            val task = _uiState.value.tasks.find { it.id == taskId } ?: return@launch
            lastDeletedTask = task
            deleteTaskUseCase(task)
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            lastDeletedTask?.let { task ->
                // Создаём новую задачу с теми же данными (но новым id)
                createTaskUseCase(task.copy(id = 0))
                lastDeletedTask = null
            }
        }
    }

    fun refresh() {
        loadTasks()
    }
}
