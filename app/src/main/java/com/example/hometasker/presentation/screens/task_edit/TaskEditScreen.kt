package com.example.hometasker.presentation.screens.task_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.model.Priority
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskEditScreen(
    taskId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Редактирование" else "Новая задача") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.save() },
                        enabled = !uiState.isSaving
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Название
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Название *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.error?.contains("название") == true
            )

            // Описание
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Приоритет
            Text(
                text = "Приоритет",
                style = MaterialTheme.typography.labelLarge
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                Priority.entries.forEachIndexed { index, priority ->
                    SegmentedButton(
                        selected = uiState.priority == priority,
                        onClick = { viewModel.onPriorityChange(priority) },
                        shape = SegmentedButtonDefaults.itemShape(index, Priority.entries.size)
                    ) {
                        Text(
                            when (priority) {
                                Priority.HIGH -> "Высокий"
                                Priority.MEDIUM -> "Средний"
                                Priority.LOW -> "Низкий"
                                Priority.NONE -> "Нет"
                            }
                        )
                    }
                }
            }

            // Категории - multi-select
            Text(
                text = "Категории",
                style = MaterialTheme.typography.labelLarge
            )

            // Выбранные категории как чипы
            val selectedCategories = uiState.categories.filter { it.id in uiState.selectedCategoryIds }
            if (selectedCategories.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedCategories.forEach { category ->
                        CategoryChip(
                            category = category,
                            onRemove = { viewModel.onCategoryToggle(category.id) }
                        )
                    }
                }
            }

            // Кнопка добавления категорий
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCategoryPicker = true },
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategories.isEmpty()) "Выбрать категории"
                               else "Изменить категории (${selectedCategories.size})",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Дата
            OutlinedTextField(
                value = uiState.dueDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата выполнения") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("Выбрать")
                    }
                }
            )

            // Время
            OutlinedTextField(
                value = uiState.dueTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Время выполнения") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { showTimePicker = true }) {
                        Text("Выбрать")
                    }
                }
            )

            // Оценка времени
            OutlinedTextField(
                value = uiState.estimatedMinutes?.toString() ?: "",
                onValueChange = { value ->
                    viewModel.onEstimatedMinutesChange(value.toIntOrNull())
                },
                label = { Text("Оценка времени (минуты)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Напоминание
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Напоминание")
                Switch(
                    checked = uiState.reminderEnabled,
                    onCheckedChange = viewModel::onReminderEnabledChange
                )
            }

            // Ошибка
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Category Picker Dialog
    if (showCategoryPicker) {
        CategoryPickerDialog(
            categories = uiState.categories,
            selectedCategoryIds = uiState.selectedCategoryIds,
            onCategoryToggle = viewModel::onCategoryToggle,
            onDismiss = { showCategoryPicker = false }
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.dueDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        viewModel.onDueDateChange(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.dueTime?.hour ?: 12,
            initialMinute = uiState.dueTime?.minute ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDueTimeChange(
                        LocalTime.of(timePickerState.hour, timePickerState.minute)
                    )
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Отмена")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    onRemove: () -> Unit
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = categoryColor.copy(alpha = 0.15f),
        modifier = Modifier.border(1.dp, categoryColor, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoryPickerDialog(
    categories: List<Category>,
    selectedCategoryIds: List<Long>,
    onCategoryToggle: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val rootCategories = categories.filter { it.parentId == null }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите категории") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rootCategories.forEach { rootCategory ->
                    CategoryPickerItem(
                        category = rootCategory,
                        isSelected = rootCategory.id in selectedCategoryIds,
                        onToggle = { onCategoryToggle(rootCategory.id) },
                        isRoot = true
                    )

                    // Подкатегории
                    val subcategories = categories.filter { it.parentId == rootCategory.id }
                    subcategories.forEach { subcategory ->
                        CategoryPickerItem(
                            category = subcategory,
                            isSelected = subcategory.id in selectedCategoryIds,
                            onToggle = { onCategoryToggle(subcategory.id) },
                            isRoot = false,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Готово")
            }
        }
    )
}

@Composable
private fun CategoryPickerItem(
    category: Category,
    isSelected: Boolean,
    onToggle: () -> Unit,
    isRoot: Boolean,
    modifier: Modifier = Modifier
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )

        Box(
            modifier = Modifier
                .size(if (isRoot) 20.dp else 14.dp)
                .clip(CircleShape)
                .background(categoryColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = category.name,
            style = if (isRoot) MaterialTheme.typography.titleSmall
                    else MaterialTheme.typography.bodyMedium
        )
    }
}
