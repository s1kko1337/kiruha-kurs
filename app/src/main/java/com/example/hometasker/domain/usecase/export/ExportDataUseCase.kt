package com.example.hometasker.domain.usecase.export

import com.example.hometasker.domain.repository.CategoryRepository
import com.example.hometasker.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * UseCase для экспорта данных в JSON
 */
class ExportDataUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    suspend operator fun invoke(): String {
        val tasks = taskRepository.getAllTasks().first()
        val categories = categoryRepository.getAllCategories().first()

        val exportData = ExportData(
            version = 1,
            exportedAt = System.currentTimeMillis(),
            tasks = tasks.map { task ->
                ExportTask(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    categoryId = task.categoryId,
                    priority = task.priority.name,
                    isCompleted = task.isCompleted,
                    completedAt = task.completedAt?.toString(),
                    dueDate = task.dueDate?.toString(),
                    dueTime = task.dueTime?.toString(),
                    estimatedMinutes = task.estimatedMinutes,
                    actualMinutes = task.actualMinutes,
                    repeatType = task.repeatType.name,
                    repeatConfig = task.repeatConfig,
                    repeatEndDate = task.repeatEndDate?.toString(),
                    repeatCount = task.repeatCount,
                    reminderEnabled = task.reminderEnabled,
                    reminderOffsetMinutes = task.reminderOffsetMinutes,
                    createdAt = task.createdAt.toString(),
                    updatedAt = task.updatedAt.toString()
                )
            },
            categories = categories.map { category ->
                ExportCategory(
                    id = category.id,
                    name = category.name,
                    parentId = category.parentId,
                    colorHex = category.colorHex,
                    iconName = category.iconName,
                    sortOrder = category.sortOrder,
                    isDefault = category.isDefault
                )
            }
        )

        return json.encodeToString(exportData)
    }
}

@Serializable
data class ExportData(
    val version: Int,
    val exportedAt: Long,
    val tasks: List<ExportTask>,
    val categories: List<ExportCategory>
)

@Serializable
data class ExportTask(
    val id: Long,
    val title: String,
    val description: String?,
    val categoryId: Long?,
    val priority: String,
    val isCompleted: Boolean,
    val completedAt: String?,
    val dueDate: String?,
    val dueTime: String?,
    val estimatedMinutes: Int?,
    val actualMinutes: Int?,
    val repeatType: String,
    val repeatConfig: String?,
    val repeatEndDate: String?,
    val repeatCount: Int?,
    val reminderEnabled: Boolean,
    val reminderOffsetMinutes: Int?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ExportCategory(
    val id: Long,
    val name: String,
    val parentId: Long?,
    val colorHex: String,
    val iconName: String?,
    val sortOrder: Int,
    val isDefault: Boolean
)
