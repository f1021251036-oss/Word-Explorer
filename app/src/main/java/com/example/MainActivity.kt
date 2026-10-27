package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.WordExplorerTheme
import com.example.ui.viewmodel.WordExplorerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordExplorerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WordExplorerApp()
                }
            }
        }
    }
}

@Composable
fun WordExplorerApp(viewModel: WordExplorerViewModel = viewModel()) {
    val currentScreen by viewModel.currentAppScreen.collectAsState()

    // Handle system back navigation cleanly
    BackHandler(
        enabled = currentScreen in listOf("dashboard", "vocab", "stories", "companions", "quiz")
    ) {
        if (currentScreen == "quiz") {
            viewModel.exitQuizToMap()
        } else {
            viewModel.currentAppScreen.value = "map"
        }
    }

    when (currentScreen) {
        "auth" -> AuthScreen(viewModel = viewModel)
        "onboarding" -> OnboardingScreen(viewModel = viewModel)
        "questionnaire" -> QuestionnaireScreen(viewModel = viewModel)
        "diagnostic" -> DiagnosticScreen(viewModel = viewModel)
        "map" -> ExplorerMapScreen(viewModel = viewModel)
        "quiz" -> QuizScreen(viewModel = viewModel)
        "dashboard" -> DashboardScreen(viewModel = viewModel)
        "vocab" -> VocabularyScreen(viewModel = viewModel)
        "stories" -> StoriesScreen(viewModel = viewModel)
        "companions" -> CompanionsScreen(viewModel = viewModel)
        else -> ExplorerMapScreen(viewModel = viewModel)
    }
}
