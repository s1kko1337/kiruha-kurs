package com.example.hometasker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hometasker.data.local.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY due_date ASC, priority DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE due_date = :date ORDER BY due_time ASC, priority DESC")
    fun getTasksByDate(date: LocalDate): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startDate AND :endDate ORDER BY due_date ASC, due_time ASC")
    fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category_id = :categoryId ORDER BY due_date ASC")
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY completed_at DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY due_date ASC, priority DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 AND due_date < :today ORDER BY due_date ASC")
    fun getOverdueTasks(today: LocalDate = LocalDate.now()): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY due_date ASC")
    fun searchTasks(query: String): Flow<List<TaskEntity>>

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
}
