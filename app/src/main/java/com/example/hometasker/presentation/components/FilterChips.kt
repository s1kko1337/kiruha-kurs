package com.example.hometasker.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hometasker.domain.usecase.task.TaskFilter

@Composable
fun TaskFilterChips(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        TaskFilter.ALL to "Все",
        TaskFilter.TODAY to "Сегодня",
        TaskFilter.WEEK to "Неделя",
        TaskFilter.PENDING to "Активные",
        TaskFilter.COMPLETED to "Выполненные",
        TaskFilter.OVERDUE to "Просроченные"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (filter, label) ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(label) }
            )
        }
    }
}
