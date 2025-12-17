package com.example.hometasker.domain.usecase.tracking

import com.example.hometasker.domain.model.TrackingStatus
import com.example.hometasker.domain.repository.TaskRepository
import com.example.hometasker.domain.repository.TimeTrackingRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * UseCase для остановки трекинга времени
 */
class StopTrackingUseCase @Inject constructor(
    private val timeTrackingRepository: TimeTrackingRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(sessionId: Long) {
        val session = timeTrackingRepository.getSessionById(sessionId) ?: return
        if (session.status == TrackingStatus.COMPLETED) return

        val now = LocalDateTime.now()
        val additionalSeconds = if (session.status == TrackingStatus.IN_PROGRESS) {
            ChronoUnit.SECONDS.between(session.startedAt, now)
        } else {
            0L
        }

        val totalSeconds = session.totalSeconds + additionalSeconds

        timeTrackingRepository.updateSession(
            session.copy(
                endedAt = now,
                pausedAt = null,
                totalSeconds = totalSeconds,
                status = TrackingStatus.COMPLETED
            )
        )

        // Обновляем фактическое время выполнения задачи
        val task = taskRepository.getTaskById(session.taskId)
        if (task != null) {
            val totalMinutes = (totalSeconds / 60).toInt()
            taskRepository.updateTask(
                task.copy(actualMinutes = (task.actualMinutes ?: 0) + totalMinutes)
            )
        }
    }
}
