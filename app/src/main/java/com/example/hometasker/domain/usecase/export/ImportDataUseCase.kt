package com.example.hometasker.domain.usecase.export

import com.example.hometasker.domain.model.Category
import com.example.hometasker.domain.model.Priority
import com.example.hometasker.domain.model.RepeatType
import com.example.hometasker.domain.model.Task
import com.example.hometasker.domain.repository.CategoryRepository
import com.example.hometasker.domain.repository.TaskRepository
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

/**
 * UseCase для импорта данных из JSON
 */
class ImportDataUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    sealed class ImportResult {
        data class Success(val tasksCount: Int, val categoriesCount: Int) : ImportResult()
        data class Error(val message: String) : ImportResult()
    }

    suspend operator fun invoke(jsonString: String, replaceAll: Boolean = false): ImportResult {
        return try {
            val exportData = json.decodeFromString<ExportData>(jsonString)

            // Импортируем категории
            val categoryIdMap = mutableMapOf<Long, Long>()
            for (exportCategory in exportData.categories) {
                val category = Category(
                    name = exportCategory.name,
                    parentId = exportCategory.parentId?.let { categoryIdMap[it] },
                    colorHex = exportCategory.colorHex,
                    iconName = exportCategory.iconName,
                    sortOrder = exportCategory.sortOrder,
                    isDefault = exportCategory.isDefault
                )
                val newId = categoryRepository.insertCategory(category)
                categoryIdMap[exportCategory.id] = newId
            }

            // Импортируем задачи
            for (exportTask in exportData.tasks) {
                val task = Task(
                    title = exportTask.title,
                    description = exportTask.description,
                    categoryId = exportTask.categoryId?.let { categoryIdMap[it] },
                    priority = Priority.valueOf(exportTask.priority),
                    isCompleted = exportTask.isCompleted,
                    completedAt = exportTask.completedAt?.let { LocalDateTime.parse(it) },
                    dueDate = exportTask.dueDate?.let { LocalDate.parse(it) },
                    dueTime = exportTask.dueTime?.let { LocalTime.parse(it) },
                    estimatedMinutes = exportTask.estimatedMinutes,
                    actualMinutes = exportTask.actualMinutes,
                    repeatType = RepeatType.valueOf(exportTask.repeatType),
                    repeatConfig = exportTask.repeatConfig,
                    repeatEndDate = exportTask.repeatEndDate?.let { LocalDate.parse(it) },
                    repeatCount = exportTask.repeatCount,
                    reminderEnabled = exportTask.reminderEnabled,
                    reminderOffsetMinutes = exportTask.reminderOffsetMinutes,
                    createdAt = LocalDateTime.parse(exportTask.createdAt),
                    updatedAt = LocalDateTime.parse(exportTask.updatedAt)
                )
                taskRepository.insertTask(task)
            }

            ImportResult.Success(
                tasksCount = exportData.tasks.size,
                categoriesCount = exportData.categories.size
            )
        } catch (e: Exception) {
            ImportResult.Error(e.message ?: "Ошибка импорта данных")
        }
    }
}
