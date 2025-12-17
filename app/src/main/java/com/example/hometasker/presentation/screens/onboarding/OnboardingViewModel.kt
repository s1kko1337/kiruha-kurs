package com.example.hometasker.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hometasker.domain.repository.CategoryRepository
import com.example.hometasker.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    fun onPageChange(page: Int) {
        _currentPage.value = page
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Вставляем категории по умолчанию
            categoryRepository.insertDefaultCategories()
            // Отмечаем онбординг как завершённый
            settingsRepository.setOnboardingCompleted(true)
            onComplete()
        }
    }
}
