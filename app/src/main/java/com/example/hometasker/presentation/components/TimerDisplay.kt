package com.example.hometasker.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hometasker.domain.model.TrackingStatus

@Composable
fun TimerDisplay(
    elapsedSeconds: Long,
    estimatedMinutes: Int?,
    status: TrackingStatus,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60

    val timeString = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    val progress = if (estimatedMinutes != null && estimatedMinutes > 0) {
        (elapsedSeconds / 60f) / estimatedMinutes
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(300),
        label = "progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Время
            Text(
                text = timeString,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Оценочное время
            if (estimatedMinutes != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Оценка: $estimatedMinutes мин",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (progress > 1f) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки управления
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (status) {
                    TrackingStatus.IN_PROGRESS -> {
                        // Пауза
                        FilledIconButton(
                            onClick = onPause,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Пауза"
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Стоп
                        FilledIconButton(
                            onClick = onStop,
                            modifier = Modifier.size(56.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Завершить"
                            )
                        }
                    }

                    TrackingStatus.PAUSED -> {
                        // Продолжить
                        FilledIconButton(
                            onClick = onResume,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Продолжить"
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Стоп
                        FilledIconButton(
                            onClick = onStop,
                            modifier = Modifier.size(56.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Завершить"
                            )
                        }
                    }

                    TrackingStatus.COMPLETED -> {
                        // Начать заново
                        FilledIconButton(
                            onClick = onStart,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Начать"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StartTimerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Начать отсчёт"
        )
    }
}
