package com.example.hometasker.presentation.screens.settings

import com.example.hometasker.domain.repository.FirstDayOfWeek
import com.example.hometasker.domain.repository.SortOption
import com.example.hometasker.domain.repository.ThemeMode
import com.example.hometasker.domain.repository.TimeFormat

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val defaultReminderOffset: Int = 30,
    val defaultSortOption: SortOption = SortOption.DATE,
    val showCompletedTasks: Boolean = true,
    val firstDayOfWeek: FirstDayOfWeek = FirstDayOfWeek.MONDAY,
    val timeFormat: TimeFormat = TimeFormat.HOURS_24,
    val isLoading: Boolean = true
)
