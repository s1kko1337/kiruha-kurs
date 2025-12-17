package com.example.hometasker.presentation.navigation

/**
 * Sealed class для определения экранов навигации
 */
sealed class Screen(val route: String) {
    // Онбординг
    data object Onboarding : Screen("onboarding")

    // Главные экраны (Bottom Navigation)
    data object Home : Screen("home")
    data object Calendar : Screen("calendar")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")

    // Детальные экраны
    data object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }

    data object TaskEdit : Screen("task_edit?taskId={taskId}") {
        fun createRoute(taskId: Long? = null) = if (taskId != null) {
            "task_edit?taskId=$taskId"
        } else {
            "task_edit"
        }
    }

    data object Categories : Screen("categories")

    data object CategoryEdit : Screen("category_edit?categoryId={categoryId}") {
        fun createRoute(categoryId: Long? = null) = if (categoryId != null) {
            "category_edit?categoryId=$categoryId"
        } else {
            "category_edit"
        }
    }

    data object Search : Screen("search")
}
