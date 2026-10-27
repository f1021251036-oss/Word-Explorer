package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.LearningPath
import com.example.data.models.LevelStatus
import com.example.ui.components.ExplorerBottomNavBar
import com.example.ui.components.ExplorerTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun DashboardScreen(viewModel: WordExplorerViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val levels by viewModel.levelsForCurrentPath.collectAsState()
    val selectedPath by viewModel.selectedMapPath.collectAsState()

    val completedLevels = levels.count { it.status == LevelStatus.COMPLETED }
    val totalStars = levels.sumOf { it.stars }
    val nextAvailableLevel = levels.firstOrNull { it.status == LevelStatus.AVAILABLE }
        ?: levels.firstOrNull()

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
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Explorer Profile Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AdventureOrange,
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, GoldYellow),
                        shadowElevation = 6.dp,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.char_adventurer_explorer_1793116866013),
                            contentDescription = "Adventurer Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = profile?.displayName ?: "Explorer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Jalur: ${profile?.currentPath?.title ?: "Beginner"} (${profile?.currentPath?.cefrLabel})",
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Bahasa Antarmuka: ${profile?.currentPath?.uiLanguage}",
                            color = ExplorerCream.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Primary Action: Continue Adventure Button
            Button(
                onClick = {
                    nextAvailableLevel?.let {
                        viewModel.selectLevel(it)
                        viewModel.currentAppScreen.value = "map"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("continue_adventure_button")
            ) {
                Icon(Icons.Default.Explore, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Lanjutkan Petualangan",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Menuju ${nextAvailableLevel?.title ?: "Stage 1-1"}",
                        fontSize = 11.sp,
                        color = ExplorerCream
                    )
                }
            }

            // Daily Target & Streak Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Streak Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Daily Streak", color = ExplorerCream, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${profile?.currentStreak ?: 0} Hari",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = GoldYellow
                        )
                        Text(
                            text = "Terbaik: ${profile?.bestStreak ?: 0} hari",
                            color = ExplorerCream.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Daily Words Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Target Harian", color = ExplorerCream, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${profile?.dailyTarget ?: 10} Kata",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color(0xFFA5D6A7)
                        )
                        Text(
                            text = "Per hari aktif",
                            color = ExplorerCream.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // World Progression Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Kemajuan Dunia (World Progress)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐ Total Bintang", color = ExplorerCream.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("$totalStars / 150", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏆 Level Selesai", color = ExplorerCream.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("$completedLevels / 50", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🦊 Sahabat Aktif", color = ExplorerCream.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text(
                                text = when (profile?.activeCompanionId) {
                                    "parrot" -> "Echo 🦜"
                                    "otter" -> "Splash 🦦"
                                    "fennec" -> "Zephyr 🐾"
                                    "hawk" -> "Sol 🦅"
                                    "griffin" -> "Aurelius 🦁"
                                    "phoenix" -> "Ignis 🔥"
                                    "dragon" -> "Azure 🐉"
                                    else -> "Rusty 🦊"
                                },
                                color = ExplorerCream,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { completedLevels / 50f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = ForestGreen,
                        trackColor = Color(0xFF285434)
                    )
                }
            }

            // Learning Path Switcher Section
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Pilih Jalur Pembelajaran Aktif",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LearningPath.values().forEach { path ->
                        val isSelected = profile?.currentPath == path
                        Surface(
                            onClick = { viewModel.changeUserCurrentPath(path) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF285434) else Color(0xFF1E4637),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) GoldYellow else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.changeUserCurrentPath(path) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = GoldYellow,
                                        unselectedColor = ExplorerCream
                                    )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${path.title} (${path.cefrLabel})",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${path.targetWordsPerDay} kata/hari • ${path.uiLanguage}",
                                        color = ExplorerCream.copy(alpha = 0.8f),
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
