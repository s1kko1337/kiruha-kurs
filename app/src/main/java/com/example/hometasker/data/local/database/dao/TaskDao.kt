package com.example.hometasker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.hometasker.data.local.database.entity.TaskCategoryCrossRef
import com.example.hometasker.data.local.database.entity.TaskEntity
import com.example.hometasker.data.local.database.entity.TaskWithCategories
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY due_date ASC, priority DESC")
    fun getAllTasksWithCategories(): Flow<List<TaskWithCategories>>

    @Query("SELECT * FROM tasks ORDER BY due_date ASC, priority DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE due_date = :date ORDER BY due_time ASC, priority DESC")
    fun getTasksByDateWithCategories(date: LocalDate): Flow<List<TaskWithCategories>>

    @Query("SELECT * FROM tasks WHERE due_date = :date ORDER BY due_time ASC, priority DESC")
    fun getTasksByDate(date: LocalDate): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startDate AND :endDate ORDER BY due_date ASC, due_time ASC")
    fun getTasksByDateRangeWithCategories(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskWithCategories>>

    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startDate AND :endDate ORDER BY due_date ASC, due_time ASC")
    fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskEntity>>

    @Transaction
    @Query("""
        SELECT DISTINCT tasks.* FROM tasks
        INNER JOIN task_category_cross_ref ON tasks.id = task_category_cross_ref.task_id
        WHERE task_category_cross_ref.category_id = :categoryId
        ORDER BY due_date ASC
    """)
    fun getTasksByCategoryWithCategories(categoryId: Long): Flow<List<TaskWithCategories>>

    @Query("""
        SELECT DISTINCT tasks.* FROM tasks
        INNER JOIN task_category_cross_ref ON tasks.id = task_category_cross_ref.task_id
        WHERE task_category_cross_ref.category_id = :categoryId
        ORDER BY due_date ASC
    """)
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY completed_at DESC")
    fun getCompletedTasksWithCategories(): Flow<List<TaskWithCategories>>

    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY completed_at DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY due_date ASC, priority DESC")
    fun getPendingTasksWithCategories(): Flow<List<TaskWithCategories>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY due_date ASC, priority DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE is_completed = 0 AND due_date < :today ORDER BY due_date ASC")
    fun getOverdueTasksWithCategories(today: LocalDate = LocalDate.now()): Flow<List<TaskWithCategories>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 AND due_date < :today ORDER BY due_date ASC")
    fun getOverdueTasks(today: LocalDate = LocalDate.now()): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY due_date ASC")
    fun searchTasksWithCategories(query: String): Flow<List<TaskWithCategories>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY due_date ASC")
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskByIdWithCategories(id: Long): TaskWithCategories?

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET is_completed = :isCompleted, completed_at = :completedAt, updated_at = :updatedAt WHERE id = :taskId")
    suspend fun toggleTaskCompletion(
        taskId: Long,
        isCompleted: Boolean,
        completedAt: LocalDateTime?,
        updatedAt: LocalDateTime = LocalDateTime.now()
    )

    // Category cross-reference methods
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskCategoryCrossRef(crossRef: TaskCategoryCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskCategoryCrossRefs(crossRefs: List<TaskCategoryCrossRef>)

    @Delete
    suspend fun deleteTaskCategoryCrossRef(crossRef: TaskCategoryCrossRef)

    @Query("DELETE FROM task_category_cross_ref WHERE task_id = :taskId")
    suspend fun deleteAllCategoriesForTask(taskId: Long)

    @Query("DELETE FROM task_category_cross_ref WHERE task_id = :taskId AND category_id = :categoryId")
    suspend fun deleteTaskCategoryLink(taskId: Long, categoryId: Long)

    @Query("SELECT category_id FROM task_category_cross_ref WHERE task_id = :taskId")
    suspend fun getCategoryIdsForTask(taskId: Long): List<Long>

    @Query("SELECT category_id FROM task_category_cross_ref WHERE task_id = :taskId")
    fun getCategoryIdsForTaskFlow(taskId: Long): Flow<List<Long>>
}
