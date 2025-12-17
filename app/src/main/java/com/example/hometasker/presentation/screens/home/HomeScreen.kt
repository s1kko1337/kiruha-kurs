package com.example.hometasker.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hometasker.presentation.components.EmptyTasksState
import com.example.hometasker.presentation.components.TaskCard
import com.example.hometasker.presentation.components.TaskFilterChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTaskDetail: (Long) -> Unit,
    onNavigateToCreateTask: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Задачи") },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить задачу"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Фильтры
            TaskFilterChips(
                selectedFilter = uiState.filter,
                onFilterSelected = viewModel::onFilterChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Контент
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = !uiState.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    when {
                        uiState.error != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.error ?: "Ошибка",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        uiState.tasks.isEmpty() -> {
                            EmptyTasksState()
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = uiState.tasks,
                                    key = { it.id }
                                ) { task ->
                                    TaskCard(
                                        task = task,
                                        onTaskClick = { onNavigateToTaskDetail(task.id) },
                                        onCheckedChange = { isCompleted ->
                                            viewModel.onTaskCheckedChange(task.id, isCompleted)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Индикатор загрузки при первой загрузке
                if (uiState.isLoading && uiState.tasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
