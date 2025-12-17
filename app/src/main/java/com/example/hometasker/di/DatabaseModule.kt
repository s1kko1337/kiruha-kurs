package com.example.hometasker.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hometasker.data.local.database.AppDatabase
import com.example.hometasker.data.local.database.DefaultCategories
import com.example.hometasker.data.local.database.dao.CategoryDao
import com.example.hometasker.data.local.database.dao.TaskDao
import com.example.hometasker.data.local.database.dao.TaskInstanceDao
import com.example.hometasker.data.local.database.dao.TimeTrackingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        categoryDaoProvider: Provider<CategoryDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Prepopulate with default categories
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        val categoryDao = categoryDaoProvider.get()
                        DefaultCategories.getDefaultCategories().forEach { category ->
                            categoryDao.insert(category)
                        }
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideTaskInstanceDao(database: AppDatabase): TaskInstanceDao {
        return database.taskInstanceDao()
    }

    @Provides
    fun provideTimeTrackingDao(database: AppDatabase): TimeTrackingDao {
        return database.timeTrackingDao()
    }
}
