package com.example.hometasker.data.repository

import com.example.hometasker.data.local.database.dao.TaskInstanceDao
import com.example.hometasker.data.mapper.TaskInstanceMapper
import com.example.hometasker.domain.model.TaskInstance
import com.example.hometasker.domain.repository.TaskInstanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TaskInstanceRepositoryImpl @Inject constructor(
    private val taskInstanceDao: TaskInstanceDao,
    private val mapper: TaskInstanceMapper
) : TaskInstanceRepository {

    override fun getInstancesByTaskId(taskId: Long): Flow<List<TaskInstance>> {
        return taskInstanceDao.getInstancesByTaskId(taskId).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getInstancesByDate(date: LocalDate): Flow<List<TaskInstance>> {
        return taskInstanceDao.getInstancesByDate(date).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getInstancesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskInstance>> {
        return taskInstanceDao.getInstancesByDateRange(startDate, endDate).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun getInstanceById(id: Long): TaskInstance? {
        return taskInstanceDao.getInstanceById(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun insertInstance(instance: TaskInstance): Long {
        return taskInstanceDao.insert(mapper.toEntity(instance))
    }

    override suspend fun updateInstance(instance: TaskInstance) {
        taskInstanceDao.update(mapper.toEntity(instance))
    }

    override suspend fun deleteInstance(instance: TaskInstance) {
        taskInstanceDao.delete(mapper.toEntity(instance))
    }

    override suspend fun deleteInstancesByTaskId(taskId: Long) {
        taskInstanceDao.deleteInstancesByTaskId(taskId)
    }
}
