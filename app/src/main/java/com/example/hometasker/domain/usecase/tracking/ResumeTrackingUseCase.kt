package com.example.hometasker.domain.usecase.tracking

import com.example.hometasker.domain.model.TrackingStatus
import com.example.hometasker.domain.repository.TimeTrackingRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * UseCase для возобновления трекинга времени
 */
class ResumeTrackingUseCase @Inject constructor(
    private val timeTrackingRepository: TimeTrackingRepository
) {
    suspend operator fun invoke(sessionId: Long) {
        val session = timeTrackingRepository.getSessionById(sessionId) ?: return
        if (session.status != TrackingStatus.PAUSED) return

        timeTrackingRepository.updateSession(
            session.copy(
                startedAt = LocalDateTime.now(),
                pausedAt = null,
                status = TrackingStatus.IN_PROGRESS
            )
        )
    }
}
