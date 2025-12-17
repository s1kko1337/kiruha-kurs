package com.example.hometasker.widget

import android.content.Context
import androidx.compose.runtime.Composable
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

    @Composable
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
                    text = "Today's Tasks",
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
                        contentDescription = "Add task",
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
                        text = "No tasks for today",
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

    @Composable
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

class ToggleTaskCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val taskId = parameters[TaskWidget.taskIdKey] ?: return

        // Update widget state to show loading/toggled state
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

        // Update the widget
        TaskWidget().update(context, glanceId)

        // Also trigger actual task completion via repository
        // This would be done via WorkManager or similar in production
    }
}
