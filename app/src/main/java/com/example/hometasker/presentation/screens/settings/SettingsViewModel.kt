package com.example.hometasker.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.repository.FirstDayOfWeek
import com.example.hometasker.domain.repository.SettingsRepository
import com.example.hometasker.domain.repository.SortOption
import com.example.hometasker.domain.repository.ThemeMode
import com.example.hometasker.domain.repository.TimeFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.getThemeMode(),
                settingsRepository.isDynamicColorEnabled(),
                settingsRepository.areNotificationsEnabled(),
                settingsRepository.getDefaultReminderOffset(),
                settingsRepository.getDefaultSortOption(),
                settingsRepository.shouldShowCompletedTasks(),
                settingsRepository.getFirstDayOfWeek(),
                settingsRepository.getTimeFormat()
            ) { theme, dynamicColor, notifications, reminder, sort, showCompleted, firstDay, timeFormat ->
                SettingsUiState(
                    themeMode = theme,
                    dynamicColorEnabled = dynamicColor,
                    notificationsEnabled = notifications,
                    defaultReminderOffset = reminder,
                    defaultSortOption = sort,
                    showCompletedTasks = showCompleted,
                    firstDayOfWeek = firstDay,
                    timeFormat = timeFormat,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun onDynamicColorChange(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColorEnabled(enabled)
        }
    }

    fun onNotificationsChange(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }

    fun onDefaultReminderChange(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultReminderOffset(minutes)
        }
    }

    fun onDefaultSortChange(option: SortOption) {
        viewModelScope.launch {
            settingsRepository.setDefaultSortOption(option)
        }
    }

    fun onShowCompletedTasksChange(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowCompletedTasks(show)
        }
    }

    fun onFirstDayOfWeekChange(day: FirstDayOfWeek) {
        viewModelScope.launch {
            settingsRepository.setFirstDayOfWeek(day)
        }
    }

    fun onTimeFormatChange(format: TimeFormat) {
        viewModelScope.launch {
            settingsRepository.setTimeFormat(format)
        }
    }
}
