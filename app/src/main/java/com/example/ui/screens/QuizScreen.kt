package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Question
import com.example.data.models.QuestionType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizUiState
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun QuizScreen(viewModel: WordExplorerViewModel) {
    val quizState by viewModel.quizState.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()

    // Handle back gesture/button gracefully in all states: always return to map
    BackHandler {
        viewModel.exitQuizToMap()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
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

/**
 * Hand-Drawn Waypoint Track: Replaces generic linear progress bar with a surveyor's path
 */
@Composable
fun ExpeditionWaypointTrack(
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LOG POS SURVEYOR ${currentIndex + 1} / $totalCount",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "${((currentIndex + 1) * 100 / totalCount.coerceAtLeast(1))}% SELESAI",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Waypoint dots row connected by inked route line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cy = h / 2f
                val strokeW = 1.5.dp.toPx()

                // Route dashed background track
                drawLine(
                    color = SageGreen.copy(alpha = 0.45f),
                    start = Offset(16.dp.toPx(), cy),
                    end = Offset(w - 16.dp.toPx(), cy),
                    strokeWidth = strokeW,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
                    cap = StrokeCap.Round
                )

                // Route traversed solid track
                val completedFraction = if (totalCount > 1) {
                    (currentIndex.toFloat() / (totalCount - 1).toFloat()).coerceIn(0f, 1f)
                } else 1f
                val activeEndX = 16.dp.toPx() + (w - 32.dp.toPx()) * completedFraction

                drawLine(
                    color = DeepForestGreen,
                    start = Offset(16.dp.toPx(), cy),
                    end = Offset(activeEndX, cy),
                    strokeWidth = strokeW * 1.5f,
                    cap = StrokeCap.Round
                )
            }

            // Waypoint Markers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until totalCount) {
                    val isDone = i < currentIndex
                    val isCurrent = i == currentIndex

                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 16.dp else 10.dp)
                            .shadow(if (isCurrent) 2.dp else 0.dp, CircleShape)
                            .background(
                                color = when {
                                    isCurrent -> DeepForestGreen
                                    isDone -> SandGold
                                    else -> Cream
                                },
                                shape = CircleShape
                            )
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = when {
                                    isCurrent -> SandGold
                                    isDone -> DeepForestGreen
                                    else -> SageGreen
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
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
            .padding(18.dp)
    ) {
        // Surveyor Header Bar
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
                    ExpeditionIconExit(tint = DeepForestGreen, modifier = Modifier.size(24.dp))
                }
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("quiz_sound_toggle_button")
                ) {
                    ExpeditionIconSound(isEnabled = isSoundEnabled, tint = DeepForestGreen, modifier = Modifier.size(22.dp))
                }
            }

            // Stage Seal
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DeepForestGreen,
                border = androidx.compose.foundation.BorderStroke(1.dp, SandGold),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "WILAYAH ${state.stage}-${state.level}",
                    fontFamily = CinzelFontFamily,
                    color = SandGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Score Stamp
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SandGold,
                border = androidx.compose.foundation.BorderStroke(1.dp, DeepForestGreen),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "SKOR: ${state.firstAttemptScore}/10",
                    fontFamily = CinzelFontFamily,
                    color = DeepForestGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Expedition Waypoint Track
        ExpeditionWaypointTrack(
            currentIndex = state.currentQuestionIndex,
            totalCount = totalQuestions
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Question Dossier Sheet (Parchment with Inked Crosshairs & Corner Details)
        ParchmentDossierCard(
            modifier = Modifier.fillMaxWidth(),
            rotation = 0.5f,
            backgroundColor = PureWhite
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Question Type Badge (Typographic & Custom Glyph, No Emojis)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DeepForestGreen,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SandGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (currentQ.type) {
                                QuestionType.DRAG_AND_MATCH -> ExpeditionIconMap(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.FILL_BLANK -> ExpeditionIconScroll(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.IMAGE_VOCABULARY -> ExpeditionIconCompass(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.WORD_UNSCRAMBLE, QuestionType.SCRAMBLED_LETTERS -> ExpeditionIconBook(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.SENTENCE_COMPLETION -> ExpeditionIconScroll(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.MULTIPLE_CHOICE -> ExpeditionIconCompass(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.SENTENCE_BUILDER -> ExpeditionIconScroll(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.LISTEN_AND_TYPE -> ExpeditionIconSound(isEnabled = true, tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.PICTURE_TAP -> ExpeditionIconCompass(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.CATEGORY_SORT -> ExpeditionIconBackpack(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.MEMORY_FLIP -> ExpeditionIconBook(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.TRUE_OR_FALSE -> ExpeditionIconCompass(tint = SandGold, modifier = Modifier.size(14.dp))
                                QuestionType.WORD_HUNT -> ExpeditionIconMap(tint = SandGold, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentQ.type) {
                                    QuestionType.DRAG_AND_MATCH -> "PENCOCOKAN KATA"
                                    QuestionType.FILL_BLANK -> "MELENGKAPI LOG"
                                    QuestionType.IMAGE_VOCABULARY -> "IDENTIFIKASI VISUAL"
                                    QuestionType.WORD_UNSCRAMBLE, QuestionType.SCRAMBLED_LETTERS -> "DEKODE HURUF"
                                    QuestionType.SENTENCE_COMPLETION -> "SINTAKSIS KALIMAT"
                                    QuestionType.MULTIPLE_CHOICE -> "INQUIRI JALUR"
                                    QuestionType.SENTENCE_BUILDER -> "SUSUN KALIMAT"
                                    QuestionType.LISTEN_AND_TYPE -> "DENGAR & KETIK"
                                    QuestionType.PICTURE_TAP -> "KETUK GAMBAR"
                                    QuestionType.CATEGORY_SORT -> "SORTIR KATEGORI"
                                    QuestionType.MEMORY_FLIP -> "KARTU MEMORI"
                                    QuestionType.TRUE_OR_FALSE -> "BENAR ATAU SALAH"
                                    QuestionType.WORD_HUNT -> "CARI KATA"
                                },
                                fontFamily = CinzelFontFamily,
                                color = SandGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Pronunciation Practice Audio Button (Tactile Brass Badge)
                    Surface(
                        onClick = { viewModel.speakWord(currentQ.targetWord) },
                        shape = RoundedCornerShape(8.dp),
                        color = SandGold,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DeepForestGreen),
                        shadowElevation = 2.dp,
                        modifier = Modifier.testTag("pronunciation_speaker_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExpeditionIconSound(isEnabled = true, tint = DeepForestGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "DENGAR",
                                fontFamily = CinzelFontFamily,
                                color = DeepForestGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentQ.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen,
                    lineHeight = 24.sp
                )

                currentQ.subText?.let { sub ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepForestGreen.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                InteractiveFillBlankRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.WORD_UNSCRAMBLE, QuestionType.SCRAMBLED_LETTERS -> {
                WordUnscrambleRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.SENTENCE_BUILDER -> {
                SentenceBuilderRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.LISTEN_AND_TYPE -> {
                ListenAndTypeRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.PICTURE_TAP -> {
                PictureTapRenderer(
                    question = currentQ,
                    selectedOption = state.selectedOption,
                    isAnswerChecked = state.isAnswerChecked,
                    isAnswerCorrect = state.isAnswerCorrect,
                    onOptionSelected = onOptionSelected
                )
            }
            QuestionType.CATEGORY_SORT -> {
                CategorySortRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.MEMORY_FLIP -> {
                MemoryFlipRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked
                )
            }
            QuestionType.TRUE_OR_FALSE -> {
                TrueFalseRenderer(
                    question = currentQ,
                    viewModel = viewModel,
                    isAnswerChecked = state.isAnswerChecked,
                    isAnswerCorrect = state.isAnswerCorrect
                )
            }
            QuestionType.WORD_HUNT -> {
                WordHuntRenderer(
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

        Spacer(modifier = Modifier.height(16.dp))

        // Immediate Feedback Explanation Card
        AnimatedVisibility(visible = state.isAnswerChecked) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PureWhite,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (state.isAnswerCorrect) Color(0xFF2D5A27) else Color(0xFFA8422B)
                ),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (state.isAnswerCorrect) {
                            ExpeditionIconCheckmark(
                                tint = Color(0xFF2D5A27),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            ExpeditionIconCross(
                                tint = Color(0xFFA8422B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isAnswerCorrect) "Langkah Tepat Terverifikasi!" else "Analisis Jalur Kurang Tepat!",
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isAnswerCorrect) Color(0xFF2D5A27) else Color(0xFFA8422B),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQ.explanation,
                        color = DeepForestGreen,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Action Button: Tactile Signpost Button
        if (!state.isAnswerChecked) {
            val canCheck = when (currentQ.type) {
                QuestionType.DRAG_AND_MATCH -> {
                    val pairs = currentQ.matchingPairs ?: emptyMap()
                    pairs.isNotEmpty() && viewModel.matchingAnswers.size == pairs.size
                }
                QuestionType.FILL_BLANK, QuestionType.LISTEN_AND_TYPE -> viewModel.typedAnswer.collectAsState().value.isNotBlank()
                QuestionType.WORD_UNSCRAMBLE, QuestionType.SCRAMBLED_LETTERS -> viewModel.scrambledSlots.isNotEmpty()
                QuestionType.SENTENCE_BUILDER -> viewModel.sentenceBuilderSlots.isNotEmpty()
                QuestionType.CATEGORY_SORT -> {
                    val totalWords = currentQ.categoryBuckets?.values?.sumOf { it.size } ?: 0
                    totalWords > 0 && viewModel.categoryBucketAssignments.size == totalWords
                }
                QuestionType.MEMORY_FLIP -> {
                    val pairs = currentQ.matchingPairs ?: emptyMap()
                    pairs.isNotEmpty() && viewModel.memoryMatchedCards.size == pairs.size * 2
                }
                QuestionType.TRUE_OR_FALSE -> viewModel.selectedTrueFalse != null
                QuestionType.WORD_HUNT -> viewModel.selectedGridCoords.size == currentQ.targetWord.length
                else -> state.selectedOption != null
            }

            TactileSignpostButton(
                label = "PERIKSA JAWABAN",
                onClick = onCheckAnswer,
                enabled = canCheck,
                testTag = "quiz_check_button",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            TactileSignpostButton(
                label = if (state.currentQuestionIndex < totalQuestions - 1) "PERTANYAAN BERIKUTNYA" else "SELESAIKAN MISI WILAYAH",
                onClick = onNextQuestion,
                testTag = "quiz_next_button",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// 1. Drag and Match Renderer (Ledger Specimen Pairing)
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
            text = "Ketuk kata bahasa Inggris, lalu ketuk arti padanan bahasa Indonesia:",
            fontSize = 12.sp,
            color = DeepForestGreen,
            fontWeight = FontWeight.Medium
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
                        shape = RoundedCornerShape(10.dp),
                        color = when {
                            isSelected -> SandGold
                            isPaired -> Cream
                            else -> PureWhite
                        },
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) DeepForestGreen else SageGreen
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
                                color = DeepForestGreen,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            ExpeditionIconSound(
                                isEnabled = true,
                                tint = DeepForestGreen,
                                modifier = Modifier.size(14.dp)
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
                        shape = RoundedCornerShape(10.dp),
                        color = when {
                            pairedEng != null -> SandGold
                            else -> PureWhite
                        },
                        shadowElevation = if (pairedEng != null) 3.dp else 1.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (pairedEng != null) DeepForestGreen else SageGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("match_indo_$indo")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = indo,
                                fontWeight = FontWeight.Medium,
                                color = DeepForestGreen,
                                fontSize = 12.sp
                            )
                            if (pairedEng != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "➔ $pairedEng",
                                        fontWeight = FontWeight.Bold,
                                        color = DeepForestGreen,
                                        fontSize = 11.sp
                                    )
                                    if (!isAnswerChecked) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "(Batal)", fontSize = 9.sp, color = SageGreen)
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
            shape = RoundedCornerShape(12.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "LENGKAPI LOG KALIMAT SURVEYOR:",
                    fontFamily = CinzelFontFamily,
                    fontSize = 11.sp,
                    color = SageGreen,
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

        // Emerged input field
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PureWhite,
            shadowElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SageGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = typed,
                onValueChange = { if (!isAnswerChecked) viewModel.setTypedAnswer(it) },
                label = { Text("Ketik kata bahasa Inggris...", color = SageGreen) },
                enabled = !isAnswerChecked,
                trailingIcon = {
                    IconButton(onClick = { viewModel.speakWord(question.correctAnswer) }) {
                        ExpeditionIconSound(isEnabled = true, tint = DeepForestGreen, modifier = Modifier.size(18.dp))
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = DeepForestGreen,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = DeepForestGreen,
                    unfocusedTextColor = DeepForestGreen,
                    focusedLabelColor = DeepForestGreen,
                    unfocusedLabelColor = SageGreen
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fill_blank_input_field")
            )
        }

        if (isAnswerChecked) {
            Text(
                text = "Kunci Jawaban: ${question.correctAnswer}",
                fontFamily = CinzelFontFamily,
                color = DeepForestGreen,
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
            color = DeepForestGreen,
            fontWeight = FontWeight.Medium
        )

        // Assembled Word Slots
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DeepForestGreen),
            shadowElevation = 2.dp,
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
                        color = SageGreen,
                        fontSize = 13.sp
                    )
                } else {
                    slots.forEachIndexed { idx, char ->
                        Surface(
                            onClick = { if (!isAnswerChecked) viewModel.removeScrambledLetter(idx) },
                            shape = RoundedCornerShape(8.dp),
                            color = DeepForestGreen,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(40.dp)
                                .testTag("scramble_slot_$idx")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = char,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                }
            }
        }

        // Shuffled Available Letter Tiles (Tactile Wooden/Brass Blocks)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            available.forEach { (id, char) ->
                Surface(
                    onClick = { if (!isAnswerChecked) viewModel.pickScrambledLetter(id, char) },
                    shape = RoundedCornerShape(8.dp),
                    color = SandGold,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DeepForestGreen),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(42.dp)
                        .testTag("letter_tile_$id")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = char,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DeepForestGreen
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
                ExpeditionIconReset(tint = DeepForestGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ULANGI SUSUNAN",
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
        // Visual Illustration Card (Dignified Explorer Specimen Sheet)
        ParchmentDossierCard(
            modifier = Modifier.fillMaxWidth(),
            rotation = -0.5f,
            backgroundColor = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(SandGold.copy(alpha = 0.35f), CircleShape)
                        .border(1.5.dp, DeepForestGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ExpeditionIconCompass(
                        tint = DeepForestGreen,
                        accentTint = SandGold,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = question.subText ?: "",
                    fontSize = 13.sp,
                    color = DeepForestGreen,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Options List
        question.options.forEachIndexed { idx, opt ->
            val isSelected = selectedOption == opt
            val isCorrect = opt == question.correctAnswer

            Surface(
                onClick = { if (!isAnswerChecked) onOptionSelected(opt) },
                shape = RoundedCornerShape(10.dp),
                color = when {
                    isAnswerChecked && isCorrect -> Color(0xFFE8F5E9)
                    isAnswerChecked && isSelected && !isAnswerCorrect -> Color(0xFFFFEBEE)
                    isSelected -> SandGold
                    else -> PureWhite
                },
                shadowElevation = if (isSelected) 3.dp else 1.dp,
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected || isAnswerChecked) 2.dp else 1.5.dp,
                    when {
                        isAnswerChecked && isCorrect -> Color(0xFF2D5A27) // Leaf green
                        isAnswerChecked && isSelected && !isAnswerCorrect -> Color(0xFFA8422B) // Terracotta
                        isSelected -> DeepForestGreen
                        else -> SageGreen
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
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) DeepForestGreen else SageGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = ('A' + idx).toString(),
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PureWhite else DeepForestGreen,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = opt,
                        color = when {
                            isAnswerChecked && isSelected && !isAnswerCorrect -> Color(0xFFA8422B)
                            else -> DeepForestGreen
                        },
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (isAnswerChecked && isCorrect) {
                        ExpeditionIconCheckmark(tint = Color(0xFF2D5A27), modifier = Modifier.size(18.dp))
                    } else if (isAnswerChecked && isSelected && !isAnswerCorrect) {
                        ExpeditionIconCross(tint = Color(0xFFA8422B), modifier = Modifier.size(18.dp))
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
                shape = RoundedCornerShape(10.dp),
                color = when {
                    isAnswerChecked && isCorrect -> Color(0xFFE8F5E9)
                    isAnswerChecked && isSelected && !isAnswerCorrect -> Color(0xFFFFEBEE)
                    isSelected -> SandGold
                    else -> PureWhite
                },
                shadowElevation = if (isSelected) 3.dp else 1.dp,
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected || isAnswerChecked) 2.dp else 1.5.dp,
                    when {
                        isAnswerChecked && isCorrect -> Color(0xFF2D5A27) // Leaf green
                        isAnswerChecked && isSelected && !isAnswerCorrect -> Color(0xFFA8422B) // Terracotta
                        isSelected -> DeepForestGreen
                        else -> SageGreen
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
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) DeepForestGreen else SageGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = ('A' + idx).toString(),
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PureWhite else DeepForestGreen,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = opt,
                        color = when {
                            isAnswerChecked && isSelected && !isAnswerCorrect -> Color(0xFFA8422B)
                            else -> DeepForestGreen
                        },
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (isAnswerChecked && isCorrect) {
                        ExpeditionIconCheckmark(tint = Color(0xFF2D5A27), modifier = Modifier.size(18.dp))
                    } else if (isAnswerChecked && isSelected && !isAnswerCorrect) {
                        ExpeditionIconCross(tint = Color(0xFFA8422B), modifier = Modifier.size(18.dp))
                    }
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
            .padding(18.dp)
    ) {
        // Remedial Banner: Stamped Dossier Header
        ParchmentDossierCard(
            modifier = Modifier.fillMaxWidth(),
            rotation = -0.5f,
            backgroundColor = PureWhite
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(SandGold, CircleShape)
                        .border(1.dp, DeepForestGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ExpeditionIconCompass(tint = DeepForestGreen, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MISI PEMBETULAN JALUR",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Kuasai kata kunci untuk membuka kembali jalur peta (${state.currentRemedialIndex + 1}/$totalRemedials)",
                        color = DeepForestGreen.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Remedial Question Card
        ParchmentDossierCard(
            modifier = Modifier.fillMaxWidth(),
            rotation = 0.5f,
            backgroundColor = PureWhite
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = currentTask.question.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )
                currentTask.question.subText?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = it, color = DeepForestGreen.copy(alpha = 0.85f), fontSize = 13.sp)
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
                shape = RoundedCornerShape(10.dp),
                color = when {
                    state.isRemedialChecked && isCorrect -> PureWhite
                    state.isRemedialChecked && isSelected && !state.isRemedialCorrect -> PureWhite
                    isSelected -> SandGold
                    else -> PureWhite
                },
                shadowElevation = if (isSelected) 3.dp else 1.dp,
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected || state.isRemedialChecked) 2.dp else 1.5.dp,
                    when {
                        state.isRemedialChecked && isCorrect -> Color(0xFF2D5A27)
                        state.isRemedialChecked && isSelected && !state.isRemedialCorrect -> Color(0xFFA8422B)
                        isSelected -> DeepForestGreen
                        else -> SageGreen
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
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) DeepForestGreen else SageGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = ('A' + idx).toString(),
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PureWhite else DeepForestGreen,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = opt,
                        color = when {
                            state.isRemedialChecked && isSelected && !state.isRemedialCorrect -> Color(0xFFA8422B)
                            else -> DeepForestGreen
                        },
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        if (!state.isRemedialChecked) {
            TactileSignpostButton(
                label = "PERIKSA JAWABAN REMEDIAL",
                onClick = onCheckAnswer,
                enabled = state.remedialSelectedOption != null,
                testTag = "remedial_check_button",
                modifier = Modifier.fillMaxWidth()
            )
        } else if (state.isRemedialCorrect) {
            TactileSignpostButton(
                label = "LANJUT KE MATERI BERIKUTNYA",
                onClick = onNextTask,
                testTag = "remedial_next_button",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            TactileSignpostButton(
                label = "COBA LAGI",
                onClick = onRetry,
                backgroundColor = Color(0xFFA8422B),
                testTag = "remedial_retry_button",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Celebration Content (Royal Surveyor's Certificate of Mastery)
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
        ParchmentDossierCard(
            modifier = Modifier.fillMaxWidth(),
            rotation = 0f,
            backgroundColor = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stamped Star Wax Seal
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(SandGold, CircleShape)
                        .border(2.dp, DeepForestGreen, CircleShape)
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ExpeditionIconStarSeal(
                        tint = DeepForestGreen,
                        accentTint = PureWhite,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SERTIFIKAT WILAYAH",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SageGreen,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Wilayah Berhasil Dikuasai!",
                    fontFamily = CinzelFontFamily,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Misi Stage ${state.stage}-${state.level} berhasil disurvei dan dicatat dalam jurnal kanonikal.",
                    color = DeepForestGreen.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stars Row: Hand-crafted Star Seals
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(3) { idx ->
                        val earned = idx < state.starsAwarded
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(
                                    if (earned) SandGold else Cream,
                                    CircleShape
                                )
                                .border(
                                    1.5.dp,
                                    if (earned) DeepForestGreen else SageGreen.copy(alpha = 0.5f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ExpeditionIconStarSeal(
                                tint = if (earned) DeepForestGreen else SageGreen.copy(alpha = 0.5f),
                                accentTint = if (earned) PureWhite else Cream,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${state.starsAwarded} DARI 3 BINTANG DIPEROLEH",
                    fontFamily = CinzelFontFamily,
                    color = DeepForestGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Return Action directly inside the Dossier Card lower area
                TactileSignpostButton(
                    label = "KEMBALI KE PETA",
                    onClick = onBackToMap,
                    testTag = "celebration_card_back_button",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TactileSignpostButton(
            label = "KEMBALI KE PETA PETUALANGAN",
            onClick = onBackToMap,
            testTag = "celebration_continue_button",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
