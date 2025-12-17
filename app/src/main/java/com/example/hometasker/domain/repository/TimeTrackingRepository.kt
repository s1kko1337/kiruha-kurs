package com.example.hometasker.domain.repository

import com.example.hometasker.domain.model.TimeTrackingSession
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория трекинга времени
 */
interface TimeTrackingRepository {
    fun getSessionsByTaskId(taskId: Long): Flow<List<TimeTrackingSession>>
    fun getActiveSession(): Flow<TimeTrackingSession?>
    suspend fun getSessionById(id: Long): TimeTrackingSession?
    suspend fun insertSession(session: TimeTrackingSession): Long
    suspend fun updateSession(session: TimeTrackingSession)
    suspend fun deleteSession(session: TimeTrackingSession)
}
