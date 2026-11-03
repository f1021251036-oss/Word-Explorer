package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Question
import com.example.data.models.QuestionType
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizUiState
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun QuizScreen(viewModel: WordExplorerViewModel) {
    val quizState by viewModel.quizState.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (!quizState.isQuizFinished) {
            // Main 10-Question Interactive Quiz Mode
            MainQuizContent(
                state = quizState,
                viewModel = viewModel,
                isSoundEnabled = isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onOptionSelected = { viewModel.selectQuizOption(it) },
                onCheckAnswer = { viewModel.checkQuizAnswer() },
                onNextQuestion = { viewModel.nextQuizQuestion() },
                onExit = { viewModel.exitQuizToMap() }
            )
        } else if (quizState.isRemedialMode && !quizState.isLevelCompleted) {
            // Adaptive Remedial Obligation Mode
            RemedialTaskContent(
                state = quizState,
                viewModel = viewModel,
                isSoundEnabled = isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onOptionSelected = { viewModel.selectRemedialOption(it) },
                onCheckAnswer = { viewModel.checkRemedialAnswer() },
                onRetry = { viewModel.retryRemedialTask() },
                onNextTask = { viewModel.nextRemedialTask() },
                onExit = { viewModel.exitQuizToMap() }
            )
        } else {
            // Level Completed Celebration Screen
            LevelCelebrationContent(
                state = quizState,
                onBackToMap = { viewModel.exitQuizToMap() }
            )
        }
    }
}

@Composable
fun MainQuizContent(
    state: QuizUiState,
    viewModel: WordExplorerViewModel,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onOptionSelected: (String) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onExit: () -> Unit
) {
    val currentQ = state.questions.getOrNull(state.currentQuestionIndex) ?: return
    val totalQuestions = state.questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onExit,
                    modifier = Modifier.testTag("exit_quiz_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Exit Quiz", tint = ExplorerCream)
                }
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("quiz_sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (isSoundEnabled) "Mute Sound" else "Unmute Sound",
                        tint = if (isSoundEnabled) GoldYellow else Color.Gray
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DeepJungleGreen,
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen)
            ) {
                Text(
                    text = "Stage ${state.stage}-${state.level}",
                    color = GoldYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF285434)
            ) {
                Text(
                    text = "Skor: ${state.firstAttemptScore}/10",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pertanyaan ${state.currentQuestionIndex + 1} dari $totalQuestions",
                color = ExplorerCream,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${((state.currentQuestionIndex + 1) * 100 / totalQuestions)}%",
                color = GoldYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = AdventureOrange,
            trackColor = Color(0xFF285434)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Question Type Badge & Prompt Header
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (currentQ.type) {
                            QuestionType.DRAG_AND_MATCH -> Color(0xFF2962FF)
                            QuestionType.FILL_BLANK -> Color(0xFFE65100)
                            QuestionType.IMAGE_VOCABULARY -> Color(0xFF00897B)
                            QuestionType.WORD_UNSCRAMBLE -> Color(0xFF8E24AA)
                            QuestionType.SENTENCE_COMPLETION -> Color(0xFF43A047)
                            QuestionType.MULTIPLE_CHOICE -> ForestGreen
                        }
                    ) {
                        Text(
                            text = when (currentQ.type) {
                                QuestionType.DRAG_AND_MATCH -> "🔗 Drag & Match"
                                QuestionType.FILL_BLANK -> "✍️ Fill in the Blanks"
                                QuestionType.IMAGE_VOCABULARY -> "🖼️ Image Vocabulary"
                                QuestionType.WORD_UNSCRAMBLE -> "🔤 Word Arrangement"
                                QuestionType.SENTENCE_COMPLETION -> "📝 Sentence Completion"
                                QuestionType.MULTIPLE_CHOICE -> "Pilihan Ganda"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Pronunciation Practice Audio Button
                    Surface(
                        onClick = { viewModel.speakWord(currentQ.targetWord) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF285434),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
                        modifier = Modifier.testTag("pronunciation_speaker_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Dengar Pengucapan", tint = GoldYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dengar", color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = currentQ.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 22.sp
                )

                currentQ.subText?.let { sub ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ExplorerCream,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- Interactive Exercise Renderers ---
        when (currentQ.type) {
            QuestionType.DRAG_AND_MATCH -> {
                DragAndMatchRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.FILL_BLANK -> {
                FillBlankRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.WORD_UNSCRAMBLE -> {
                WordUnscrambleRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.IMAGE_VOCABULARY -> {
                ImageVocabularyRenderer(
                    question = currentQ,
                    selectedOption = state.selectedOption,
                    isAnswerChecked = state.isAnswerChecked,
                    isAnswerCorrect = state.isAnswerCorrect,
                    onOptionSelected = onOptionSelected,
                    onSpeak = { viewModel.speakWord(currentQ.targetWord) }
                )
            }
            QuestionType.SENTENCE_COMPLETION, QuestionType.MULTIPLE_CHOICE -> {
                SentenceCompletionRenderer(
                    question = currentQ,
                    selectedOption = state.selectedOption,
                    isAnswerChecked = state.isAnswerChecked,
                    isAnswerCorrect = state.isAnswerCorrect,
                    onOptionSelected = onOptionSelected
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Immediate Feedback Explanation Card
        AnimatedVisibility(visible = state.isAnswerChecked) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (state.isAnswerCorrect) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (state.isAnswerCorrect) SuccessGreen else ErrorRed
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (state.isAnswerCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (state.isAnswerCorrect) SuccessGreen else ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isAnswerCorrect) "Luar Biasa, Jawaban Benar! 🎉" else "Jawaban Belum Tepat!",
                            fontWeight = FontWeight.Bold,
                            color = if (state.isAnswerCorrect) SuccessGreen else ErrorRed,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentQ.explanation,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Action Button: Periksa Jawaban / Lanjut
        if (!state.isAnswerChecked) {
            val canCheck = when (currentQ.type) {
                QuestionType.DRAG_AND_MATCH -> {
                    val pairs = currentQ.matchingPairs ?: emptyMap()
                    pairs.isNotEmpty() && viewModel.matchingAnswers.size == pairs.size
                }
                QuestionType.FILL_BLANK -> viewModel.typedAnswer.collectAsState().value.isNotBlank()
                QuestionType.WORD_UNSCRAMBLE -> viewModel.scrambledSlots.isNotEmpty()
                else -> state.selectedOption != null
            }

            Button(
                onClick = onCheckAnswer,
                enabled = canCheck,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("quiz_check_button")
            ) {
                Text("Periksa Jawaban", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        } else {
            Button(
                onClick = onNextQuestion,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("quiz_next_button")
            ) {
                Text(
                    text = if (state.currentQuestionIndex < totalQuestions - 1) "Pertanyaan Berikutnya ➔" else "Selesaikan Misi Level 🏆",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

// 1. Drag and Match Renderer
@Composable
fun DragAndMatchRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val pairs = question.matchingPairs ?: emptyMap()
    val englishWords = remember(question.id) { pairs.keys.toList() }
    val indonesianWords = remember(question.id) { pairs.values.toList().shuffled() }

    val selectedEnglish by viewModel.matchingSelectedEnglish.collectAsState()
    val matchingAnswers = viewModel.matchingAnswers

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Ketuk kata bahasa Inggris, lalu ketuk arti bahasa Indonesia yang cocok:",
            fontSize = 12.sp,
            color = ExplorerCream.copy(alpha = 0.8f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Column: English Words
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                englishWords.forEach { eng ->
                    val isSelected = selectedEnglish == eng
                    val isPaired = matchingAnswers.containsKey(eng)

                    Surface(
                        onClick = {
                            if (!isAnswerChecked) {
                                viewModel.selectMatchingEnglish(eng)
                                viewModel.speakWord(eng)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSelected -> GoldYellow
                            isPaired -> Color(0xFF1B4E38)
                            else -> SurfaceDark
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) AdventureOrange else ForestGreen.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("match_eng_$eng")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = eng,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) DeepJungleGreen else Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Dengar",
                                tint = if (isSelected) DeepJungleGreen else GoldYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Right Column: Indonesian Targets
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                indonesianWords.forEach { indo ->
                    val pairedEng = matchingAnswers.entries.find { it.value == indo }?.key

                    Surface(
                        onClick = {
                            if (!isAnswerChecked) {
                                if (selectedEnglish != null) {
                                    viewModel.pairMatchingIndonesian(indo)
                                } else if (pairedEng != null) {
                                    viewModel.unpairMatching(pairedEng)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            pairedEng != null -> Color(0xFF2E6B4F)
                            else -> SurfaceDark
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (pairedEng != null) GoldYellow else Color(0xFF3B6B58)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("match_indo_$indo")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = indo,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            if (pairedEng != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "➔ $pairedEng",
                                        fontWeight = FontWeight.Bold,
                                        color = GoldYellow,
                                        fontSize = 11.sp
                                    )
                                    if (!isAnswerChecked) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "(Batal)", fontSize = 9.sp, color = ExplorerCream.copy(alpha = 0.6f))
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

// 2. Fill in the Blanks Renderer
@Composable
fun FillBlankRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val typed by viewModel.typedAnswer.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1B3D2F),
            border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Lengkapi kalimat bahasa Inggris di bawah ini:",
                    fontSize = 12.sp,
                    color = GoldYellow,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = question.sentenceWithBlank ?: question.prompt,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                )
            }
        }

        OutlinedTextField(
            value = typed,
            onValueChange = { if (!isAnswerChecked) viewModel.setTypedAnswer(it) },
            label = { Text("Ketik kata bahasa Inggris...") },
            enabled = !isAnswerChecked,
            trailingIcon = {
                IconButton(onClick = { viewModel.speakWord(question.correctAnswer) }) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Dengar", tint = GoldYellow)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldYellow,
                unfocusedBorderColor = ForestGreen,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fill_blank_input_field")
        )

        if (isAnswerChecked) {
            Text(
                text = "Kunci Jawaban: ${question.correctAnswer}",
                color = GoldYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 3. Word Arrangement (Unscramble) Renderer
@Composable
fun WordUnscrambleRenderer(
    question: Question,
    viewModel: WordExplorerViewModel,
    isAnswerChecked: Boolean
) {
    val slots = viewModel.scrambledSlots
    val available = viewModel.availableLetters

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Ketuk huruf di bawah untuk menyusun kata:",
            fontSize = 12.sp,
            color = ExplorerCream
        )

        // Assembled Word Slots
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1B382B),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldYellow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (slots.isEmpty()) {
                    Text(
                        text = "Ketuk huruf-huruf di bawah...",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                } else {
                    slots.forEachIndexed { idx, char ->
                        Surface(
                            onClick = { if (!isAnswerChecked) viewModel.removeScrambledLetter(idx) },
                            shape = RoundedCornerShape(8.dp),
                            color = AdventureOrange,
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(36.dp)
                                .testTag("scramble_slot_$idx")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = char,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Shuffled Available Letter Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            available.forEach { (id, char) ->
                Surface(
                    onClick = { if (!isAnswerChecked) viewModel.pickScrambledLetter(id, char) },
                    shape = RoundedCornerShape(8.dp),
                    color = DeepJungleGreen,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldYellow),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(40.dp)
                        .testTag("letter_tile_$id")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = char,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Reset Button
        if (!isAnswerChecked && slots.isNotEmpty()) {
            TextButton(
                onClick = { viewModel.resetScrambledLetters() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ulangi Susunan", color = GoldYellow, fontSize = 12.sp)
            }
        }
    }
}

// 4. Image-Based Vocabulary Renderer
@Composable
fun ImageVocabularyRenderer(
    question: Question,
    selectedOption: String?,
    isAnswerChecked: Boolean,
    isAnswerCorrect: Boolean,
    onOptionSelected: (String) -> Unit,
    onSpeak: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Visual Illustration Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF143326)),
            border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = question.iconEmoji ?: "✨",
                    fontSize = 52.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.subText ?: "",
                    fontSize = 13.sp,
                    color = ExplorerCream,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Options List
        question.options.forEachIndexed { idx, opt ->
            val isSelected = selectedOption == opt
            val isCorrect = opt == question.correctAnswer

            Surface(
                onClick = { if (!isAnswerChecked) onOptionSelected(opt) },
                shape = RoundedCornerShape(12.dp),
                color = when {
                    isAnswerChecked && isCorrect -> SuccessGreen.copy(alpha = 0.35f)
                    isAnswerChecked && isSelected && !isAnswerCorrect -> ErrorRed.copy(alpha = 0.35f)
                    isSelected -> Color(0xFF285434)
                    else -> SurfaceDark
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    when {
                        isAnswerChecked && isCorrect -> SuccessGreen
                        isAnswerChecked && isSelected && !isAnswerCorrect -> ErrorRed
                        isSelected -> GoldYellow
                        else -> ForestGreen.copy(alpha = 0.4f)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_option_$idx")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ('A' + idx).toString(),
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = opt,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (isAnswerChecked && isCorrect) {
                        Icon(Icons.Default.Check, contentDescription = "Benar", tint = SuccessGreen)
                    }
                }
            }
        }
    }
}

// 5. Sentence Completion Renderer
@Composable
fun SentenceCompletionRenderer(
    question: Question,
    selectedOption: String?,
    isAnswerChecked: Boolean,
    isAnswerCorrect: Boolean,
    onOptionSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEachIndexed { idx, opt ->
            val isSelected = selectedOption == opt
            val isCorrect = opt == question.correctAnswer

            Surface(
                onClick = { if (!isAnswerChecked) onOptionSelected(opt) },
                shape = RoundedCornerShape(14.dp),
                color = when {
                    isAnswerChecked && isCorrect -> SuccessGreen.copy(alpha = 0.35f)
                    isAnswerChecked && isSelected && !isAnswerCorrect -> ErrorRed.copy(alpha = 0.35f)
                    isSelected -> Color(0xFF285434)
                    else -> SurfaceDark
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    when {
                        isAnswerChecked && isCorrect -> SuccessGreen
                        isAnswerChecked && isSelected && !isAnswerCorrect -> ErrorRed
                        isSelected -> GoldYellow
                        else -> ForestGreen.copy(alpha = 0.4f)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_option_$idx")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ('A' + idx).toString(),
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = opt,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// Remedial Task Content
@Composable
fun RemedialTaskContent(
    state: QuizUiState,
    viewModel: WordExplorerViewModel,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onOptionSelected: (String) -> Unit,
    onCheckAnswer: () -> Unit,
    onRetry: () -> Unit,
    onNextTask: () -> Unit,
    onExit: () -> Unit
) {
    val currentTask = state.remedialTasks.getOrNull(state.currentRemedialIndex) ?: return
    val totalRemedials = state.initialRemedialCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Remedial Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF3E2723),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AdventureOrange),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🛡️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Misi Remedial Adaptif",
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Kuasai kata yang belum tepat untuk membuka jalan ke level berikutnya! (${state.currentRemedialIndex + 1}/$totalRemedials)",
                        color = ExplorerCream,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Remedial Question Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = currentTask.question.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                currentTask.question.subText?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = it, color = ExplorerCream, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Options
        currentTask.question.options.forEachIndexed { idx, opt ->
            val isSelected = state.remedialSelectedOption == opt
            val isCorrect = opt == currentTask.question.correctAnswer

            Surface(
                onClick = { if (!state.isRemedialChecked) onOptionSelected(opt) },
                shape = RoundedCornerShape(14.dp),
                color = when {
                    state.isRemedialChecked && isCorrect -> SuccessGreen.copy(alpha = 0.35f)
                    state.isRemedialChecked && isSelected && !state.isRemedialCorrect -> ErrorRed.copy(alpha = 0.35f)
                    isSelected -> Color(0xFF285434)
                    else -> SurfaceDark
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    when {
                        state.isRemedialChecked && isCorrect -> SuccessGreen
                        state.isRemedialChecked && isSelected && !state.isRemedialCorrect -> ErrorRed
                        isSelected -> GoldYellow
                        else -> ForestGreen.copy(alpha = 0.4f)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("remedial_option_$idx")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ('A' + idx).toString(),
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(text = opt, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        if (!state.isRemedialChecked) {
            Button(
                onClick = onCheckAnswer,
                enabled = state.remedialSelectedOption != null,
                colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("remedial_check_button")
            ) {
                Text("Periksa Jawaban Remedial", fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else if (state.isRemedialCorrect) {
            Button(
                onClick = onNextTask,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("remedial_next_button")
            ) {
                Text("Lanjut ke Materi Berikutnya ➔", fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("remedial_retry_button")
            ) {
                Text("Coba Lagi ↺", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// Celebration Content
@Composable
fun LevelCelebrationContent(
    state: QuizUiState,
    onBackToMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🎉", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Level Selesai Dikuasai!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = GoldYellow,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Misi Stage ${state.stage}-${state.level} berhasil dituntaskan dengan gemilang.",
            color = ExplorerCream,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Stars Row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { idx ->
                val earned = idx < state.starsAwarded
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (earned) GoldYellow else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${state.starsAwarded}/3 Bintang Diperoleh",
            color = GoldYellow,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onBackToMap,
            colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("celebration_continue_button")
        ) {
            Text("Kembali ke Peta Petualangan 🗺️", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }
    }
}
