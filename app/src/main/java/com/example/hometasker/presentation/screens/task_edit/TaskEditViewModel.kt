package com.example.hometasker.presentation.screens.task_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.model.Priority
import com.example.hometasker.domain.model.RepeatType
import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.usecase.category.GetCategoriesUseCase
import com.example.hometasker.domain.usecase.task.CreateTaskUseCase
import com.example.hometasker.domain.usecase.task.GetTaskByIdUseCase
import com.example.hometasker.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val taskId: Long? = savedStateHandle.get<Long>("taskId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(TaskEditUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val task = getTaskByIdUseCase(taskId)
                if (task != null) {
                    _uiState.update { it.copy(
                        isEditMode = true,
                        title = task.title,
                        description = task.description ?: "",
                        selectedCategoryIds = task.categoryIds,
                        priority = task.priority,
                        dueDate = task.dueDate,
                        dueTime = task.dueTime,
                        estimatedMinutes = task.estimatedMinutes,
                        repeatType = task.repeatType,
                        reminderEnabled = task.reminderEnabled,
                        reminderOffsetMinutes = task.reminderOffsetMinutes ?: 30,
                        isLoading = false
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onCategoryToggle(categoryId: Long) {
        _uiState.update { state ->
            val currentIds = state.selectedCategoryIds.toMutableList()
            if (currentIds.contains(categoryId)) {
                currentIds.remove(categoryId)
            } else {
                currentIds.add(categoryId)
            }
            state.copy(selectedCategoryIds = currentIds)
        }
    }

    fun onCategoriesChange(categoryIds: List<Long>) {
        _uiState.update { it.copy(selectedCategoryIds = categoryIds) }
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onDueDateChange(date: LocalDate?) {
        _uiState.update { it.copy(dueDate = date) }
    }

    fun onDueTimeChange(time: LocalTime?) {
        _uiState.update { it.copy(dueTime = time) }
    }

    fun onEstimatedMinutesChange(minutes: Int?) {
        _uiState.update { it.copy(estimatedMinutes = minutes) }
    }

    fun onRepeatTypeChange(repeatType: RepeatType) {
        _uiState.update { it.copy(repeatType = repeatType) }
    }

    fun onReminderEnabledChange(enabled: Boolean) {
        _uiState.update { it.copy(reminderEnabled = enabled) }
    }

    fun onReminderOffsetChange(minutes: Int) {
        _uiState.update { it.copy(reminderOffsetMinutes = minutes) }
    }

    fun save() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Введите название задачи") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val task = Task(
                    id = taskId ?: 0,
                    title = state.title.trim(),
                    description = state.description.trim().takeIf { it.isNotEmpty() },
                    categoryIds = state.selectedCategoryIds,
                    priority = state.priority,
                    dueDate = state.dueDate,
                    dueTime = state.dueTime,
                    estimatedMinutes = state.estimatedMinutes,
                    repeatType = state.repeatType,
                    reminderEnabled = state.reminderEnabled,
                    reminderOffsetMinutes = if (state.reminderEnabled) state.reminderOffsetMinutes else null
                )

                if (state.isEditMode) {
                    updateTaskUseCase(task)
                } else {
                    createTaskUseCase(task)
                }

                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isSaving = false) }
            }
        }
    }
}
