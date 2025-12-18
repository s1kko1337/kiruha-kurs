package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import com.example.hometasker.service.NotificationService
import javax.inject.Inject

/**
 * UseCase для создания новой задачи
 */
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val notificationService: NotificationService
) {
    suspend operator fun invoke(task: Task): Long {
        val taskId = taskRepository.insertTask(task)

        // Планируем уведомление, если напоминание включено
        if (task.reminderEnabled && task.reminderDateTime != null) {
            val taskWithId = task.copy(id = taskId)
            notificationService.scheduleTaskReminder(taskWithId)
        }

        return taskId
    }
}
