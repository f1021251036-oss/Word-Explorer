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
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DeepJungleGreen,
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📖 Cerita Petualangan (Adventure Lore)",
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Baca kisah penjelajahan di setiap wilayah peta, pelajari kosakata dalam konteks bacaan, dan selesaikan kuis pemahaman.",
                        color = ExplorerCream,
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
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
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
                                    color = when (story.region) {
                                        "Green Forest" -> ForestGreen
                                        "Lost Island" -> SeaBlue
                                        "Ancient Ruins" -> Color(0xFFC49A45)
                                        "Mountain Passage" -> StoneGray
                                        else -> CrystalViolet
                                    }
                                ) {
                                    Text(
                                        text = story.region,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                if (isDone) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Selesai", color = Color(0xFFA5D6A7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = story.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldYellow
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = story.synopsis,
                                color = ExplorerCream,
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
                                        color = Color(0xFF1E4637)
                                    ) {
                                        Text(
                                            text = word,
                                            color = ExplorerCream,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
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
            color = SurfaceDark,
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
                        color = GoldYellow
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = ExplorerCream)
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
                            color = Color.White,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reading Comprehension Check
                    firstQ?.let { question ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E4637)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Uji Pemahaman Bacaan:",
                                    color = GoldYellow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = question.prompt,
                                    color = Color.White,
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
                                            quizAnswered && opt == question.correctAnswer -> SuccessGreen.copy(alpha = 0.4f)
                                            quizAnswered && isSelected && !quizCorrect -> ErrorRed.copy(alpha = 0.4f)
                                            isSelected -> Color(0xFF285434)
                                            else -> Color(0xFF15382B)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = opt,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }

                                if (quizAnswered) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (quizCorrect) "✓ Jawaban Benar! Cerita Selesai." else "❌ Kurang tepat: ${question.explanation}",
                                        color = if (quizCorrect) Color(0xFFA5D6A7) else Color(0xFFFF8A80),
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
                    colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Tandai Cerita Telah Dibaca", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
