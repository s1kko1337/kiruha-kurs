package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import com.example.hometasker.service.NotificationService
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * UseCase для обновления задачи
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val notificationService: NotificationService
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.updateTask(task.copy(updatedAt = LocalDateTime.now()))

        // Обновляем уведомление
        if (task.reminderEnabled && task.reminderDateTime != null) {
            notificationService.scheduleTaskReminder(task)
        } else {
            // Отменяем уведомление, если напоминание отключено
            notificationService.cancelTaskReminder(task.id)
        }
    }
}
