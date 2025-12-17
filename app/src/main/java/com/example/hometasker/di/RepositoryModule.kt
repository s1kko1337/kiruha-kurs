package com.example.hometasker.di

import com.example.hometasker.data.repository.CategoryRepositoryImpl
import com.example.hometasker.data.repository.SettingsRepositoryImpl
import com.example.hometasker.data.repository.TaskInstanceRepositoryImpl
import com.example.hometasker.data.repository.TaskRepositoryImpl
import com.example.hometasker.data.repository.TimeTrackingRepositoryImpl
import com.example.hometasker.domain.repository.CategoryRepository
import com.example.hometasker.domain.repository.SettingsRepository
import com.example.hometasker.domain.repository.TaskInstanceRepository
import com.example.hometasker.domain.repository.TaskRepository
import com.example.hometasker.domain.repository.TimeTrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTaskInstanceRepository(
        taskInstanceRepositoryImpl: TaskInstanceRepositoryImpl
    ): TaskInstanceRepository

    @Binds
    @Singleton
    abstract fun bindTimeTrackingRepository(
        timeTrackingRepositoryImpl: TimeTrackingRepositoryImpl
    ): TimeTrackingRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}
