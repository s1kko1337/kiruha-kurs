package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * UseCase для получения задачи по ID
 */
class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long): Task? {
        return taskRepository.getTaskById(taskId)
    }
}
