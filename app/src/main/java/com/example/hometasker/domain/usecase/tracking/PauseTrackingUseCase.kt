package com.example.hometasker.domain.usecase.tracking

import com.example.hometasker.domain.model.TrackingStatus
import com.example.hometasker.domain.repository.TimeTrackingRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * UseCase для паузы трекинга времени
 */
class PauseTrackingUseCase @Inject constructor(
    private val timeTrackingRepository: TimeTrackingRepository
) {
    suspend operator fun invoke(sessionId: Long) {
        val session = timeTrackingRepository.getSessionById(sessionId) ?: return
        if (session.status != TrackingStatus.IN_PROGRESS) return

        val now = LocalDateTime.now()
        val additionalSeconds = ChronoUnit.SECONDS.between(session.startedAt, now)

        timeTrackingRepository.updateSession(
            session.copy(
                pausedAt = now,
                totalSeconds = session.totalSeconds + additionalSeconds,
                status = TrackingStatus.PAUSED
            )
        )
    }
}
