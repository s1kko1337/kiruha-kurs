package com.example.hometasker.data.repository

import com.example.hometasker.data.local.database.dao.TaskDao
import com.example.hometasker.data.mapper.TaskMapper
import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val mapper: TaskMapper
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksByDate(date).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksByDateRange(startDate, endDate).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getTasksByCategory(categoryId: Long): Flow<List<Task>> {
        return taskDao.getTasksByCategory(categoryId).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getPendingTasks(): Flow<List<Task>> {
        return taskDao.getPendingTasks().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getOverdueTasks(): Flow<List<Task>> {
        return taskDao.getOverdueTasks().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insert(mapper.toEntity(task))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(mapper.toEntity(task))
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(mapper.toEntity(task))
    }

    override suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) LocalDateTime.now() else null
        taskDao.toggleTaskCompletion(taskId, isCompleted, completedAt)
    }
}
