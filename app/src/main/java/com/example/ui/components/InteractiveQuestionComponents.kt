@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PictureOption
import com.example.data.models.Question
import com.example.ui.theme.*
import com.example.ui.viewmodel.WordExplorerViewModel

// =========================================================================
// 1. FILL IN THE BLANK (TYPED + ADVENTURE HINT TOGGLE)
// =========================================================================
@Composable
fun InteractiveFillBlankRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val typed by viewModel.typedAnswer.collectAsState()
    val showHint by viewModel.showTypingHint.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Sentence Card with Blank
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LENGKAPI LOG KALIMAT SURVEYOR:",
                    fontFamily = CinzelFontFamily,
                    fontSize = 11.sp,
                    color = SageDark,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = question.sentenceWithBlank ?: question.prompt,
                    fontSize = 15.sp,
                    color = DeepForestGreen,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                )
            }
        }

        // Typing Field (Single Crisp Border, no notch cut)
        ExpeditionTextInputField(
            value = typed,
            onValueChange = { if (!isAnswerChecked) viewModel.setTypedAnswer(it) },
            placeholderText = "Ketik kata bahasa Inggris...",
            enabled = !isAnswerChecked,
            trailingIcon = {
                IconButton(onClick = { viewModel.speakWord(question.correctAnswer) }) {
                    ExpeditionIconSound(isEnabled = true, tint = DeepForestGreen, modifier = Modifier.size(18.dp))
                }
            },
            testTag = "fill_blank_input_field"
        )

        // Optional Two-Step Indirect Clue Drawer (Task 7)
        if (question.hintsEnabled && (question.clue != null || question.hint != null)) {
            val step by viewModel.clueStep.collectAsState()
            val clue1 = question.clue ?: question.hint ?: ""
            val clue2 = question.clue2 ?: "Kata ini terdiri dari ${question.targetWord.length} huruf."

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.advanceClueStep() },
                    modifier = Modifier.testTag("fill_blank_hint_button")
                ) {
                    ExpeditionIconScroll(tint = DeepForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (step) {
                            0 -> "BUTUH KISI-KISI?"
                            1 -> "KISI-KISI TAMBAHAN (2/2)"
                            else -> "TUTUP KISI-KISI"
                        },
                        fontFamily = CinzelFontFamily,
                        color = DeepForestGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = step > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SandGold.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SandGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Petunjuk Fungsi: $clue1",
                            color = DeepForestGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (step >= 2) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🔍 Detail Tambahan: $clue2",
                                color = DeepForestGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else if (!question.hintsEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tidak ada kisi-kisi di tingkat master",
                    color = SageDark,
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

// =========================================================================
// 2. SENTENCE BUILDER (WORD CHIP ORDERING)
// =========================================================================
@Composable
fun SentenceBuilderRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val slots = viewModel.sentenceBuilderSlots
    val available = viewModel.availableSentenceChips

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Ketuk kepingan kata berurutan untuk menyusun kalimat ekspedisi:",
            fontSize = 12.sp,
            color = DeepForestGreen,
            fontWeight = FontWeight.Medium
        )

        // Assembled Sentence Area
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DeepForestGreen),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (slots.isEmpty()) {
                    Text(
                        text = "Kalimat akan tersusun di sini...",
                        color = SageDark,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        slots.forEachIndexed { idx, token ->
                            Surface(
                                onClick = { if (!isAnswerChecked) viewModel.removeSentenceChip(idx) },
                                shape = RoundedCornerShape(8.dp),
                                color = DeepForestGreen,
                                shadowElevation = 2.dp,
                                modifier = Modifier.testTag("sentence_slot_$idx")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = token,
                                        color = PureWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (!isAnswerChecked) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "✕", color = SandGold, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Available Chips Pool
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Cream,
            border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Kepingan Kata Tersedia:",
                    fontFamily = CinzelFontFamily,
                    fontSize = 10.sp,
                    color = SageDark,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    available.forEach { (id, token) ->
                        Surface(
                            onClick = { if (!isAnswerChecked) viewModel.pickSentenceChip(id, token) },
                            shape = RoundedCornerShape(8.dp),
                            color = SandGold,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, DeepForestGreen),
                            shadowElevation = 3.dp,
                            modifier = Modifier.testTag("sentence_chip_$id")
                        ) {
                            Text(
                                text = token,
                                color = DeepForestGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Reset Button
        if (!isAnswerChecked && slots.isNotEmpty()) {
            TextButton(
                onClick = { viewModel.resetSentenceBuilder() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                ExpeditionIconReset(tint = DeepForestGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ULANGI SUSUNAN KALIMAT",
                    fontFamily = CinzelFontFamily,
                    color = DeepForestGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

// =========================================================================
// 3. LISTEN AND TYPE (AUDITORY PRODUCTION + HINT)
// =========================================================================
@Composable
fun ListenAndTypeRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val typed by viewModel.typedAnswer.collectAsState()
    val showHint by viewModel.showTypingHint.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Big Audio Speaker Button
        Surface(
            onClick = { viewModel.speakWord(question.audioPromptWord ?: question.targetWord) },
            shape = CircleShape,
            color = SandGold,
            border = androidx.compose.foundation.BorderStroke(2.dp, DeepForestGreen),
            shadowElevation = 6.dp,
            modifier = Modifier
                .size(76.dp)
                .testTag("listen_audio_replay_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                ExpeditionIconSound(isEnabled = true, tint = DeepForestGreen, modifier = Modifier.size(36.dp))
            }
        }

        Text(
            text = "Dengarkan pelafalan penjelajah dan ketik kata yang terdengar:",
            fontSize = 12.sp,
            color = DeepForestGreen,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        // Typing Field (Single Crisp Border, no notch cut)
        ExpeditionTextInputField(
            value = typed,
            onValueChange = { if (!isAnswerChecked) viewModel.setTypedAnswer(it) },
            placeholderText = "Ketik kata yang kamu dengar...",
            enabled = !isAnswerChecked,
            testTag = "listen_type_input_field"
        )

        // Two-Step Indirect Clue Toggle (Task 7)
        if (question.hintsEnabled && (question.clue != null || question.hint != null)) {
            val step by viewModel.clueStep.collectAsState()
            val clue1 = question.clue ?: question.hint ?: ""
            val clue2 = question.clue2 ?: "Kata ini terdiri dari ${question.targetWord.length} huruf."

            TextButton(onClick = { viewModel.advanceClueStep() }) {
                Text(
                    text = when (step) {
                        0 -> "Butuh Kisi-kisi?"
                        1 -> "Kisi-kisi Tambahan (2/2)"
                        else -> "Tutup Kisi-kisi"
                    },
                    color = DeepForestGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            AnimatedVisibility(visible = step > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SandGold.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SandGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 Petunjuk Fungsi: $clue1",
                            color = DeepForestGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (step >= 2) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🔍 Detail Tambahan: $clue2",
                                color = DeepForestGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else if (!question.hintsEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tidak ada kisi-kisi di tingkat master",
                    color = SageDark,
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

// =========================================================================
// 4. PICTURE TAP (BESPOKE INLINE VECTOR ART)
// =========================================================================
@Composable
fun PictureTapRenderer(
    question: Question,
    selectedOption: String?,
    isAnswerChecked: Boolean,
    isAnswerCorrect: Boolean,
    onOptionSelected: (String) -> Unit
) {
    val options = question.pictureOptions ?: listOf(
        PictureOption("opt_compass", "compass", "Navigational Compass"),
        PictureOption("opt_tree", "tree", "Woodland Pine Tree"),
        PictureOption("opt_tent", "tent", "Explorer Camp Tent"),
        PictureOption("opt_map", "map", "Surveyor Route Map")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 2x2 Grid of Specimen Cards
        for (row in 0 until (options.size + 1) / 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (col in 0 until 2) {
                    val idx = row * 2 + col
                    if (idx < options.size) {
                        val opt = options[idx]
                        val isSelected = selectedOption == opt.label
                        val isCorrect = opt.label == question.correctAnswer

                        Surface(
                            onClick = { if (!isAnswerChecked) onOptionSelected(opt.label) },
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isAnswerChecked && isCorrect -> LeafTint
                                isAnswerChecked && isSelected && !isAnswerCorrect -> TerracottaTint
                                isSelected -> Sand
                                else -> PureWhite
                            },
                            shadowElevation = if (isSelected) 4.dp else 1.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected || isAnswerChecked) 2.dp else 1.5.dp,
                                when {
                                    isAnswerChecked && isCorrect -> Leaf
                                    isAnswerChecked && isSelected && !isAnswerCorrect -> Terracotta
                                    isSelected -> Forest900
                                    else -> Sage
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("picture_option_$idx")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(
                                            if (isSelected) DeepForestGreen.copy(alpha = 0.15f) else Cream,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (opt.vectorIconKey) {
                                        "compass" -> ExpeditionIconCompass(modifier = Modifier.size(32.dp))
                                        "map" -> ExpeditionIconMap(modifier = Modifier.size(32.dp))
                                        "tree" -> ExpeditionIconTree(modifier = Modifier.size(32.dp))
                                        "tent" -> ExpeditionIconTent(modifier = Modifier.size(32.dp))
                                        "lantern" -> ExpeditionIconLantern(modifier = Modifier.size(32.dp))
                                        "boots" -> ExpeditionIconBoots(modifier = Modifier.size(32.dp))
                                        "backpack" -> ExpeditionIconBackpack(modifier = Modifier.size(32.dp))
                                        "campfire", "fire" -> ExpeditionIconCampfire(modifier = Modifier.size(32.dp))
                                        else -> ExpeditionIconCompass(modifier = Modifier.size(32.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = opt.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestGreen,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. CATEGORY SORT (EXPEDITION BUCKETS)
// =========================================================================
@Composable
fun CategorySortRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val buckets = question.categoryBuckets ?: emptyMap()
    val bucketNames = remember(question.id) { buckets.keys.toList() }
    val allWords = remember(question.id) { buckets.values.flatten().shuffled() }

    val assignments = viewModel.categoryBucketAssignments
    val selectedWord by viewModel.selectedCategoryWord.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Ketuk kata di bawah, lalu ketuk kantung survei tujuan:",
            fontSize = 12.sp,
            color = DeepForestGreen,
            fontWeight = FontWeight.Medium
        )

        // Unassigned Words Row
        val unassigned = allWords.filter { !assignments.containsKey(it) }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Kata yang Belum Dipilah:",
                    fontFamily = CinzelFontFamily,
                    fontSize = 10.sp,
                    color = SageDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (unassigned.isEmpty()) {
                    Text("Semua kata telah dimasukkan ke dalam kantung!", fontSize = 11.sp, color = DeepForestGreen)
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        unassigned.forEach { w ->
                            val isSelected = selectedWord == w
                            Surface(
                                onClick = { if (!isAnswerChecked) viewModel.selectCategoryWord(w) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) DeepForestGreen else SandGold,
                                shadowElevation = 2.dp,
                                modifier = Modifier.testTag("sort_word_$w")
                            ) {
                                Text(
                                    text = w,
                                    color = if (isSelected) PureWhite else DeepForestGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Two Destination Buckets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            bucketNames.forEach { bucket ->
                val assignedToThis = assignments.filter { it.value == bucket }.keys
                Surface(
                    onClick = {
                        val word = selectedWord
                        if (!isAnswerChecked && word != null) {
                            viewModel.assignCategoryWord(word, bucket)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = Cream,
                    border = androidx.compose.foundation.BorderStroke(
                        if (selectedWord != null) 2.dp else 1.5.dp,
                        if (selectedWord != null) DeepForestGreen else SageGreen
                    ),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bucket_$bucket")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = bucket.uppercase(),
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DeepForestGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (assignedToThis.isEmpty()) {
                            Text(
                                text = "Ketuk untuk taruh di sini",
                                fontSize = 11.sp,
                                color = SageDark
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                assignedToThis.forEach { w ->
                                    Surface(
                                        onClick = { if (!isAnswerChecked) viewModel.unassignCategoryWord(w) },
                                        shape = RoundedCornerShape(6.dp),
                                        color = PureWhite,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DeepForestGreen),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = w, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                                            if (!isAnswerChecked) {
                                                Text(text = "✕", fontSize = 10.sp, color = Terracotta)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 6. MEMORY FLIP (CARD PAIRING)
// =========================================================================
@Composable
fun MemoryFlipRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val pairs = question.matchingPairs ?: emptyMap()
    val allCards = remember(question.id) {
        pairs.flatMap { listOf(it.key, it.value) }.sorted()
    }
    val flipped = viewModel.memoryFlippedIndices
    val matched = viewModel.memoryMatchedCards

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Buka kartu dan temukan pasangan kata dengan maknanya:",
            fontSize = 12.sp,
            color = DeepForestGreen,
            fontWeight = FontWeight.Medium
        )

        // 2x2 or 2x3 Grid
        for (row in 0 until (allCards.size + 1) / 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (col in 0 until 2) {
                    val idx = row * 2 + col
                    if (idx < allCards.size) {
                        val cardText = allCards[idx]
                        val isFlipped = flipped.contains(idx) || matched.contains(cardText)
                        val isMatched = matched.contains(cardText)

                        Surface(
                            onClick = {
                                if (!isAnswerChecked) {
                                    viewModel.flipMemoryCard(idx, cardText, pairs)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                isMatched -> PureWhite
                                isFlipped -> SandGold
                                else -> DeepForestGreen
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isMatched) Leaf else Forest900
                            ),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .testTag("memory_card_$idx")
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                                if (isFlipped) {
                                    Text(
                                        text = cardText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = DeepForestGreen,
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    ExpeditionIconCompass(tint = SandGold, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// =========================================================================
// 7. TRUE OR FALSE
// =========================================================================
@Composable
fun TrueFalseRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean,
    isAnswerCorrect: Boolean
) {
    val selected by viewModel.selectedTrueFalse.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Statement Plate
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PERNYATAAN LOG SURVEI:",
                    fontFamily = CinzelFontFamily,
                    fontSize = 10.sp,
                    color = SageDark,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.subText ?: question.prompt,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Forest900,
                    lineHeight = 22.sp
                )
            }
        }

        // True / False Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // TRUE Button
            val isTrueSelected = selected == true
            Surface(
                onClick = { if (!isAnswerChecked) viewModel.selectTrueFalse(true) },
                shape = RoundedCornerShape(12.dp),
                color = when {
                    isAnswerChecked && question.isStatementTrue == true -> LeafTint
                    isAnswerChecked && isTrueSelected && !isAnswerCorrect -> TerracottaTint
                    isTrueSelected -> Sand
                    else -> PureWhite
                },
                border = androidx.compose.foundation.BorderStroke(
                    if (isTrueSelected || isAnswerChecked) 2.dp else 1.5.dp,
                    when {
                        isAnswerChecked && question.isStatementTrue == true -> Leaf
                        isAnswerChecked && isTrueSelected && !isAnswerCorrect -> Terracotta
                        isTrueSelected -> Forest900
                        else -> Sage
                    }
                ),
                shadowElevation = if (isTrueSelected) 4.dp else 1.dp,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("true_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "BENAR (TRUE)",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen,
                        fontSize = 13.sp
                    )
                }
            }

            // FALSE Button
            val isFalseSelected = selected == false
            Surface(
                onClick = { if (!isAnswerChecked) viewModel.selectTrueFalse(false) },
                shape = RoundedCornerShape(12.dp),
                color = when {
                    isAnswerChecked && question.isStatementTrue == false -> LeafTint
                    isAnswerChecked && isFalseSelected && !isAnswerCorrect -> TerracottaTint
                    isFalseSelected -> Sand
                    else -> PureWhite
                },
                border = androidx.compose.foundation.BorderStroke(
                    if (isFalseSelected || isAnswerChecked) 2.dp else 1.5.dp,
                    when {
                        isAnswerChecked && question.isStatementTrue == false -> Leaf
                        isAnswerChecked && isFalseSelected && !isAnswerCorrect -> Terracotta
                        isFalseSelected -> Forest900
                        else -> Sage
                    }
                ),
                shadowElevation = if (isFalseSelected) 4.dp else 1.dp,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("false_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "SALAH (FALSE)",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// =========================================================================
// 8. WORD HUNT (5x5 LETTER GRID)
// =========================================================================
@Composable
fun WordHuntRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val grid = question.gridLetters ?: listOf(
        listOf("F", "T", "R", "E", "E"),
        listOf("P", "O", "M", "A", "P"),
        listOf("A", "T", "O", "S", "S"),
        listOf("T", "M", "O", "S", "S"),
        listOf("H", "W", "O", "O", "D")
    )
    val selectedCoords = viewModel.selectedGridCoords

    val currentSpelled = selectedCoords.mapNotNull { (r, c) ->
        grid.getOrNull(r)?.getOrNull(c)
    }.joinToString("")

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Current Assembled Letters Display
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, DeepForestGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kata Terpilih: ${if (currentSpelled.isEmpty()) "(Ketuk huruf pada kisi)" else currentSpelled}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepForestGreen
                )
                if (!isAnswerChecked && selectedCoords.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearGridCoords() }) {
                        Text("Reset", color = Terracotta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Dynamic Letter Grid Display (fits 320px width cleanly)
        val colCount = grid.firstOrNull()?.size ?: 5
        val tileSize = if (colCount >= 7) 34.dp else if (colCount >= 6) 38.dp else 44.dp
        val fontSize = if (colCount >= 7) 14.sp else if (colCount >= 6) 15.sp else 17.sp

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Cream,
            border = androidx.compose.foundation.BorderStroke(2.dp, DeepForestGreen),
            shadowElevation = 3.dp,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                grid.forEachIndexed { r, rowList ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowList.forEachIndexed { c, char ->
                            val isSelected = selectedCoords.contains(Pair(r, c))
                            Surface(
                                onClick = { if (!isAnswerChecked) viewModel.toggleGridCoord(r, c) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) DeepForestGreen else PureWhite,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) SandGold else SageGreen
                                ),
                                shadowElevation = if (isSelected) 2.dp else 1.dp,
                                modifier = Modifier
                                    .size(tileSize)
                                    .testTag("grid_tile_${r}_${c}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = char,
                                        fontWeight = FontWeight.Black,
                                        fontSize = fontSize,
                                        color = if (isSelected) SandGold else DeepForestGreen
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
