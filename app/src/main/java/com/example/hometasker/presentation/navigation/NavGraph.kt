package com.example.hometasker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hometasker.presentation.screens.calendar.CalendarScreen
import com.example.hometasker.presentation.screens.categories.CategoriesScreen
import com.example.hometasker.presentation.screens.home.HomeScreen
import com.example.hometasker.presentation.screens.onboarding.OnboardingScreen
import com.example.hometasker.presentation.screens.search.SearchScreen
import com.example.hometasker.presentation.screens.settings.SettingsScreen
import com.example.hometasker.presentation.screens.statistics.StatisticsScreen
import com.example.hometasker.presentation.screens.task_detail.TaskDetailScreen
import com.example.hometasker.presentation.screens.task_edit.TaskEditScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Онбординг
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Главный экран
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onNavigateToCreateTask = {
                    navController.navigate(Screen.TaskEdit.createRoute())
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        // Календарь
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onNavigateToCreateTask = { date ->
                    navController.navigate(Screen.TaskEdit.createRoute())
                }
            )
        }

        // Статистика
        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }

        // Настройки
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToCategories = {
                    navController.navigate(Screen.Categories.route)
                }
            )
        }

        // Детали задачи
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.TaskEdit.createRoute(id))
                }
            )
        }

        // Редактирование/создание задачи
        composable(
            route = Screen.TaskEdit.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")?.takeIf { it != -1L }
            TaskEditScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Категории
        composable(Screen.Categories.route) {
            CategoriesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Поиск
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }
    }
}
