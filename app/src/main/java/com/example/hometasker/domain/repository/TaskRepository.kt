package com.example.hometasker.domain.repository

import com.example.hometasker.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Интерфейс репозитория задач
 */
interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByDate(date: LocalDate): Flow<List<Task>>
    fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>
    fun getTasksByCategory(categoryId: Long): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getPendingTasks(): Flow<List<Task>>
    fun getOverdueTasks(): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean)
}
