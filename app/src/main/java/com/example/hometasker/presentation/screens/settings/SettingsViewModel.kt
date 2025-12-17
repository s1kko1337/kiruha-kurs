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
            // Используем два combine для обхода ограничения в 5 параметров
            val firstPart = combine(
                settingsRepository.getThemeMode(),
                settingsRepository.isDynamicColorEnabled(),
                settingsRepository.areNotificationsEnabled(),
                settingsRepository.getDefaultReminderOffset()
            ) { theme, dynamicColor, notifications, reminder ->
                SettingsPartOne(theme, dynamicColor, notifications, reminder)
            }

            val secondPart = combine(
                settingsRepository.getDefaultSortOption(),
                settingsRepository.shouldShowCompletedTasks(),
                settingsRepository.getFirstDayOfWeek(),
                settingsRepository.getTimeFormat()
            ) { sort, showCompleted, firstDay, timeFormat ->
                SettingsPartTwo(sort, showCompleted, firstDay, timeFormat)
            }

            combine(firstPart, secondPart) { part1, part2 ->
                SettingsUiState(
                    themeMode = part1.themeMode,
                    dynamicColorEnabled = part1.dynamicColorEnabled,
                    notificationsEnabled = part1.notificationsEnabled,
                    defaultReminderOffset = part1.defaultReminderOffset,
                    defaultSortOption = part2.defaultSortOption,
                    showCompletedTasks = part2.showCompletedTasks,
                    firstDayOfWeek = part2.firstDayOfWeek,
                    timeFormat = part2.timeFormat,
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

private data class SettingsPartOne(
    val themeMode: ThemeMode,
    val dynamicColorEnabled: Boolean,
    val notificationsEnabled: Boolean,
    val defaultReminderOffset: Int
)

private data class SettingsPartTwo(
    val defaultSortOption: SortOption,
    val showCompletedTasks: Boolean,
    val firstDayOfWeek: FirstDayOfWeek,
    val timeFormat: TimeFormat
)
