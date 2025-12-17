package com.example.hometasker.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hometasker.domain.model.Priority
import com.example.hometasker.domain.model.Task
import com.example.hometasker.presentation.theme.PriorityHigh
import com.example.hometasker.presentation.theme.PriorityLow
import com.example.hometasker.presentation.theme.PriorityMedium
import com.example.hometasker.presentation.theme.PriorityNone
import java.time.format.DateTimeFormatter

@Composable
fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (task.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "backgroundColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTaskClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Индикатор приоритета
            PriorityIndicator(priority = task.priority)

            Spacer(modifier = Modifier.width(12.dp))

            // Основной контент
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Заголовок
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Описание (если есть)
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Дата и время
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    task.dueDate?.let { date ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("dd.MM")),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    task.dueTime?.let { time ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    task.estimatedMinutes?.let { minutes ->
                        Text(
                            text = "${minutes} мин",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityIndicator(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val color = when (priority) {
        Priority.HIGH -> PriorityHigh
        Priority.MEDIUM -> PriorityMedium
        Priority.LOW -> PriorityLow
        Priority.NONE -> PriorityNone
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}

fun Priority.toColor(): Color {
    return when (this) {
        Priority.HIGH -> PriorityHigh
        Priority.MEDIUM -> PriorityMedium
        Priority.LOW -> PriorityLow
        Priority.NONE -> PriorityNone
    }
}
