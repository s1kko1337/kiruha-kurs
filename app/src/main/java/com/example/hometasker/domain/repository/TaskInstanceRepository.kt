package com.example.hometasker.domain.repository

import com.example.hometasker.domain.model.TaskInstance
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Интерфейс репозитория экземпляров повторяющихся задач
 */
interface TaskInstanceRepository {
    fun getInstancesByTaskId(taskId: Long): Flow<List<TaskInstance>>
    fun getInstancesByDate(date: LocalDate): Flow<List<TaskInstance>>
    fun getInstancesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskInstance>>
    suspend fun getInstanceById(id: Long): TaskInstance?
    suspend fun insertInstance(instance: TaskInstance): Long
    suspend fun updateInstance(instance: TaskInstance)
    suspend fun deleteInstance(instance: TaskInstance)
    suspend fun deleteInstancesByTaskId(taskId: Long)
}
