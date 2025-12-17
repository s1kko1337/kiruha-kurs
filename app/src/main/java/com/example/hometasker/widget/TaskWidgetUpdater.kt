package com.example.hometasker.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.hometasker.domain.model.Priority
import com.example.hometasker.domain.model.Task
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskWidgetUpdater @Inject constructor() {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    suspend fun updateWidget(context: Context, tasks: List<Task>) {
        val glanceManager = GlanceAppWidgetManager(context)
        val glanceIds = glanceManager.getGlanceIds(TaskWidget::class.java)

        val widgetTasks = tasks.map { task ->
            WidgetTask(
                id = task.id,
                title = task.title,
                time = task.scheduledTime?.format(timeFormatter),
                isCompleted = task.isCompleted,
                priorityColor = getPriorityColor(task.priority)
            )
        }

        val tasksJson = Json.encodeToString(widgetTasks)

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[TaskWidget.TASKS_KEY] = tasksJson
                }
            }
            TaskWidget().update(context, glanceId)
        }
    }

    private fun getPriorityColor(priority: Priority): Int? {
        return when (priority) {
            Priority.NONE -> null
            Priority.LOW -> 0xFF4CAF50.toInt() // Green
            Priority.MEDIUM -> 0xFFFF9800.toInt() // Orange
            Priority.HIGH -> 0xFFF44336.toInt() // Red
        }
    }
}
