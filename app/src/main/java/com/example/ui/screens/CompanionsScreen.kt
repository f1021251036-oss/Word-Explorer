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
            // Compact Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sahabat Hewan",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Forest900,
                        fontSize = 19.sp
                    )
                    Text(
                        text = "Aktifkan hewan untuk menemanimu di peta.",
                        color = SageDark,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Honey,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Forest900)
                ) {
                    Text(
                        text = "🔥 $currentStreak Hari",
                        color = Forest900,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                                color = if (isUnlocked) Forest900 else SageDark,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "${companion.species} • ${companion.trait}",
                                color = Terracotta,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = companion.description,
                                color = SageDark,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isUnlocked) {
                                Button(
                                    onClick = { viewModel.equipCompanion(companion) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isEquipped) LeafTint else Forest700,
                                        contentColor = if (isEquipped) Leaf else PureWhite
                                    ),
                                    border = if (isEquipped) androidx.compose.foundation.BorderStroke(1.5.dp, Leaf) else null,
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 44.dp)
                                ) {
                                    Text(
                                        text = if (isEquipped) "✓ Aktif" else "Pilih",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isEquipped) Leaf else PureWhite
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CreamSoft,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Sage.copy(alpha = 0.7f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Streak ${companion.requiredStreak} hari",
                                        color = Forest900,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
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
