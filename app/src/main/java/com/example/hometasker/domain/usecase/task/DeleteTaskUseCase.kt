package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * UseCase для удаления задачи
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.deleteTask(task)
    }
}
