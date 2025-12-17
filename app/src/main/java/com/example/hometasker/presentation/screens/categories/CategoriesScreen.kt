package com.example.hometasker.presentation.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hometasker.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Категории") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.categories.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет категорий")
                }
            }

            else -> {
                // Группируем категории: корневые и подкатегории
                val rootCategories = uiState.categories.filter { it.parentId == null }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rootCategories) { rootCategory ->
                        CategoryItem(
                            category = rootCategory,
                            onDelete = {
                                if (!rootCategory.isDefault) {
                                    viewModel.onDeleteCategory(rootCategory)
                                }
                            },
                            isRoot = true
                        )

                        // Подкатегории
                        val subcategories = uiState.categories.filter { it.parentId == rootCategory.id }
                        subcategories.forEach { subcategory ->
                            CategoryItem(
                                category = subcategory,
                                onDelete = {
                                    if (!subcategory.isDefault) {
                                        viewModel.onDeleteCategory(subcategory)
                                    }
                                },
                                isRoot = false,
                                modifier = Modifier.padding(start = 24.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onDelete: () -> Unit,
    isRoot: Boolean,
    modifier: Modifier = Modifier
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (isRoot) 24.dp else 16.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = if (isRoot) MaterialTheme.typography.titleMedium
                    else MaterialTheme.typography.bodyMedium
                )
                if (category.isDefault) {
                    Text(
                        text = "По умолчанию",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!category.isDefault) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
