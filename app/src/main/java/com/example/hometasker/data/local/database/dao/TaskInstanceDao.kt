package com.example.hometasker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hometasker.data.local.database.entity.TaskInstanceEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskInstanceDao {

    @Query("SELECT * FROM task_instances WHERE parent_task_id = :taskId ORDER BY scheduled_date ASC")
    fun getInstancesByTaskId(taskId: Long): Flow<List<TaskInstanceEntity>>

    @Query("SELECT * FROM task_instances WHERE scheduled_date = :date ORDER BY scheduled_date ASC")
    fun getInstancesByDate(date: LocalDate): Flow<List<TaskInstanceEntity>>

    @Query("SELECT * FROM task_instances WHERE scheduled_date BETWEEN :startDate AND :endDate ORDER BY scheduled_date ASC")
    fun getInstancesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskInstanceEntity>>

    @Query("SELECT * FROM task_instances WHERE id = :id")
    suspend fun getInstanceById(id: Long): TaskInstanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(instance: TaskInstanceEntity): Long

    @Update
    suspend fun update(instance: TaskInstanceEntity)

    @Delete
    suspend fun delete(instance: TaskInstanceEntity)

    @Query("DELETE FROM task_instances WHERE parent_task_id = :taskId")
    suspend fun deleteInstancesByTaskId(taskId: Long)
}
