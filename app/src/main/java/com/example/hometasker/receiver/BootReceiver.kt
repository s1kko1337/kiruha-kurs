package com.example.hometasker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.hometasker.domain.repository.TaskRepository
import com.example.hometasker.service.NotificationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var notificationService: NotificationService

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            rescheduleAllReminders()
        }
    }

    private fun rescheduleAllReminders() {
        scope.launch {
            try {
                // Get all tasks that have reminders set for today or future
                val today = LocalDate.now()
                val tasks = taskRepository.getAllTasks().first()

                tasks.filter { task ->
                    val reminderTime = task.reminderDateTime
                    reminderTime != null &&
                            reminderTime.toLocalDate() >= today &&
                            !task.isCompleted
                }.forEach { task ->
                    notificationService.scheduleTaskReminder(task)
                }
            } catch (e: Exception) {
                // Log error in production
                e.printStackTrace()
            }
        }
    }
}
