package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.LearningPath
import com.example.data.models.LevelStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun DashboardScreen(viewModel: WordExplorerViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val levels by viewModel.levelsForCurrentPath.collectAsState()
    val selectedPath by viewModel.selectedMapPath.collectAsState()

    // Daily Limit & Daily Vocabulary Tracking
    val dailyLevelsCompleted by viewModel.dailyCompletedLevelsCount.collectAsState()
    val dailyResetCountdown by viewModel.timeUntilMidnightReset.collectAsState()
    val dailyAssignments by viewModel.dailyVocabAssignments.collectAsState()

    val totalAssignedVocab = if (dailyAssignments.isNotEmpty()) {
        dailyAssignments.size
    } else {
        when (selectedPath) {
            LearningPath.LOW -> 25
            LearningPath.MIDDLE -> 35
            LearningPath.HIGH -> 40
        }
    }
    val completedVocabCount = dailyAssignments.count { it.status == "MASTERED" || it.status == "LEARNING" }
    val masteredVocabCount = dailyAssignments.count { it.status == "MASTERED" }
    val remainingDailyLevels = (3 - dailyLevelsCompleted).coerceAtLeast(0)

    val completedLevels = levels.count { it.status == LevelStatus.COMPLETED }
    val totalStars = levels.sumOf { it.stars }
    val nextAvailableLevel = levels.firstOrNull { it.status == LevelStatus.AVAILABLE }
        ?: levels.firstOrNull()

    val currentChapterName = when (nextAvailableLevel?.stageNumber ?: 1) {
        1 -> "Chapter 1 — Whispering Woods"
        2 -> "Chapter 2 — Granite Peaks"
        3 -> "Chapter 3 — Sunken Valley"
        4 -> "Chapter 4 — Shifting Sands"
        5 -> "Chapter 5 — Arctic Frontier"
        else -> "Chapter 1 — Whispering Woods"
    }

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
                currentTab = "dashboard",
                onTabSelected = { viewModel.currentAppScreen.value = it }
            )
        },
        containerColor = Cream
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Subtle Perimeter Surveyor Border & Grain Simulation
            Canvas(modifier = Modifier.fillMaxSize()) {
                val margin = 10.dp.toPx()
                val w = size.width
                val h = size.height

                // Double inked surveyor border in Sage Green
                drawRect(
                    color = SageGreen.copy(alpha = 0.35f),
                    topLeft = Offset(margin, margin),
                    size = androidx.compose.ui.geometry.Size(w - 2 * margin, h - 2 * margin),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                    )
                )
                // Corner surveyor crosshairs
                val cross = 12.dp.toPx()
                drawLine(SageGreen.copy(alpha = 0.6f), Offset(margin - cross / 2, margin), Offset(margin + cross / 2, margin), 1.5.dp.toPx())
                drawLine(SageGreen.copy(alpha = 0.6f), Offset(margin, margin - cross / 2), Offset(margin, margin + cross / 2), 1.5.dp.toPx())
                drawLine(SageGreen.copy(alpha = 0.6f), Offset(w - margin - cross / 2, margin), Offset(w - margin + cross / 2, margin), 1.5.dp.toPx())
                drawLine(SageGreen.copy(alpha = 0.6f), Offset(w - margin, margin - cross / 2), Offset(w - margin, margin + cross / 2), 1.5.dp.toPx())
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // =========================================================================
                // 1. ADVENTURER DOSSIER SHEET (Tilted -1.2° with Hand-Inked Brass Frame)
                // =========================================================================
                ParchmentDossierCard(
                    rotation = -1.2f,
                    backgroundColor = PureWhite,
                    borderColor = SageGreen
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brass Compass Enclosed Avatar
                        Box(contentAlignment = Alignment.Center) {
                            Surface(
                                shape = CircleShape,
                                color = SandGold,
                                border = androidx.compose.foundation.BorderStroke(2.5.dp, DeepForestGreen),
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(68.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.char_adventurer_explorer_1793116866013),
                                    contentDescription = "Adventurer Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile?.displayName ?: "Explorer",
                                    fontFamily = CinzelFontFamily,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestGreen
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                WaxSealBadge(
                                    badgeSize = 28.dp,
                                    label = "",
                                    icon = {
                                        ExpeditionIconStarSeal(
                                            modifier = Modifier.size(14.dp),
                                            tint = DeepForestGreen,
                                            accentTint = SandGold
                                        )
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ExpeditionIconCompass(modifier = Modifier.size(13.dp), tint = SageGreen)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${profile?.currentPath?.title ?: "Beginner"} • ${profile?.currentPath?.cefrLabel}",
                                    fontFamily = CinzelFontFamily,
                                    color = DeepForestGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Daily Target: $totalAssignedVocab words • Max 3 waypoints/day",
                                fontFamily = JakartaFontFamily,
                                color = DeepForestGreen.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // =========================================================================
                // 2. TODAY'S LEARNING PROGRESS (SIGNATURE EXPEDITION LOG)
                // =========================================================================
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DeepForestGreen,
                    border = androidx.compose.foundation.BorderStroke(2.dp, SandGold),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_progress_dashboard_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Dossier Header Tab
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ExpeditionIconTent(modifier = Modifier.size(20.dp), tint = SandGold, accentTint = PureWhite)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TODAY'S EXPEDITION LOG",
                                    fontFamily = CinzelFontFamily,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SandGold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Brass Pocketwatch Reset Timer
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SandGold,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DeepForestGreen)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ExpeditionIconLantern(modifier = Modifier.size(13.dp), tint = DeepForestGreen, accentTint = PureWhite)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = dailyResetCountdown,
                                        fontFamily = JakartaFontFamily,
                                        color = DeepForestGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Asymmetrical Dual-Insert Panels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Left Panel: New Vocabulary
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PureWhite,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ExpeditionIconBook(modifier = Modifier.size(14.dp), tint = DeepForestGreen)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("VOCABULARY", fontFamily = CinzelFontFamily, fontSize = 10.sp, color = SageGreen, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$totalAssignedVocab Words",
                                        fontFamily = CinzelFontFamily,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepForestGreen
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ExpeditionIconCheckmark(modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("$completedVocabCount Active", fontSize = 11.sp, color = DeepForestGreen, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("Mastered: $masteredVocabCount", fontSize = 11.sp, color = DeepForestGreen.copy(alpha = 0.85f))
                                }
                            }

                            // Right Panel: Daily Limit Winding Trail
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PureWhite,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (dailyLevelsCompleted >= 3) SandGold else SageGreen
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ExpeditionIconMap(modifier = Modifier.size(14.dp), tint = DeepForestGreen)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("DAILY WAYPOINTS", fontFamily = CinzelFontFamily, fontSize = 10.sp, color = SageGreen, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$dailyLevelsCompleted / 3 Completed",
                                        fontFamily = CinzelFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (dailyLevelsCompleted >= 3) DeepForestGreen else DeepForestGreen
                                    )

                                    // Winding Trail Waypoint Progress
                                    HandDrawnWindingTrail(
                                        completedSteps = dailyLevelsCompleted,
                                        totalSteps = 3,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Text(
                                        text = if (remainingDailyLevels == 0) "Daily Limit Reached ✓" else "$remainingDailyLevels Waypoints Left",
                                        fontSize = 11.sp,
                                        color = if (remainingDailyLevels == 0) Color(0xFF2D5A27) else DeepForestGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Current Chapter Inked Footer Strip
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PureWhite.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "CURRENT EXPEDITION REGION",
                                        fontFamily = CinzelFontFamily,
                                        fontSize = 9.sp,
                                        color = Cream.copy(alpha = 0.85f),
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = currentChapterName,
                                        fontFamily = CinzelFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "ACTIVE STAGE",
                                        fontFamily = CinzelFontFamily,
                                        fontSize = 9.sp,
                                        color = Cream.copy(alpha = 0.85f)
                                    )
                                    Text(
                                        text = "Level ${nextAvailableLevel?.id ?: "1-1"}",
                                        fontFamily = CinzelFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SandGold
                                    )
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 3. PRIMARY ACTION: TACTILE SIGNPOST BUTTON
                // =========================================================================
                TactileSignpostButton(
                    onClick = {
                        nextAvailableLevel?.let {
                            viewModel.selectLevel(it)
                            viewModel.currentAppScreen.value = "map"
                        }
                    },
                    label = if (dailyLevelsCompleted >= 3) "View Expedition Map" else "Continue Expedition",
                    subLabel = if (dailyLevelsCompleted >= 3) "3 Waypoints done today • Reset in $dailyResetCountdown" else "March to ${nextAvailableLevel?.title ?: "Stage 1-1"}",
                    testTag = "continue_adventure_button",
                    icon = {
                        ExpeditionIconCompass(
                            modifier = Modifier.size(24.dp),
                            tint = PureWhite,
                            accentTint = SandGold
                        )
                    }
                )

                // =========================================================================
                // 4. STAMPED WAX MEDAL STATS ROW (Asymmetrical Rotations +1° & -1°)
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Streak Card (Tilted +1.2°)
                    ParchmentDossierCard(
                        rotation = 1.2f,
                        backgroundColor = PureWhite,
                        borderColor = SageGreen,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WaxSealBadge(
                                badgeSize = 32.dp,
                                label = "",
                                icon = {
                                    ExpeditionIconStreakFlame(
                                        modifier = Modifier.size(16.dp),
                                        tint = DeepForestGreen,
                                        accentTint = SandGold
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "STREAK",
                                fontFamily = CinzelFontFamily,
                                color = SageGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${profile?.currentStreak ?: 0} Days",
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DeepForestGreen
                        )
                        Text(
                            text = "Best: ${profile?.bestStreak ?: 0} days record",
                            fontFamily = JakartaFontFamily,
                            color = DeepForestGreen.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }

                    // Total Stars Card (Tilted -1.2°)
                    ParchmentDossierCard(
                        rotation = -1.2f,
                        backgroundColor = PureWhite,
                        borderColor = SageGreen,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WaxSealBadge(
                                badgeSize = 32.dp,
                                label = "",
                                icon = {
                                    ExpeditionIconStarSeal(
                                        modifier = Modifier.size(16.dp),
                                        tint = DeepForestGreen,
                                        accentTint = SandGold
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "STARS",
                                fontFamily = CinzelFontFamily,
                                color = SageGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalStars / 150",
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DeepForestGreen
                        )
                        Text(
                            text = "$completedLevels / 50 levels charted",
                            fontFamily = JakartaFontFamily,
                            color = DeepForestGreen.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }
                }

                // =========================================================================
                // 5. WORLD EXPEDITION ROUTE (50 Levels — Charted Path)
                // =========================================================================
                ParchmentDossierCard(
                    rotation = 0.6f,
                    backgroundColor = PureWhite,
                    borderColor = SageGreen
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPEDITION WORLD ROUTE",
                            fontFamily = CinzelFontFamily,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen,
                            letterSpacing = 0.5.sp
                        )
                        WaxSealBadge(
                            badgeSize = 28.dp,
                            label = "${completedLevels}/50",
                            icon = {}
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "5 Chapters × 10 Levels = 50 Waypoints. Daily 3-level pace maintains long-term fluency.",
                        fontFamily = JakartaFontFamily,
                        fontSize = 12.sp,
                        color = DeepForestGreen.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Inked Winding Trail for World Progression
                    HandDrawnWindingTrail(
                        completedSteps = ((completedLevels.toFloat() / 50f) * 5f).toInt(),
                        totalSteps = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Woods", fontFamily = CinzelFontFamily, fontSize = 10.sp, color = SageGreen)
                        Text(text = "Peaks", fontFamily = CinzelFontFamily, fontSize = 10.sp, color = SageGreen)
                        Text(text = "Valley", fontFamily = CinzelFontFamily, fontSize = 10.sp, color = SageGreen)
                        Text(text = "Sands", fontFamily = CinzelFontFamily, fontSize = 10.sp, color = SageGreen)
                        Text(text = "Frontier", fontFamily = CinzelFontFamily, fontSize = 10.sp, color = DeepForestGreen, fontWeight = FontWeight.Bold)
                    }
                }

                // =========================================================================
                // 6. SURVEYOR'S ACTIVE LEARNING PATH SELECTOR
                // =========================================================================
                ParchmentDossierCard(
                    rotation = -0.5f,
                    backgroundColor = PureWhite,
                    borderColor = SageGreen
                ) {
                    Text(
                        text = "ACTIVE EXPEDITION CADENCE",
                        fontFamily = CinzelFontFamily,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LearningPath.values().forEach { path ->
                        val isSelected = profile?.currentPath == path
                        Surface(
                            onClick = { viewModel.changeUserCurrentPath(path) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Cream else PureWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) DeepForestGreen else SageGreen.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Wax Stamp Checkbox
                                WaxSealBadge(
                                    badgeSize = 28.dp,
                                    label = "",
                                    icon = {
                                        if (isSelected) {
                                            ExpeditionIconCheckmark(modifier = Modifier.size(14.dp))
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${path.title} (${path.cefrLabel})",
                                        fontFamily = CinzelFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepForestGreen,
                                        fontSize = 14.sp
                                    )
                                    val targetWords = when (path) {
                                        LearningPath.LOW -> 25
                                        LearningPath.MIDDLE -> 35
                                        LearningPath.HIGH -> 40
                                    }
                                    Text(
                                        text = "$targetWords words daily • ${path.uiLanguage}",
                                        fontFamily = JakartaFontFamily,
                                        color = DeepForestGreen.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
