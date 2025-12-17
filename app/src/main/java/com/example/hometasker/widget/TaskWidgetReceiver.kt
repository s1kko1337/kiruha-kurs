package com.example.hometasker.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = TaskWidget()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        updateWidgetData(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateWidgetData(context)
    }

    private fun updateWidgetData(context: Context) {
        coroutineScope.launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    WidgetEntryPoint::class.java
                )
                val taskRepository = entryPoint.taskRepository()
                val widgetUpdater = entryPoint.taskWidgetUpdater()

                // Get today's tasks
                val todayTasks = taskRepository.getTasksByDate(LocalDate.now()).first()
                    .filter { !it.isCompleted }
                    .take(10) // Limit to 10 tasks for widget

                widgetUpdater.updateWidget(context, todayTasks)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
