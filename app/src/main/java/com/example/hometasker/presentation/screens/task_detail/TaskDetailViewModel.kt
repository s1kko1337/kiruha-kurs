package com.example.hometasker.presentation.screens.task_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.model.TrackingStatus
import com.example.hometasker.domain.repository.CategoryRepository
import com.example.hometasker.domain.repository.TimeTrackingRepository
import com.example.hometasker.domain.usecase.task.DeleteTaskUseCase
import com.example.hometasker.domain.usecase.task.GetTaskByIdUseCase
import com.example.hometasker.domain.usecase.task.ToggleTaskCompletionUseCase
import com.example.hometasker.domain.usecase.tracking.PauseTrackingUseCase
import com.example.hometasker.domain.usecase.tracking.ResumeTrackingUseCase
import com.example.hometasker.domain.usecase.tracking.StartTrackingUseCase
import com.example.hometasker.domain.usecase.tracking.StopTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val categoryRepository: CategoryRepository,
    private val timeTrackingRepository: TimeTrackingRepository,
    private val startTrackingUseCase: StartTrackingUseCase,
    private val pauseTrackingUseCase: PauseTrackingUseCase,
    private val resumeTrackingUseCase: ResumeTrackingUseCase,
    private val stopTrackingUseCase: StopTrackingUseCase
) : ViewModel() {

    private val taskId: Long = savedStateHandle["taskId"] ?: -1L

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadTask()
        observeActiveSession()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val task = getTaskByIdUseCase(taskId)
                val category = task?.categoryId?.let { categoryRepository.getCategoryById(it) }

                _uiState.update { it.copy(
                    task = task,
                    category = category,
                    isLoading = false
                )}

                loadTrackingSessions()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message,
                    isLoading = false
                )}
            }
        }
    }

    private fun loadTrackingSessions() {
        viewModelScope.launch {
            timeTrackingRepository.getSessionsByTaskId(taskId).collect { sessions ->
                _uiState.update { it.copy(trackingSessions = sessions) }
            }
        }
    }

    private fun observeActiveSession() {
        viewModelScope.launch {
            timeTrackingRepository.getActiveSession().collect { session ->
                if (session?.taskId == taskId) {
                    _uiState.update { it.copy(activeSession = session) }
                    if (session.status == TrackingStatus.IN_PROGRESS) {
                        startTimer(session.startedAt, session.totalSeconds)
                    } else {
                        stopTimer()
                        _uiState.update { it.copy(elapsedSeconds = session.totalSeconds) }
                    }
                } else {
                    _uiState.update { it.copy(activeSession = null, elapsedSeconds = 0) }
                    stopTimer()
                }
            }
        }
    }

    private fun startTimer(startedAt: LocalDateTime, previousSeconds: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val currentSeconds = ChronoUnit.SECONDS.between(startedAt, LocalDateTime.now())
                _uiState.update { it.copy(elapsedSeconds = previousSeconds + currentSeconds) }
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun onToggleCompletion() {
        val task = _uiState.value.task ?: return
        viewModelScope.launch {
            toggleTaskCompletionUseCase(task.id, !task.isCompleted)
            loadTask()
        }
    }

    fun onDelete(onSuccess: () -> Unit) {
        val task = _uiState.value.task ?: return
        viewModelScope.launch {
            deleteTaskUseCase(task)
            onSuccess()
        }
    }

    fun onStartTracking() {
        viewModelScope.launch {
            startTrackingUseCase(taskId)
        }
    }

    fun onPauseTracking() {
        val sessionId = _uiState.value.activeSession?.id ?: return
        viewModelScope.launch {
            pauseTrackingUseCase(sessionId)
        }
    }

    fun onResumeTracking() {
        val sessionId = _uiState.value.activeSession?.id ?: return
        viewModelScope.launch {
            resumeTrackingUseCase(sessionId)
        }
    }

    fun onStopTracking() {
        val sessionId = _uiState.value.activeSession?.id ?: return
        viewModelScope.launch {
            stopTrackingUseCase(sessionId)
            loadTask()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
