package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * UseCase для переключения статуса выполнения задачи
 */
class ToggleTaskCompletionUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long, isCompleted: Boolean) {
        taskRepository.toggleTaskCompletion(taskId, isCompleted)
    }
}
