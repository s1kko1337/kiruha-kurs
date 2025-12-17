package com.example.hometasker.domain.usecase.task

import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase для поиска задач
 */
class SearchTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(query: String): Flow<List<Task>> {
        return taskRepository.searchTasks(query)
    }
}
