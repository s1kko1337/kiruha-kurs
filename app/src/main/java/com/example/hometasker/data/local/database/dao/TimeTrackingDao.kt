package com.example.hometasker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hometasker.data.local.database.entity.TimeTrackingSessionEntity
import com.example.hometasker.domain.model.TrackingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeTrackingDao {

    @Query("SELECT * FROM time_tracking_sessions WHERE task_id = :taskId ORDER BY started_at DESC")
    fun getSessionsByTaskId(taskId: Long): Flow<List<TimeTrackingSessionEntity>>

    @Query("SELECT * FROM time_tracking_sessions WHERE status IN ('IN_PROGRESS', 'PAUSED') LIMIT 1")
    fun getActiveSession(): Flow<TimeTrackingSessionEntity?>

    @Query("SELECT * FROM time_tracking_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): TimeTrackingSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: TimeTrackingSessionEntity): Long

    @Update
    suspend fun update(session: TimeTrackingSessionEntity)

    @Delete
    suspend fun delete(session: TimeTrackingSessionEntity)
}
