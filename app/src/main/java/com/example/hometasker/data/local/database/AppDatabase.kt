package com.example.hometasker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hometasker.data.local.database.converter.Converters
import com.example.hometasker.data.local.database.dao.CategoryDao
import com.example.hometasker.data.local.database.dao.TaskDao
import com.example.hometasker.data.local.database.dao.TaskInstanceDao
import com.example.hometasker.data.local.database.dao.TimeTrackingDao
import com.example.hometasker.data.local.database.entity.CategoryEntity
import com.example.hometasker.data.local.database.entity.TaskCategoryCrossRef
import com.example.hometasker.data.local.database.entity.TaskEntity
import com.example.hometasker.data.local.database.entity.TaskInstanceEntity
import com.example.hometasker.data.local.database.entity.TimeTrackingSessionEntity

@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class,
        TaskCategoryCrossRef::class,
        TaskInstanceEntity::class,
        TimeTrackingSessionEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun taskInstanceDao(): TaskInstanceDao
    abstract fun timeTrackingDao(): TimeTrackingDao

    companion object {
        const val DATABASE_NAME = "hometasker_database"
    }
}
