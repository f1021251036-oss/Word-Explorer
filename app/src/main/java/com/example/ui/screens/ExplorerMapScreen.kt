package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // First-time map guide state
    val showFirstTimeGuide by viewModel.showFirstTimeGuide.collectAsState()
    val firstTimeGuideStep by viewModel.firstTimeGuideStep.collectAsState()
    val isNextStepChipDismissed by viewModel.isNextStepChipDismissed.collectAsState()

    // Calculate Stage & Level stats
    val completedCount = levels.count { it.status == LevelStatus.COMPLETED }
    val totalStars = levels.sumOf { it.stars }
    val firstPlayableLevel = levels.lastOrNull { it.status == LevelStatus.AVAILABLE }
        ?: levels.firstOrNull()

    BackHandler(enabled = showFirstTimeGuide) {
        viewModel.completeFirstTimeGuide(openFirstLevel = false)
    }

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
        containerColor = DeepForestGreen
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Master Adventure Island Map
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
                onReplayGuide = { viewModel.replayMapGuide() },
                modifier = Modifier.fillMaxSize()
            )

            // Stage Progress Overview Banner (Floating Top Right Parchment Badge)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PureWhite,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Forest700),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ $totalStars",
                        color = Forest900,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🏆 $completedCount/50",
                        color = Forest900,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Daily Adventure Limit Persistent Banner (Positioned cleanly below top HUD bar)
            AnimatedVisibility(
                visible = isDailyLimitReached,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 56.dp, start = 16.dp, end = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Forest700),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Forest700, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Batas Hari Ini: 3/3 Level Selesai!",
                                color = Forest900,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = SageDark, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Reset dalam: $timeUntilReset",
                                    color = SageDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // First-Time Next-Step Action Chip (helps new explorers know what to tap first)
            AnimatedVisibility(
                visible = !showFirstTimeGuide &&
                    !isNextStepChipDismissed &&
                    completedCount == 0 &&
                    !showModal &&
                    !showRegionModal &&
                    !isDailyLimitReached &&
                    firstPlayableLevel != null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 56.dp, start = 14.dp, end = 14.dp)
            ) {
                firstPlayableLevel?.let { levelToPlay ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = HoneyTint,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, HoneyPressed),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .widthIn(max = 380.dp)
                            .testTag("next_step_guide_chip")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.selectLevel(levelToPlay) }
                                    .padding(horizontal = 6.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Honey,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Forest900),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Forest900,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mulai di sini: Ketuk untuk buka Level ${levelToPlay.stageNumber}-${levelToPlay.levelNumber}",
                                    color = Forest900,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            IconButton(
                                onClick = { viewModel.dismissNextStepHint() },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("dismiss_next_step_chip")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup petunjuk awal",
                                    tint = Forest900,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // First-Time 3-Step Guided Tour Overlay
            if (showFirstTimeGuide) {
                FirstTimeMapGuideOverlay(
                    step = firstTimeGuideStep,
                    firstLevelLabel = firstPlayableLevel?.let { "${it.stageNumber}-${it.levelNumber}" } ?: "1-1",
                    onSkip = { viewModel.completeFirstTimeGuide(openFirstLevel = false) },
                    onNext = { viewModel.advanceFirstTimeGuideStep() },
                    onStartFirstLevel = { viewModel.completeFirstTimeGuide(openFirstLevel = true) }
                )
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

@Composable
private fun FirstTimeMapGuideOverlay(
    step: Int,
    firstLevelLabel: String,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onStartFirstLevel: () -> Unit
) {
    val (title, body, iconTint, iconBg) = when (step) {
        0 -> Tuple4(
            "Langkah 1: Mulai Misi Pertamamu",
            "Ketuk tombol PLAY atau angka level emas di peta untuk membuka Level $firstLevelLabel. Kamu bisa menyelesaikan hingga 3 level baru setiap hari.",
            Forest900,
            HoneyTint
        )
        1 -> Tuple4(
            "Langkah 2: Jelajahi Peta & Legenda",
            "Pilih wilayah lewat bilah atas, cubit atau pakai tombol kanan bawah untuk memperbesar, dan ketuk Legenda di kiri bawah untuk melihat arti tiap ikon level.",
            Lagoon,
            LagoonTint
        )
        else -> Tuple4(
            "Langkah 3: Camp, Lexicon, Lore & Beasts",
            "Gunakan menu bawah untuk memantau target harian di Camp, melatih pelafalan kata di Lexicon, membaca Lore, dan membuka Beasts lewat streak harian.",
            Leaf,
            LeafTint
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ShadowDark.copy(alpha = 0.58f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(2.dp, Forest700),
            shadowElevation = 12.dp,
            modifier = Modifier
                .widthIn(max = 360.dp)
                .fillMaxWidth()
                .testTag("first_time_guide_card")
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HoneyTint,
                        border = androidx.compose.foundation.BorderStroke(1.dp, HoneyPressed)
                    ) {
                        Text(
                            text = "PANDUAN ${step + 1} / 3",
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Forest900,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("guide_skip_button")
                    ) {
                        Text(
                            text = "Lewati",
                            color = SageDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = iconBg,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, iconTint),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (step) {
                                    0 -> Icons.Default.PlayArrow
                                    1 -> Icons.Default.Map
                                    else -> Icons.Default.Explore
                                },
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Text(
                        text = title,
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = Forest900,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = body,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = SageDark
                )

                // Step progress dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (i == step) 22.dp else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (i == step) HoneyPressed else DisabledBg)
                        )
                    }
                }

                // Action Row
                if (step < 2) {
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Honey,
                            contentColor = Forest900
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Forest900),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("guide_next_button")
                    ) {
                        Text(
                            text = "Lanjut (${step + 1}/3)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Forest900
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSkip,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Forest700),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Forest900),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("guide_close_button")
                        ) {
                            Text(
                                text = "Nanti",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = onStartFirstLevel,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Honey,
                                contentColor = Forest900
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Forest900),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("guide_start_level_button")
                        ) {
                            Text(
                                text = "Mulai Level $firstLevelLabel!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Forest900
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
