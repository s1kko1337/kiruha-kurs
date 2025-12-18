package com.example.hometasker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.hometasker.domain.repository.TaskRepository
import com.example.hometasker.service.NotificationService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AlarmReceiverEntryPoint::class.java
        )
        val notificationService = entryPoint.notificationService()
        val taskRepository = entryPoint.taskRepository()

        when (intent.action) {
            ACTION_TASK_REMINDER -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
                val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: return
                val taskDescription = intent.getStringExtra(EXTRA_TASK_DESCRIPTION)

                if (taskId != -1L) {
                    notificationService.showTaskReminderNotification(
                        taskId = taskId,
                        title = taskTitle,
                        description = taskDescription
                    )
                }
            }

            ACTION_COMPLETE_TASK -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
                if (taskId != -1L) {
                    scope.launch {
                        taskRepository.toggleTaskCompletion(taskId, true)
                    }
                }
            }

            ACTION_DAILY_SUMMARY -> {
                notificationService.showDailySummaryNotification(
                    tasksCount = 0,
                    completedCount = 0
                )
            }
        }
    }

    companion object {
        const val ACTION_TASK_REMINDER = "com.example.hometasker.action.TASK_REMINDER"
        const val ACTION_COMPLETE_TASK = "com.example.hometasker.action.COMPLETE_TASK"
        const val ACTION_DAILY_SUMMARY = "com.example.hometasker.action.DAILY_SUMMARY"

        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_DESCRIPTION = "extra_task_description"
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AlarmReceiverEntryPoint {
    fun notificationService(): NotificationService
    fun taskRepository(): TaskRepository
}
