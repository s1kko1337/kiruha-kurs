package com.example.hometasker.data.repository

import com.example.hometasker.data.local.database.dao.TaskDao
import com.example.hometasker.data.local.database.entity.TaskCategoryCrossRef
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
        return taskDao.getAllTasksWithCategories().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksByDateWithCategories(date).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksByDateRangeWithCategories(startDate, endDate).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getTasksByCategory(categoryId: Long): Flow<List<Task>> {
        return taskDao.getTasksByCategoryWithCategories(categoryId).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasksWithCategories().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getPendingTasks(): Flow<List<Task>> {
        return taskDao.getPendingTasksWithCategories().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun getOverdueTasks(): Flow<List<Task>> {
        return taskDao.getOverdueTasksWithCategories().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasksWithCategories(query).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskByIdWithCategories(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun insertTask(task: Task): Long {
        val taskId = taskDao.insert(mapper.toEntity(task))
        // Insert category cross-references
        if (task.categoryIds.isNotEmpty()) {
            val crossRefs = task.categoryIds.map { categoryId ->
                TaskCategoryCrossRef(taskId = taskId, categoryId = categoryId)
            }
            taskDao.insertTaskCategoryCrossRefs(crossRefs)
        }
        return taskId
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(mapper.toEntity(task))
        // Update category cross-references
        updateTaskCategories(task.id, task.categoryIds)
    }

    override suspend fun deleteTask(task: Task) {
        // Cross-references will be deleted automatically due to CASCADE
        taskDao.delete(mapper.toEntity(task))
    }

    override suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) LocalDateTime.now() else null
        taskDao.toggleTaskCompletion(taskId, isCompleted, completedAt)
    }

    override suspend fun updateTaskCategories(taskId: Long, categoryIds: List<Long>) {
        // Delete all existing category links for this task
        taskDao.deleteAllCategoriesForTask(taskId)
        // Insert new category links
        if (categoryIds.isNotEmpty()) {
            val crossRefs = categoryIds.map { categoryId ->
                TaskCategoryCrossRef(taskId = taskId, categoryId = categoryId)
            }
            taskDao.insertTaskCategoryCrossRefs(crossRefs)
        }
    }

    override suspend fun addCategoryToTask(taskId: Long, categoryId: Long) {
        taskDao.insertTaskCategoryCrossRef(TaskCategoryCrossRef(taskId = taskId, categoryId = categoryId))
    }

    override suspend fun removeCategoryFromTask(taskId: Long, categoryId: Long) {
        taskDao.deleteTaskCategoryLink(taskId, categoryId)
    }
}
