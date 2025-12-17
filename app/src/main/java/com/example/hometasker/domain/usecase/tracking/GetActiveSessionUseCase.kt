package com.example.hometasker.domain.usecase.tracking

import com.example.hometasker.domain.model.TimeTrackingSession
import com.example.hometasker.domain.repository.TimeTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase для получения активной сессии трекинга
 */
class GetActiveSessionUseCase @Inject constructor(
    private val timeTrackingRepository: TimeTrackingRepository
) {
    operator fun invoke(): Flow<TimeTrackingSession?> {
        return timeTrackingRepository.getActiveSession()
    }
}
