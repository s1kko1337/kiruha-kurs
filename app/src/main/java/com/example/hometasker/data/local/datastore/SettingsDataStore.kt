package com.example.hometasker.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hometasker.domain.repository.FirstDayOfWeek
import com.example.hometasker.domain.repository.SortOption
import com.example.hometasker.domain.repository.ThemeMode
import com.example.hometasker.domain.repository.TimeFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DEFAULT_REMINDER_OFFSET = intPreferencesKey("default_reminder_offset")
        val DEFAULT_SORT_OPTION = stringPreferencesKey("default_sort_option")
        val SHOW_COMPLETED_TASKS = booleanPreferencesKey("show_completed_tasks")
        val FIRST_DAY_OF_WEEK = stringPreferencesKey("first_day_of_week")
        val TIME_FORMAT = stringPreferencesKey("time_format")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    // Theme Mode
    fun getThemeMode(): Flow<ThemeMode> {
        return context.dataStore.data.map { preferences ->
            val value = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            ThemeMode.valueOf(value)
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    // Dynamic Color
    fun isDynamicColorEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] ?: true
        }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    // Notifications
    fun areNotificationsEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // Default Reminder Offset
    fun getDefaultReminderOffset(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DEFAULT_REMINDER_OFFSET] ?: 30
        }
    }

    suspend fun setDefaultReminderOffset(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_REMINDER_OFFSET] = minutes
        }
    }

    // Default Sort Option
    fun getDefaultSortOption(): Flow<SortOption> {
        return context.dataStore.data.map { preferences ->
            val value = preferences[PreferencesKeys.DEFAULT_SORT_OPTION] ?: SortOption.DATE.name
            SortOption.valueOf(value)
        }
    }

    suspend fun setDefaultSortOption(option: SortOption) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_SORT_OPTION] = option.name
        }
    }

    // Show Completed Tasks
    fun shouldShowCompletedTasks(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SHOW_COMPLETED_TASKS] ?: true
        }
    }

    suspend fun setShowCompletedTasks(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_COMPLETED_TASKS] = show
        }
    }

    // First Day of Week
    fun getFirstDayOfWeek(): Flow<FirstDayOfWeek> {
        return context.dataStore.data.map { preferences ->
            val value = preferences[PreferencesKeys.FIRST_DAY_OF_WEEK] ?: FirstDayOfWeek.MONDAY.name
            FirstDayOfWeek.valueOf(value)
        }
    }

    suspend fun setFirstDayOfWeek(day: FirstDayOfWeek) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_DAY_OF_WEEK] = day.name
        }
    }

    // Time Format
    fun getTimeFormat(): Flow<TimeFormat> {
        return context.dataStore.data.map { preferences ->
            val value = preferences[PreferencesKeys.TIME_FORMAT] ?: TimeFormat.HOURS_24.name
            TimeFormat.valueOf(value)
        }
    }

    suspend fun setTimeFormat(format: TimeFormat) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TIME_FORMAT] = format.name
        }
    }

    // Onboarding Completed
    fun isOnboardingCompleted(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}
