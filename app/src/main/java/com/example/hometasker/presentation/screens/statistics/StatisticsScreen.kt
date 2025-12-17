package com.example.hometasker.presentation.screens.statistics

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") }
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

            uiState.statistics == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет данных для отображения")
                }
            }

            else -> {
                val stats = uiState.statistics!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Streak
                    if (stats.currentStreak > 0) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "\uD83D\uDD25 ${stats.currentStreak}",
                                    style = MaterialTheme.typography.displayMedium
                                )
                                Text(
                                    text = "дней подряд",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Основная статистика
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Сегодня",
                            value = stats.completedToday.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Неделя",
                            value = stats.completedThisWeek.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Месяц",
                            value = stats.completedThisMonth.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Процент выполнения
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Выполнение задач",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Выполнено")
                                Text("${stats.completedTasks} из ${stats.totalTasks}")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { stats.completionRate / 100f },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${stats.completionRate}%",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    // Среднее время
                    if (stats.avgCompletionTimeMinutes > 0) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Среднее время выполнения",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${stats.avgCompletionTimeMinutes} мин",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                    }

                    // Продуктивные дни
                    if (stats.productiveWeekdays.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Продуктивность по дням недели",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                stats.productiveWeekdays.entries
                                    .sortedByDescending { it.value }
                                    .forEach { (day, count) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = day.getDisplayName(
                                                    java.time.format.TextStyle.FULL,
                                                    java.util.Locale("ru")
                                                ).replaceFirstChar { it.uppercase() }
                                            )
                                            Text(text = "$count задач")
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
