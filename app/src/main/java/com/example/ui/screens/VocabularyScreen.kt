package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.VocabularyItem
import com.example.ui.components.ExplorerBottomNavBar
import com.example.ui.components.ExplorerTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.WordExplorerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(viewModel: WordExplorerViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val selectedPath by viewModel.selectedMapPath.collectAsState()
    val vocabList by viewModel.vocabularyList.collectAsState()
    val searchQuery by viewModel.vocabSearchQuery.collectAsState()
    val activeCategory by viewModel.vocabCategoryFilter.collectAsState()

    val categories = listOf("All", "Exploration", "Nature", "Travel", "History", "Geography", "Traits", "Science")

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
                currentTab = "vocab",
                onTabSelected = { viewModel.currentAppScreen.value = it }
            )
        },
        containerColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Compact Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explorer Lexicon",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Forest900,
                    fontSize = 19.sp
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LagoonTint,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Lagoon.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = "${vocabList.size} Kata",
                        color = Lagoon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Words of the Day Section (Step 8)
            val todayDateStr = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()) }
            val dailyCountdown by viewModel.timeUntilMidnightReset.collectAsState()
            val dailyWords = remember(vocabList, todayDateStr) {
                if (vocabList.isEmpty()) emptyList()
                else {
                    val count = 5 // DAILY_WORD_COUNT = 5 words per day
                    val dateHash = Math.abs(todayDateStr.hashCode())
                    val shuffled = vocabList.sortedBy { it.id }.shuffled(java.util.Random(dateHash.toLong()))
                    shuffled.take(count)
                }
            }

            if (dailyWords.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SandGold),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("words_of_the_day_section")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("☀️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "KATA HARI INI",
                                    fontFamily = CinzelFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestGreen,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Cream,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (dailyCountdown.isNotBlank()) "Kata baru: $dailyCountdown" else todayDateStr,
                                    fontFamily = JakartaFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DeepForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Daily Words Carousel Cards
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(dailyWords, key = { "daily_${it.id}" }) { dailyItem ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Cream,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen),
                                    modifier = Modifier.width(220.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = dailyItem.word,
                                                fontFamily = CinzelFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Forest900,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { viewModel.speakWord(dailyItem.word) },
                                                modifier = Modifier.size(44.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = "Dengarkan pelafalan ${dailyItem.word}",
                                                    tint = Forest700,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Arti: ${dailyItem.indonesian}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DeepForestGreen
                                        )
                                        if (dailyItem.exampleSentence.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "\"${dailyItem.exampleSentence}\"",
                                                fontSize = 11.sp,
                                                color = DeepForestGreen.copy(alpha = 0.8f),
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchVocabulary(it) },
                placeholder = { Text("Cari kata Inggris atau artinya...", color = SageDark) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Forest700) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Forest700,
                    unfocusedBorderColor = Sage,
                    focusedTextColor = Forest900,
                    unfocusedTextColor = Forest900,
                    focusedContainerColor = PureWhite,
                    unfocusedContainerColor = PureWhite
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vocab_search_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val isSelected = activeCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setVocabCategory(category) },
                        label = { Text(category, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Lagoon,
                            selectedLabelColor = PureWhite,
                            containerColor = PureWhite,
                            labelColor = Forest900
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Lagoon else Sage
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vocabulary List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(vocabList, key = { it.id }) { vocab ->
                    VocabularyCard(
                        vocab = vocab,
                        onToggleLearned = { viewModel.toggleLearnedWord(vocab) },
                        onSpeak = { viewModel.speakWord(vocab.word) }
                    )
                }
            }
        }
    }
}

@Composable
fun VocabularyCard(
    vocab: VocabularyItem,
    onToggleLearned: () -> Unit,
    onSpeak: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, Sage.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Word, Pronunciation, CEFR Badge, Learned Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vocab.word,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Forest900,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = vocab.pronunciation,
                            color = SageDark,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "${vocab.partOfSpeech} • ${vocab.category}",
                        color = Lagoon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Word",
                            tint = Forest700,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Lagoon
                    ) {
                        Text(
                            text = vocab.cefr,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleLearned,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = if (vocab.isLearned) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle Learned",
                            tint = if (vocab.isLearned) Leaf else SageDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Indonesian Translation & English Definition
            Text(
                text = "Arti: ${vocab.indonesian}",
                fontWeight = FontWeight.Bold,
                color = Forest900,
                fontSize = 14.sp
            )
            Text(
                text = "Definisi: ${vocab.englishDef}",
                color = SageDark,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Example Sentence Box (Cream Parchment Box)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CreamSoft,
                border = androidx.compose.foundation.BorderStroke(1.dp, Sage.copy(alpha = 0.65f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "💬 \"${vocab.exampleSentence}\"",
                        color = Forest900,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "(${vocab.exampleTranslation})",
                        color = SageDark,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
