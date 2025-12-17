package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.model.Priority
import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.SettingsRepository
import com.example.hometasker.domain.repository.SortOption
import com.example.hometasker.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для получения списка задач с фильтрацией и сортировкой
 */
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(
        filter: TaskFilter = TaskFilter.ALL,
        sortOption: SortOption? = null,
        categoryId: Long? = null
    ): Flow<List<Task>> {
        val tasksFlow = when {
            categoryId != null -> taskRepository.getTasksByCategory(categoryId)
            filter == TaskFilter.TODAY -> taskRepository.getTasksByDate(LocalDate.now())
            filter == TaskFilter.WEEK -> taskRepository.getTasksByDateRange(
                LocalDate.now(),
                LocalDate.now().plusDays(7)
            )
            filter == TaskFilter.COMPLETED -> taskRepository.getCompletedTasks()
            filter == TaskFilter.PENDING -> taskRepository.getPendingTasks()
            filter == TaskFilter.OVERDUE -> taskRepository.getOverdueTasks()
            else -> taskRepository.getAllTasks()
        }

        return if (sortOption != null) {
            tasksFlow.map { tasks -> sortTasks(tasks, sortOption) }
        } else {
            combine(tasksFlow, settingsRepository.getDefaultSortOption()) { tasks, defaultSort ->
                sortTasks(tasks, defaultSort)
            }
        }
    }

    private fun sortTasks(tasks: List<Task>, sortOption: SortOption): List<Task> {
        return when (sortOption) {
            SortOption.DATE -> tasks.sortedWith(
                compareBy(nullsLast()) { it.dueDate }
            )
            SortOption.PRIORITY -> tasks.sortedByDescending {
                when (it.priority) {
                    Priority.HIGH -> 3
                    Priority.MEDIUM -> 2
                    Priority.LOW -> 1
                    Priority.NONE -> 0
                }
            }
            SortOption.STATUS -> tasks.sortedBy { it.isCompleted }
            SortOption.CATEGORY -> tasks.sortedBy { it.categoryId }
            SortOption.NAME -> tasks.sortedBy { it.title.lowercase() }
        }
    }
}

enum class TaskFilter {
    ALL,
    TODAY,
    WEEK,
    COMPLETED,
    PENDING,
    OVERDUE
}
