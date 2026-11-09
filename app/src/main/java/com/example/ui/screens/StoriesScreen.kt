package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.AdventureStory
import com.example.data.seed.InitialDataSeed
import com.example.ui.components.ExplorerBottomNavBar
import com.example.ui.components.ExplorerTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun StoriesScreen(viewModel: WordExplorerViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val selectedPath by viewModel.selectedMapPath.collectAsState()
    val completedStories by viewModel.storyCompletions.collectAsState()
    val activeStory by viewModel.activeStory.collectAsState()

    val stories = InitialDataSeed.ADVENTURE_STORIES

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
                currentTab = "stories",
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
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DeepForestGreen,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SandGold),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📖 Cerita Petualangan (Adventure Lore)",
                        fontWeight = FontWeight.Bold,
                        color = SandGold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Baca kisah penjelajahan di setiap wilayah peta, pelajari kosakata dalam konteks bacaan, dan selesaikan kuis pemahaman.",
                        color = PureWhite,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(stories) { story ->
                    val isDone = completedStories.contains(story.id)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openStory(story) }
                            .testTag("story_card_${story.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DeepForestGreen
                                ) {
                                    Text(
                                        text = story.region,
                                        color = SandGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                if (isDone) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Selesai", color = DeepForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = story.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepForestGreen
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = story.synopsis,
                                color = DeepForestGreen.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Target Vocabulary pills
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                story.targetWords.take(4).forEach { word ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Cream,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen)
                                    ) {
                                        Text(
                                            text = word,
                                            color = DeepForestGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Story Reader Modal Dialog
            activeStory?.let { story ->
                StoryReaderDialog(
                    story = story,
                    onClose = { viewModel.closeStory() },
                    onComplete = {
                        viewModel.completeActiveStory(story.id)
                        viewModel.closeStory()
                    }
                )
            }
        }
    }
}

@Composable
fun StoryReaderDialog(
    story: AdventureStory,
    onClose: () -> Unit,
    onComplete: () -> Unit
) {
    var quizAnswered by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var quizCorrect by remember { mutableStateOf(false) }

    val firstQ = story.comprehensionQuestions.firstOrNull()

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Cream,
            border = androidx.compose.foundation.BorderStroke(2.dp, DeepForestGreen),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepForestGreen)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Story Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    story.paragraphs.forEach { paragraph ->
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeepForestGreen,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reading Comprehension Check
                    firstQ?.let { question ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Uji Pemahaman Bacaan:",
                                    color = DeepForestGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = question.prompt,
                                    color = DeepForestGreen,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                question.options.forEach { opt ->
                                    val isSelected = selectedOption == opt
                                    Surface(
                                        onClick = {
                                            if (!quizAnswered) {
                                                selectedOption = opt
                                                quizAnswered = true
                                                quizCorrect = opt == question.correctAnswer
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = when {
                                            quizAnswered && opt == question.correctAnswer -> PureWhite
                                            quizAnswered && isSelected && !quizCorrect -> PureWhite
                                            isSelected -> SandGold
                                            else -> PureWhite
                                        },
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (isSelected || quizAnswered) 2.dp else 1.dp,
                                            when {
                                                quizAnswered && opt == question.correctAnswer -> DeepForestGreen
                                                quizAnswered && isSelected && !quizCorrect -> SemanticRed
                                                isSelected -> DeepForestGreen
                                                else -> SageGreen
                                            }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = opt,
                                            color = when {
                                                quizAnswered && isSelected && !quizCorrect -> SemanticRed
                                                else -> DeepForestGreen
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }

                                if (quizAnswered) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (quizCorrect) "✓ Jawaban Benar! Cerita Selesai." else "❌ Kurang tepat: ${question.explanation}",
                                        color = if (quizCorrect) DeepForestGreen else SemanticRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepForestGreen,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Tandai Cerita Telah Dibaca", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }
    }
}
