package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ExplorationRegion
import com.example.data.models.LevelInfo
import com.example.data.models.LevelStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDetailSheet(
    region: ExplorationRegion,
    isUnlocked: Boolean,
    levels: List<LevelInfo>,
    onEnterRegion: (ExplorationRegion) -> Unit,
    onDismiss: () -> Unit
) {
    val stageLevels = levels.filter { it.stageNumber == region.stageNumber }
    val completedCount = stageLevels.count { it.status == LevelStatus.COMPLETED }
    val totalStars = stageLevels.sumOf { it.stars }
    val isAllCompleted = stageLevels.isNotEmpty() && completedCount == stageLevels.size

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Cream,
        dragHandle = { BottomSheetDefaults.DragHandle(color = DeepForestGreen) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header Row: Emoji badge, Title, Status & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepForestGreen
                    ) {
                        Text(
                            text = "${region.iconEmoji} Stage ${region.stageNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            !isUnlocked -> SageGreen.copy(alpha = 0.5f)
                            isAllCompleted -> DeepForestGreen
                            else -> SandGold
                        }
                    ) {
                        Text(
                            text = when {
                                !isUnlocked -> "LOCKED"
                                isAllCompleted -> "COMPLETED"
                                else -> "ACTIVE"
                            },
                            color = when {
                                !isUnlocked -> PureWhite
                                isAllCompleted -> SandGold
                                else -> DeepForestGreen
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_region_sheet")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepForestGreen)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Region Name & Subtitle
            Text(
                text = region.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen
            )
            Text(
                text = region.subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = DeepForestGreen.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Thematic Description
            Text(
                text = region.description,
                style = MaterialTheme.typography.bodyMedium,
                color = DeepForestGreen,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Progression Summary Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PureWhite,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Levels Selesai", color = SageGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("$completedCount / 10", color = DeepForestGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bintang Wilayah", color = SageGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$totalStars / 30", color = DeepForestGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Status", color = SageGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (isUnlocked) "Terbuka" else "Terkunci",
                            color = if (isUnlocked) DeepForestGreen else SemanticRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action: Enter Area or Show Locked Notice
            if (isUnlocked) {
                Button(
                    onClick = { onEnterRegion(region) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("enter_region_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepForestGreen,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Masuki Wilayah ${region.name}",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = DeepForestGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Selesaikan seluruh 10 level pada wilayah sebelumnya untuk membuka ${region.name}.",
                            color = DeepForestGreen,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
