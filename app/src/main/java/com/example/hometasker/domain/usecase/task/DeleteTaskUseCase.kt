package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import com.example.hometasker.service.NotificationService
import javax.inject.Inject

/**
 * UseCase для удаления задачи
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val notificationService: NotificationService
) {
    suspend operator fun invoke(task: Task) {
        // Отменяем уведомление перед удалением задачи
        notificationService.cancelTaskReminder(task.id)
        taskRepository.deleteTask(task)
    }
}
