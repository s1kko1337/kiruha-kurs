package com.example.hometasker.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hometasker.domain.model.Category

@Composable
fun CategoryChip(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    AssistChip(
        onClick = onClick,
        label = { Text(category.name) },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = categoryColor.copy(alpha = 0.1f),
            labelColor = categoryColor
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = categoryColor.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun CategoryFilterChip(
    category: Category,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = { Text(category.name) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = categoryColor.copy(alpha = 0.2f),
            selectedLabelColor = categoryColor
        )
    )
}
