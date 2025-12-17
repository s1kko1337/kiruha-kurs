package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * UseCase для обновления задачи
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.updateTask(task.copy(updatedAt = LocalDateTime.now()))
    }
}
