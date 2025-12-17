package com.example.hometasker.domain.usecase.tracking

import com.example.hometasker.domain.model.TimeTrackingSession
import com.example.hometasker.domain.model.TrackingStatus
import com.example.hometasker.domain.repository.TimeTrackingRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * UseCase для начала трекинга времени
 */
class StartTrackingUseCase @Inject constructor(
    private val timeTrackingRepository: TimeTrackingRepository
) {
    suspend operator fun invoke(taskId: Long): Long {
        val session = TimeTrackingSession(
            taskId = taskId,
            startedAt = LocalDateTime.now(),
            status = TrackingStatus.IN_PROGRESS
        )
        return timeTrackingRepository.insertSession(session)
    }
}
