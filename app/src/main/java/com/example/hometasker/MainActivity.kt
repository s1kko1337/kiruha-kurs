package com.example.hometasker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.hometasker.domain.repository.SettingsRepository
import com.example.hometasker.presentation.MainScreen
import com.example.hometasker.presentation.navigation.Screen
import com.example.hometasker.presentation.theme.HomeTaskerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isOnboardingCompleted by settingsRepository.isOnboardingCompleted()
                .collectAsState(initial = true)

            HomeTaskerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        startDestination = if (isOnboardingCompleted) {
                            Screen.Home.route
                        } else {
                            Screen.Onboarding.route
                        }
                    )
                }
            }
        }
    }
}
