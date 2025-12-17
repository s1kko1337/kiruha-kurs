package com.example.hometasker.data.repository

import com.example.hometasker.data.local.datastore.SettingsDataStore
import com.example.hometasker.domain.repository.FirstDayOfWeek
import com.example.hometasker.domain.repository.SettingsRepository
import com.example.hometasker.domain.repository.SortOption
import com.example.hometasker.domain.repository.ThemeMode
import com.example.hometasker.domain.repository.TimeFormat
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override fun getThemeMode(): Flow<ThemeMode> {
        return settingsDataStore.getThemeMode()
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        settingsDataStore.setThemeMode(mode)
    }

    override fun isDynamicColorEnabled(): Flow<Boolean> {
        return settingsDataStore.isDynamicColorEnabled()
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        settingsDataStore.setDynamicColorEnabled(enabled)
    }

    override fun areNotificationsEnabled(): Flow<Boolean> {
        return settingsDataStore.areNotificationsEnabled()
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        settingsDataStore.setNotificationsEnabled(enabled)
    }

    override fun getDefaultReminderOffset(): Flow<Int> {
        return settingsDataStore.getDefaultReminderOffset()
    }

    override suspend fun setDefaultReminderOffset(minutes: Int) {
        settingsDataStore.setDefaultReminderOffset(minutes)
    }

    override fun getDefaultSortOption(): Flow<SortOption> {
        return settingsDataStore.getDefaultSortOption()
    }

    override suspend fun setDefaultSortOption(option: SortOption) {
        settingsDataStore.setDefaultSortOption(option)
    }

    override fun shouldShowCompletedTasks(): Flow<Boolean> {
        return settingsDataStore.shouldShowCompletedTasks()
    }

    override suspend fun setShowCompletedTasks(show: Boolean) {
        settingsDataStore.setShowCompletedTasks(show)
    }

    override fun getFirstDayOfWeek(): Flow<FirstDayOfWeek> {
        return settingsDataStore.getFirstDayOfWeek()
    }

    override suspend fun setFirstDayOfWeek(day: FirstDayOfWeek) {
        settingsDataStore.setFirstDayOfWeek(day)
    }

    override fun getTimeFormat(): Flow<TimeFormat> {
        return settingsDataStore.getTimeFormat()
    }

    override suspend fun setTimeFormat(format: TimeFormat) {
        settingsDataStore.setTimeFormat(format)
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return settingsDataStore.isOnboardingCompleted()
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        settingsDataStore.setOnboardingCompleted(completed)
    }
}
