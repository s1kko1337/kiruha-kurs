package com.example.hometasker.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hometasker.domain.repository.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToCategories: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Секция: Внешний вид
            SettingsSection(title = "Внешний вид")

            SettingsItem(
                title = "Тема",
                subtitle = when (uiState.themeMode) {
                    ThemeMode.LIGHT -> "Светлая"
                    ThemeMode.DARK -> "Тёмная"
                    ThemeMode.SYSTEM -> "Системная"
                },
                onClick = {
                    // Переключаем тему циклически
                    val nextMode = when (uiState.themeMode) {
                        ThemeMode.SYSTEM -> ThemeMode.LIGHT
                        ThemeMode.LIGHT -> ThemeMode.DARK
                        ThemeMode.DARK -> ThemeMode.SYSTEM
                    }
                    viewModel.onThemeModeChange(nextMode)
                }
            )

            SettingsSwitch(
                title = "Dynamic Color",
                subtitle = "Использовать цвета из обоев (Android 12+)",
                checked = uiState.dynamicColorEnabled,
                onCheckedChange = viewModel::onDynamicColorChange
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Секция: Уведомления
            SettingsSection(title = "Уведомления")

            SettingsSwitch(
                title = "Уведомления",
                subtitle = "Напоминания о задачах",
                checked = uiState.notificationsEnabled,
                onCheckedChange = viewModel::onNotificationsChange
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Секция: Задачи
            SettingsSection(title = "Задачи")

            SettingsSwitch(
                title = "Показывать выполненные",
                subtitle = "Отображать завершённые задачи в списке",
                checked = uiState.showCompletedTasks,
                onCheckedChange = viewModel::onShowCompletedTasksChange
            )

            SettingsItem(
                title = "Категории",
                subtitle = "Управление категориями задач",
                onClick = onNavigateToCategories,
                showArrow = true
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Секция: О приложении
            SettingsSection(title = "О приложении")

            SettingsItem(
                title = "Версия",
                subtitle = "1.0.0",
                onClick = { }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showArrow: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
