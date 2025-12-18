package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.repository.TaskRepository
import com.example.hometasker.service.NotificationService
import javax.inject.Inject

/**
 * UseCase для переключения статуса выполнения задачи
 */
class ToggleTaskCompletionUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val notificationService: NotificationService
) {
    suspend operator fun invoke(taskId: Long, isCompleted: Boolean) {
        taskRepository.toggleTaskCompletion(taskId, isCompleted)

        // Отменяем уведомление при выполнении задачи
        if (isCompleted) {
            notificationService.cancelTaskReminder(taskId)
        } else {
            // Если задача снова открыта, перепланируем уведомление
            val task = taskRepository.getTaskById(taskId)
            if (task != null && task.reminderEnabled && task.reminderDateTime != null) {
                notificationService.scheduleTaskReminder(task)
            }
        }
    }
}
