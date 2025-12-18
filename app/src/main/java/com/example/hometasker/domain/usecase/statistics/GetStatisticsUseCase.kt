package com.example.hometasker.domain.usecase.statistics

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для получения статистики
 */
class GetStatisticsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<Statistics> {
        return taskRepository.getAllTasks().map { tasks ->
            val today = LocalDate.now()
            val weekAgo = today.minusDays(7)
            val monthAgo = today.minusDays(30)

            val completedTasks = tasks.filter { it.isCompleted }
            val completedToday = completedTasks.count {
                it.completedAt?.toLocalDate() == today
            }
            val completedThisWeek = completedTasks.count { task ->
                task.completedAt?.toLocalDate()?.let { date ->
                    date.isAfter(weekAgo) || date == weekAgo
                } == true
            }
            val completedThisMonth = completedTasks.count { task ->
                task.completedAt?.toLocalDate()?.let { date ->
                    date.isAfter(monthAgo) || date == monthAgo
                } == true
            }

            val completionRate = if (tasks.isNotEmpty()) {
                (completedTasks.size.toFloat() / tasks.size * 100).toInt()
            } else 0

            val avgCompletionTime = completedTasks
                .mapNotNull { it.actualMinutes }
                .average()
                .takeIf { !it.isNaN() }
                ?.toInt() ?: 0

            val tasksByDay = completedTasks
                .groupBy { it.completedAt?.toLocalDate() }
                .filterKeys { it != null && it.isAfter(weekAgo) }
                .mapKeys { it.key!! }
                .mapValues { it.value.size }

            val productiveWeekdays = completedTasks
                .groupBy { it.completedAt?.dayOfWeek }
                .filterKeys { it != null }
                .mapKeys { it.key!! }
                .mapValues { it.value.size }

            val tasksByCategory = tasks
                .flatMap { task -> task.categoryIds.map { categoryId -> categoryId to task } }
                .groupBy({ it.first }, { it.second })
                .mapValues { it.value.size }

            val streak = calculateStreak(completedTasks)

            val pendingTasks = tasks.filter { !it.isCompleted }
            val overdueTasks = pendingTasks.count { it.isOverdue }
            val highPriorityPending = pendingTasks.count {
                it.priority == com.example.hometasker.domain.model.Priority.HIGH
            }

            Statistics(
                completedToday = completedToday,
                completedThisWeek = completedThisWeek,
                completedThisMonth = completedThisMonth,
                totalTasks = tasks.size,
                completedTasks = completedTasks.size,
                completionRate = completionRate,
                avgCompletionTimeMinutes = avgCompletionTime,
                tasksByDay = tasksByDay,
                productiveWeekdays = productiveWeekdays,
                tasksByCategory = tasksByCategory,
                currentStreak = streak,
                overdueTasks = overdueTasks,
                pendingTasks = pendingTasks.size,
                highPriorityPending = highPriorityPending
            )
        }
    }

    private fun calculateStreak(completedTasks: List<Task>): Int {
        val completedDates = completedTasks
            .mapNotNull { it.completedAt?.toLocalDate() }
            .distinct()
            .sortedDescending()

        if (completedDates.isEmpty()) return 0

        var streak = 0
        var currentDate = LocalDate.now()

        // Если сегодня ещё нет выполненных задач, начинаем со вчера
        if (currentDate !in completedDates) {
            currentDate = currentDate.minusDays(1)
        }

        for (date in completedDates) {
            if (date == currentDate) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else if (date.isBefore(currentDate)) {
                break
            }
        }

        return streak
    }
}

data class Statistics(
    val completedToday: Int,
    val completedThisWeek: Int,
    val completedThisMonth: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val completionRate: Int,
    val avgCompletionTimeMinutes: Int,
    val tasksByDay: Map<LocalDate, Int>,
    val productiveWeekdays: Map<DayOfWeek, Int>,
    val tasksByCategory: Map<Long, Int>,
    val currentStreak: Int,
    val overdueTasks: Int,
    val pendingTasks: Int,
    val highPriorityPending: Int
)
