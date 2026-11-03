package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
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
    val showRegionModal by viewModel.showRegionModal.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()

    // Daily Limit & Reset Countdown state
    val dailyCompletedCount by viewModel.dailyCompletedLevelsCount.collectAsState()
    val timeUntilReset by viewModel.timeUntilMidnightReset.collectAsState()
    val dailyLimitError by viewModel.dailyLimitErrorMessage.collectAsState()
    val isDailyLimitReached = dailyCompletedCount >= 3

    // Calculate Stage & Level stats
    val completedCount = levels.count { it.status == LevelStatus.COMPLETED }
    val totalStars = levels.sumOf { it.stars }

    Scaffold(
        topBar = {
            ExplorerTopBar(
                profile = profile,
                selectedPath = selectedPath,
                onPathSelected = { viewModel.switchMap(it) },
                onLogoutClick = { viewModel.logout() },
                isSoundEnabled = isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() }
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
            // Master 3D Adventure Island Map
            IsometricWorldMap(
                path = selectedPath,
                levels = levels,
                activeCompanionId = profile?.activeCompanionId ?: "fox",
                onLevelClick = { level ->
                    viewModel.selectLevel(level)
                },
                onRegionClick = { region ->
                    viewModel.selectRegion(region)
                },
                isRegionUnlocked = { stageNumber ->
                    viewModel.isRegionUnlocked(stageNumber)
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

            // Daily Adventure Limit Persistent Banner (Floating Top Center when 3 levels reached)
            AnimatedVisibility(
                visible = isDailyLimitReached,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp, start = 16.dp, end = 120.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xF21E362A),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldYellow),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Batas Hari Ini: 3/3 Level Selesai! 🎉",
                                color = GoldYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = ExplorerCream, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Reset dalam: $timeUntilReset",
                                    color = ExplorerCream,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Region Detail Modal Sheet
            if (showRegionModal && selectedRegion != null) {
                RegionDetailSheet(
                    region = selectedRegion!!,
                    isUnlocked = viewModel.isRegionUnlocked(selectedRegion!!.stageNumber),
                    levels = levels,
                    onEnterRegion = { reg ->
                        viewModel.dismissRegionModal()
                        val targetLevel = levels.firstOrNull { it.stageNumber == reg.stageNumber && it.status == LevelStatus.AVAILABLE }
                            ?: levels.firstOrNull { it.stageNumber == reg.stageNumber }
                        targetLevel?.let { viewModel.selectLevel(it) }
                    },
                    onDismiss = { viewModel.dismissRegionModal() }
                )
            }

            // Level Detail Modal Sheet
            if (showModal && selectedLevel != null) {
                LevelDetailSheet(
                    level = selectedLevel!!,
                    isDailyLimitReached = isDailyLimitReached,
                    timeUntilReset = timeUntilReset,
                    dailyLimitError = dailyLimitError,
                    onStartQuiz = { viewModel.startQuizForSelectedLevel() },
                    onDismiss = { viewModel.dismissLevelModal() }
                )
            }
        }
    }
}
