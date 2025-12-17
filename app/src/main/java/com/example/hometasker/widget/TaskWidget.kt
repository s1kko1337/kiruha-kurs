package com.example.hometasker.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.hometasker.MainActivity
import com.example.hometasker.R
import com.example.hometasker.domain.repository.TaskRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TaskWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                TaskWidgetContent()
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun TaskWidgetContent() {
        val prefs = currentState<Preferences>()
        val tasksJson = prefs[TASKS_KEY] ?: "[]"
        val tasks = try {
            Json.decodeFromString<List<WidgetTask>>(tasksJson)
        } catch (e: Exception) {
            emptyList()
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Задачи на сегодня",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Box(
                    modifier = GlanceModifier
                        .size(32.dp)
                        .cornerRadius(16.dp)
                        .background(GlanceTheme.colors.primary)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_add),
                        contentDescription = "Добавить задачу",
                        modifier = GlanceModifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет задач на сегодня",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                    items(tasks, itemId = { it.id }) { task ->
                        TaskItem(task = task)
                        Spacer(modifier = GlanceModifier.height(8.dp))
                    }
                }
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun TaskItem(task: WidgetTask) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.surfaceVariant)
                .cornerRadius(8.dp)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CheckBox(
                checked = task.isCompleted,
                onCheckedChange = actionRunCallback<ToggleTaskCallback>(
                    actionParametersOf(taskIdKey to task.id)
                )
            )

            Spacer(modifier = GlanceModifier.width(8.dp))

            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = task.title,
                    style = TextStyle(
                        color = if (task.isCompleted) {
                            GlanceTheme.colors.onSurfaceVariant
                        } else {
                            GlanceTheme.colors.onSurface
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )

                if (task.time != null) {
                    Text(
                        text = task.time,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            if (task.priorityColor != null) {
                Box(
                    modifier = GlanceModifier
                        .size(8.dp)
                        .cornerRadius(4.dp)
                        .background(ColorProvider(Color(task.priorityColor))),
                    contentAlignment = Alignment.Center
                ) {
                    // Priority indicator
                }
            }
        }
    }

    companion object {
        val TASKS_KEY = stringPreferencesKey("widget_tasks")
        val taskIdKey = ActionParameters.Key<Long>("task_id")
    }
}

@Serializable
data class WidgetTask(
    val id: Long,
    val title: String,
    val time: String?,
    val isCompleted: Boolean,
    val priorityColor: Int?
)

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun taskRepository(): TaskRepository
    fun taskWidgetUpdater(): TaskWidgetUpdater
}

class ToggleTaskCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val taskId = parameters[TaskWidget.taskIdKey] ?: return

        // Get repository via EntryPoint
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val taskRepository = entryPoint.taskRepository()
        val widgetUpdater = entryPoint.taskWidgetUpdater()

        // Update widget state immediately for responsive UI
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val tasksJson = prefs[TaskWidget.TASKS_KEY] ?: "[]"
            val tasks = try {
                Json.decodeFromString<List<WidgetTask>>(tasksJson).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }

            val taskIndex = tasks.indexOfFirst { it.id == taskId }
            if (taskIndex != -1) {
                val task = tasks[taskIndex]
                tasks[taskIndex] = task.copy(isCompleted = !task.isCompleted)
            }

            prefs.toMutablePreferences().apply {
                this[TaskWidget.TASKS_KEY] = Json.encodeToString(tasks)
            }
        }

        // Update the widget UI
        TaskWidget().update(context, glanceId)

        // Persist to database
        withContext(Dispatchers.IO) {
            val task = taskRepository.getTaskById(taskId)
            if (task != null) {
                taskRepository.toggleTaskCompletion(taskId, !task.isCompleted)
            }
        }
    }
}
