package com.example.hometasker.presentation.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Default.CheckCircle,
        title = "Управляйте задачами",
        description = "Создавайте задачи, устанавливайте приоритеты и отслеживайте выполнение домашних дел"
    ),
    OnboardingPage(
        icon = Icons.Default.Category,
        title = "Организуйте по категориям",
        description = "Распределяйте задачи по категориям: уборка, готовка, учёба и многое другое"
    ),
    OnboardingPage(
        icon = Icons.Default.Timer,
        title = "Отслеживайте время",
        description = "Засекайте время выполнения задач и анализируйте свою продуктивность"
    ),
    OnboardingPage(
        icon = Icons.Default.Notifications,
        title = "Не забывайте о делах",
        description = "Настройте напоминания, чтобы не пропустить важные задачи"
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Кнопка пропустить
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { viewModel.completeOnboarding(onFinish) }
            ) {
                Text("Пропустить")
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPageContent(onboardingPages[page])
        }

        // Индикаторы
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(onboardingPages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        // Кнопка продолжить/начать
        Button(
            onClick = {
                if (pagerState.currentPage == onboardingPages.size - 1) {
                    viewModel.completeOnboarding(onFinish)
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (pagerState.currentPage == onboardingPages.size - 1) "Начать"
                else "Далее"
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
