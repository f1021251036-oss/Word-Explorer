package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LevelInfo
import com.example.data.models.LevelStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelDetailSheet(
    level: LevelInfo,
    isDailyLimitReached: Boolean = false,
    timeUntilReset: String = "",
    dailyLimitError: String? = null,
    onStartQuiz: () -> Unit,
    onDismiss: () -> Unit
) {
    val isCompleted = level.status == LevelStatus.COMPLETED
    val isLockedByDailyLimit = !isCompleted && isDailyLimitReached

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DeepJungleGreen,
        dragHandle = { BottomSheetDefaults.DragHandle(color = ExplorerCream) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AdventureOrange
                ) {
                    Text(
                        text = "Level ${level.id}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_level_sheet")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = ExplorerCream)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title & Status
            Text(
                text = level.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Star Rating Display
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (s in 1..3) {
                    val earned = s <= level.stars
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star $s",
                        tint = if (earned) GoldYellow else Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (level.stars > 0) "${level.stars}/3 Stars Earned" else "Not yet completed",
                    color = ExplorerCream,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Vocabulary Section
            Text(
                text = "Target Vocabulary:",
                style = MaterialTheme.typography.labelLarge,
                color = ExplorerCream,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                level.targetWords.forEach { word ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF285434),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen)
                    ) {
                        Text(
                            text = "📖 $word",
                            color = ExplorerCream,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quiz Info
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E4637),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Questions", color = ExplorerCream.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("10 Questions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Remedial", color = ExplorerCream.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("Adaptive", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Max Stars", color = ExplorerCream.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("⭐⭐⭐", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Daily Adventure Limit Warning Banner (3 levels per day limit)
            if (isLockedByDailyLimit || dailyLimitError != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x33FFA000),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Batas Petualangan Harian Tercapai (3/3)",
                                color = GoldYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "You've completed today's 3 adventure levels! Come back tomorrow to continue your journey.",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = ExplorerCream, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reset harian berikutnya dalam: $timeUntilReset",
                                color = ExplorerCream,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Start Quiz Button
            Button(
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_quiz_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLockedByDailyLimit) Color.Gray else AdventureOrange
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = level.status != LevelStatus.LOCKED && !isLockedByDailyLimit
            ) {
                Icon(
                    imageVector = if (isLockedByDailyLimit) Icons.Default.Lock else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isLockedByDailyLimit -> "Batas Harian Tercapai (3/3 Selesai)"
                        isCompleted -> "Latihan Ulang Level (Review)"
                        else -> "Mulai Kuis Petualangan"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
