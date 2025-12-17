package com.example.hometasker.presentation.screens.task_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hometasker.domain.model.TrackingStatus
import com.example.hometasker.presentation.components.CategoryChip
import com.example.hometasker.presentation.components.PriorityIndicator
import com.example.hometasker.presentation.components.StartTimerButton
import com.example.hometasker.presentation.components.TimerDisplay
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали задачи") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onToggleCompletion() }) {
                        Icon(
                            imageVector = if (uiState.task?.isCompleted == true) {
                                Icons.Filled.CheckCircle
                            } else {
                                Icons.Outlined.CheckCircle
                            },
                            contentDescription = "Отметить выполненной"
                        )
                    }
                    IconButton(onClick = { onNavigateToEdit(taskId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.task == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Задача не найдена")
                }
            }

            else -> {
                val task = uiState.task!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Заголовок и приоритет
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PriorityIndicator(priority = task.priority)
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    // Категории
                    if (uiState.categories.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.categories.forEach { category ->
                                CategoryChip(
                                    category = category,
                                    onClick = { }
                                )
                            }
                        }
                    }

                    // Описание
                    if (!task.description.isNullOrBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = task.description,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Дата и время
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            task.dueDate?.let { date ->
                                Row {
                                    Text(
                                        text = "Дата: ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            task.dueTime?.let { time ->
                                Row {
                                    Text(
                                        text = "Время: ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            task.estimatedMinutes?.let { minutes ->
                                Row {
                                    Text(
                                        text = "Оценка времени: ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$minutes мин",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            task.actualMinutes?.let { minutes ->
                                Row {
                                    Text(
                                        text = "Фактическое время: ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$minutes мин",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // Таймер
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Трекинг времени",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (uiState.activeSession != null) {
                        TimerDisplay(
                            elapsedSeconds = uiState.elapsedSeconds,
                            estimatedMinutes = task.estimatedMinutes,
                            status = uiState.activeSession!!.status,
                            onStart = viewModel::onStartTracking,
                            onPause = viewModel::onPauseTracking,
                            onResume = viewModel::onResumeTracking,
                            onStop = viewModel::onStopTracking
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Начать отсчёт времени")
                                StartTimerButton(onClick = viewModel::onStartTracking)
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить задачу?") },
            text = { Text("Эта задача будет удалена безвозвратно.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDelete { onNavigateBack() }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
