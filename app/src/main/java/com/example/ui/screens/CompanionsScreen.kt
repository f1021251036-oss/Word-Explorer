package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CompanionAnimal
import com.example.ui.components.ExplorerBottomNavBar
import com.example.ui.components.ExplorerTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun CompanionsScreen(viewModel: WordExplorerViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val selectedPath by viewModel.selectedMapPath.collectAsState()
    val companions by viewModel.companionsList.collectAsState()

    val currentStreak = profile?.currentStreak ?: 0
    val activeCompanionId = profile?.activeCompanionId ?: "fox"

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
                currentTab = "companions",
                onTabSelected = { viewModel.currentAppScreen.value = it }
            )
        },
        containerColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepForestGreen,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SandGold),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🐾 Sahabat Hewan Petualang",
                            fontWeight = FontWeight.Bold,
                            color = SandGold,
                            fontSize = 16.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SandGold
                        ) {
                            Text(
                                text = "🔥 Streak: $currentStreak Hari",
                                color = DeepForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Selesaikan target belajar harian berturut-turut untuk membuka sahabat hewan baru. Sahabat aktif akan menemanimu di Explorer Map!",
                        color = PureWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of Companions
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(companions) { companion ->
                    val isEquipped = companion.id == activeCompanionId
                    val isUnlocked = companion.isUnlocked

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = PureWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isEquipped) 2.5.dp else 1.5.dp,
                            when {
                                isEquipped -> DeepForestGreen
                                isUnlocked -> SageGreen
                                else -> SageGreen.copy(alpha = 0.4f)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isEquipped) 4.dp else 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("companion_${companion.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Emoji Avatar with Badge
                            Box(contentAlignment = Alignment.Center) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isEquipped) SandGold else Cream,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen),
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = companion.emoji, fontSize = 28.sp)
                                    }
                                }

                                if (!isUnlocked) {
                                    Surface(
                                        shape = CircleShape,
                                        color = DeepForestGreen.copy(alpha = 0.65f),
                                        modifier = Modifier.size(56.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = PureWhite, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = companion.name,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) DeepForestGreen else DeepForestGreen.copy(alpha = 0.5f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "${companion.species} • ${companion.trait}",
                                color = SageGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = companion.description,
                                color = DeepForestGreen.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isUnlocked) {
                                Button(
                                    onClick = { viewModel.equipCompanion(companion) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isEquipped) SandGold else DeepForestGreen,
                                        contentColor = if (isEquipped) DeepForestGreen else PureWhite
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                ) {
                                    Text(
                                        text = if (isEquipped) "✓ Aktif" else "Pilih Teman",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isEquipped) DeepForestGreen else PureWhite
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Cream,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Buka: ${companion.requiredStreak} hari streak",
                                        color = DeepForestGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
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
