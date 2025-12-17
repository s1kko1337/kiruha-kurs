package com.example.hometasker.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hometasker.presentation.components.TaskCard
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToTaskDetail: (Long) -> Unit,
    onNavigateToCreateTask: (LocalDate?) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Календарь") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreateTask(uiState.selectedDate) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Заголовок месяца
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = viewModel::onPreviousMonth) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Предыдущий месяц")
                }

                Text(
                    text = uiState.currentMonth.format(
                        DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
                    ).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(onClick = viewModel::onNextMonth) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Следующий месяц")
                }
            }

            // Дни недели
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                val daysOfWeek = listOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
                )
                daysOfWeek.forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale("ru")).uppercase(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Календарная сетка
            val daysInMonth = generateCalendarDays(uiState.currentMonth)

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(daysInMonth) { date ->
                    CalendarDay(
                        date = date,
                        isSelected = date == uiState.selectedDate,
                        isCurrentMonth = date?.month == uiState.currentMonth.month,
                        hasTask = date?.let { uiState.tasksPerDay.containsKey(it) } == true,
                        taskCount = date?.let { uiState.tasksPerDay[it] } ?: 0,
                        onClick = { date?.let { viewModel.onDateSelected(it) } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Задачи на выбранную дату
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Задачи на ${uiState.selectedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale("ru")))}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.tasksForSelectedDate.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Нет задач на эту дату",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.tasksForSelectedDate) { task ->
                                TaskCard(
                                    task = task,
                                    onTaskClick = { onNavigateToTaskDetail(task.id) },
                                    onCheckedChange = { }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate?,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    hasTask: Boolean,
    taskCount: Int,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date == today

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .clickable(enabled = date != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (date != null) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (hasTask) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.primary
                            )
                    )
                }
            }
        }
    }
}

private fun generateCalendarDays(yearMonth: java.time.YearMonth): List<LocalDate?> {
    val days = mutableListOf<LocalDate?>()
    val firstOfMonth = yearMonth.atDay(1)
    val lastOfMonth = yearMonth.atEndOfMonth()

    // Добавляем пустые дни до первого дня месяца
    val firstDayOfWeek = firstOfMonth.dayOfWeek.value // 1 = Monday
    repeat(firstDayOfWeek - 1) {
        days.add(null)
    }

    // Добавляем дни месяца
    var current = firstOfMonth
    while (!current.isAfter(lastOfMonth)) {
        days.add(current)
        current = current.plusDays(1)
    }

    return days
}
