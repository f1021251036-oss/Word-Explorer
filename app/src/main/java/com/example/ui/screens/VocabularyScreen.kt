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
            // Header Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DeepForestGreen,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SandGold),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📚 Explorer Lexicon & Dictionary",
                        fontWeight = FontWeight.Bold,
                        color = SandGold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Menampilkan ${vocabList.size} kosakata aktif kurikulum petualangan.",
                        color = PureWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchVocabulary(it) },
                placeholder = { Text("Cari kata Inggris atau artinya...", color = SageGreen) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DeepForestGreen) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepForestGreen,
                    unfocusedBorderColor = SageGreen,
                    focusedTextColor = DeepForestGreen,
                    unfocusedTextColor = DeepForestGreen,
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
                            selectedContainerColor = DeepForestGreen,
                            selectedLabelColor = PureWhite,
                            containerColor = PureWhite,
                            labelColor = DeepForestGreen
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) DeepForestGreen else SageGreen
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
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Word, Pronunciation, CEFR Badge, Learned Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vocab.word,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = vocab.pronunciation,
                            color = SageGreen,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "${vocab.partOfSpeech} • ${vocab.category}",
                        color = SageGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Word",
                            tint = DeepForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DeepForestGreen
                    ) {
                        Text(
                            text = vocab.cefr,
                            color = SandGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onToggleLearned,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (vocab.isLearned) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle Learned",
                            tint = if (vocab.isLearned) DeepForestGreen else SageGreen.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Indonesian Translation & English Definition
            Text(
                text = "Arti: ${vocab.indonesian}",
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen,
                fontSize = 14.sp
            )
            Text(
                text = "Definisi: ${vocab.englishDef}",
                color = DeepForestGreen.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Example Sentence Box (Cream Parchment Box)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Cream,
                border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "💬 \"${vocab.exampleSentence}\"",
                        color = DeepForestGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "(${vocab.exampleTranslation})",
                        color = DeepForestGreen.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
