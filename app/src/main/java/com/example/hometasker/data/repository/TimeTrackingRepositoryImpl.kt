package com.example.hometasker.data.repository

import com.example.hometasker.data.local.database.dao.TimeTrackingDao
import com.example.hometasker.data.mapper.TimeTrackingSessionMapper
import com.example.hometasker.domain.model.TimeTrackingSession
import com.example.hometasker.domain.repository.TimeTrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TimeTrackingRepositoryImpl @Inject constructor(
    private val timeTrackingDao: TimeTrackingDao,
    private val mapper: TimeTrackingSessionMapper
) : TimeTrackingRepository {

    override fun getSessionsByTaskId(taskId: Long): Flow<List<TimeTrackingSession>> {
        return timeTrackingDao.getSessionsByTaskId(taskId).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getActiveSession(): Flow<TimeTrackingSession?> {
        return timeTrackingDao.getActiveSession().map { entity ->
            entity?.let { mapper.toDomain(it) }
        }
    }

    override suspend fun getSessionById(id: Long): TimeTrackingSession? {
        return timeTrackingDao.getSessionById(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun insertSession(session: TimeTrackingSession): Long {
        return timeTrackingDao.insert(mapper.toEntity(session))
    }

    override suspend fun updateSession(session: TimeTrackingSession) {
        timeTrackingDao.update(mapper.toEntity(session))
    }

    override suspend fun deleteSession(session: TimeTrackingSession) {
        timeTrackingDao.delete(mapper.toEntity(session))
    }
}
