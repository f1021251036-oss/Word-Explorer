package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LearningPath
import com.example.data.models.LevelStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun ExplorerMapScreen(viewModel: WordExplorerViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val selectedPath by viewModel.selectedMapPath.collectAsState()
    val levels by viewModel.levelsForCurrentPath.collectAsState()
    val showModal by viewModel.showLevelModal.collectAsState()
    val selectedLevel by viewModel.selectedLevelInfo.collectAsState()

    // Calculate Stage & Level stats
    val completedCount = levels.count { it.status == LevelStatus.COMPLETED }
    val totalStars = levels.sumOf { it.stars }

    Scaffold(
        topBar = {
            ExplorerTopBar(
                profile = profile,
                selectedPath = selectedPath,
                onPathSelected = { viewModel.switchMap(it) },
                onLogoutClick = { viewModel.logout() }
            )
        },
        bottomBar = {
            ExplorerBottomNavBar(
                currentTab = "map",
                onTabSelected = { viewModel.currentAppScreen.value = it }
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Master 3D Isometric Adventure Map
            IsometricWorldMap(
                path = selectedPath,
                levels = levels,
                activeCompanionId = profile?.activeCompanionId ?: "fox",
                onLevelClick = { level ->
                    viewModel.selectLevel(level)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Stage Progress Overview Banner (Floating Top Right)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepJungleGreen.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ $totalStars Stars",
                        color = GoldYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🏆 $completedCount/50",
                        color = ExplorerCream,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Level Detail Modal Sheet
            if (showModal && selectedLevel != null) {
                LevelDetailSheet(
                    level = selectedLevel!!,
                    onStartQuiz = { viewModel.startQuizForSelectedLevel() },
                    onDismiss = { viewModel.dismissLevelModal() }
                )
            }
        }
    }
}
