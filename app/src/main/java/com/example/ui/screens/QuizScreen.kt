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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (!quizState.isQuizFinished) {
            // Main 10-Question Quiz Mode
            MainQuizContent(
                state = quizState,
                onOptionSelected = { viewModel.selectQuizOption(it) },
                onCheckAnswer = { viewModel.checkQuizAnswer() },
                onNextQuestion = { viewModel.nextQuizQuestion() },
                onExit = { viewModel.exitQuizToMap() }
            )
        } else if (quizState.isRemedialMode && !quizState.isLevelCompleted) {
            // Adaptive Remedial Obligation Mode
            RemedialTaskContent(
                state = quizState,
                onOptionSelected = { viewModel.selectRemedialOption(it) },
                onCheckAnswer = { viewModel.checkRemedialAnswer() },
                onRetry = { viewModel.retryRemedialTask() },
                onNextTask = { viewModel.nextRemedialTask() }
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
            IconButton(
                onClick = onExit,
                modifier = Modifier.testTag("exit_quiz_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit Quiz", tint = ExplorerCream)
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
                    text = "Score: ${state.firstAttemptScore}",
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

        Spacer(modifier = Modifier.height(24.dp))

        // Question Type Badge & Question Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ForestGreen
                ) {
                    Text(
                        text = when (currentQ.type) {
                            QuestionType.MULTIPLE_CHOICE -> "Pilihan Ganda"
                            QuestionType.FILL_BLANK -> "Lengkapi Kalimat"
                            QuestionType.WORD_MATCHING -> "Cocokkan Arti"
                            QuestionType.WORD_UNSCRAMBLE -> "Susun Huruf"
                            QuestionType.CONTEXT_CHOICE -> "Pilihan Konteks"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentQ.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 24.sp
                )

                currentQ.subText?.let { sub ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ExplorerCream,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Options List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentQ.options.forEachIndexed { idx, opt ->
                val isSelected = state.selectedOption == opt
                val showResult = state.isAnswerChecked
                val isCorrectAnswer = opt == currentQ.correctAnswer

                val backgroundColor = when {
                    showResult && isCorrectAnswer -> SuccessGreen.copy(alpha = 0.35f)
                    showResult && isSelected && !state.isAnswerCorrect -> ErrorRed.copy(alpha = 0.35f)
                    isSelected -> Color(0xFF285434)
                    else -> SurfaceDark
                }

                val borderColor = when {
                    showResult && isCorrectAnswer -> SuccessGreen
                    showResult && isSelected && !state.isAnswerCorrect -> ErrorRed
                    isSelected -> GoldYellow
                    else -> ForestGreen.copy(alpha = 0.4f)
                }

                Surface(
                    onClick = { if (!state.isAnswerChecked) onOptionSelected(opt) },
                    shape = RoundedCornerShape(14.dp),
                    color = backgroundColor,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_option_$idx")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when {
                                showResult && isCorrectAnswer -> SuccessGreen
                                showResult && isSelected && !state.isAnswerCorrect -> ErrorRed
                                isSelected -> GoldYellow
                                else -> Color(0xFF285434)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ('A' + idx).toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected && !showResult) DeepJungleGreen else Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = opt,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Feedback Explanation Banner
        AnimatedVisibility(visible = state.isAnswerChecked) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (state.isAnswerCorrect) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (state.isAnswerCorrect) SuccessGreen else ErrorRed
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (state.isAnswerCorrect) "🎉 Benar Sekali!" else "❌ Kurang Tepat!",
                        fontWeight = FontWeight.Bold,
                        color = if (state.isAnswerCorrect) Color(0xFFA5D6A7) else Color(0xFFFF8A80),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQ.explanation,
                        color = ExplorerCream,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(20.dp))

        // Check / Next Action Button
        if (!state.isAnswerChecked) {
            Button(
                onClick = onCheckAnswer,
                enabled = state.selectedOption != null,
                colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("quiz_check_button")
            ) {
                Text("Periksa Jawaban", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        } else {
            Button(
                onClick = onNextQuestion,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.currentQuestionIndex < totalQuestions - 1) AdventureOrange else ForestGreen
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("quiz_next_button")
            ) {
                Text(
                    text = if (state.currentQuestionIndex < totalQuestions - 1) "Soal Berikutnya" else "Selesaikan Kuis Utama",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

// Adaptive Remedial Obligation Mode Screen
@Composable
fun RemedialTaskContent(
    state: QuizUiState,
    onOptionSelected: (String) -> Unit,
    onCheckAnswer: () -> Unit,
    onRetry: () -> Unit,
    onNextTask: () -> Unit
) {
    val currentTask = state.remedialTasks.getOrNull(state.currentRemedialIndex) ?: return
    val remedialQ = currentTask.question

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Remedial Alert Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF6B3A0E),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AdventureOrange),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚠️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kewajiban Remedial Adaptif",
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kamu memiliki ${state.initialRemedialCount} kewajiban remedial berdasarkan kesalahan pada percobaan pertama. Selesaikan seluruhnya untuk membuka level berikutnya!",
                    color = ExplorerCream,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress among remedial tasks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tugas Remedial: ${state.currentRemedialIndex + 1} dari ${state.remedialTasks.size}",
                color = ExplorerCream,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DeepJungleGreen
            ) {
                Text(
                    text = "Fokus: ${currentTask.failedWord}",
                    color = GoldYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { (state.currentRemedialIndex + 1).toFloat() / state.remedialTasks.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = GoldYellow,
            trackColor = Color(0xFF285434)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Remedial Question Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = remedialQ.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldYellow
                )
                remedialQ.subText?.let { sub ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ExplorerCream,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            remedialQ.options.forEachIndexed { idx, opt ->
                val isSelected = state.remedialSelectedOption == opt
                val showResult = state.isRemedialChecked
                val isCorrect = opt == remedialQ.correctAnswer

                val bg = when {
                    showResult && isCorrect -> SuccessGreen.copy(alpha = 0.35f)
                    showResult && isSelected && !state.isRemedialCorrect -> ErrorRed.copy(alpha = 0.35f)
                    isSelected -> Color(0xFF285434)
                    else -> SurfaceDark
                }

                Surface(
                    onClick = { if (!state.isRemedialChecked) onOptionSelected(opt) },
                    shape = RoundedCornerShape(14.dp),
                    color = bg,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) GoldYellow else ForestGreen.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("remedial_opt_$idx")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) GoldYellow else Color(0xFF285434),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ('A' + idx).toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DeepJungleGreen else Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = opt,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Remedial Feedback
        AnimatedVisibility(visible = state.isRemedialChecked) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (state.isRemedialCorrect) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (state.isRemedialCorrect) "✓ Hebat! Konsep berhasil dikuasai!" else "Belum tepat, coba pahami kembali kata ini.",
                        fontWeight = FontWeight.Bold,
                        color = if (state.isRemedialCorrect) Color(0xFFA5D6A7) else Color(0xFFFF8A80)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = remedialQ.explanation,
                        color = ExplorerCream,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(20.dp))

        // Remedial Action Buttons
        if (!state.isRemedialChecked) {
            Button(
                onClick = onCheckAnswer,
                enabled = state.remedialSelectedOption != null,
                colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("remedial_check_button")
            ) {
                Text("Konfirmasi Jawaban Remedial", fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else if (!state.isRemedialCorrect) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC67D22)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("remedial_retry_button")
            ) {
                Text("Coba Lagi Soal Ini", fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            Button(
                onClick = onNextTask,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("remedial_next_button")
            ) {
                Text(
                    text = if (state.currentRemedialIndex < state.remedialTasks.size - 1) "Tugas Remedial Selanjutnya" else "Selesaikan Level Ini",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// Celebration Screen upon completing level & all remedial obligations
@Composable
fun LevelCelebrationContent(
    state: QuizUiState,
    onBackToMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = DeepJungleGreen,
            border = androidx.compose.foundation.BorderStroke(3.dp, GoldYellow),
            shadowElevation = 8.dp,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🏆", fontSize = 48.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Level Selesai!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = GoldYellow
        )

        Text(
            text = "Stage ${state.stage}-${state.level} Berhasil Ditaklukkan!",
            style = MaterialTheme.typography.titleMedium,
            color = ExplorerCream
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Stars Display (Strictly based on first attempt!)
        Row(horizontalArrangement = Arrangement.Center) {
            for (s in 1..3) {
                val earned = s <= state.starsAwarded
                Text(
                    text = if (earned) "⭐" else "☆",
                    fontSize = 44.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = when (state.starsAwarded) {
                3 -> "Sempurna! 10/10 pada percobaan pertama!"
                2 -> "Bagus! Kamu meraih 2 bintang."
                1 -> "Bagus! Terus tingkatkan kemampuanmu."
                else -> "Level berhasil diselesaikan setelah latihan remedial!"
            },
            color = ExplorerCream,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Score Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Skor Percobaan 1", color = ExplorerCream.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text("${state.firstAttemptScore} / 10", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Remedial Selesai", color = ExplorerCream.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text("${state.initialRemedialCount}", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Status Level", color = ExplorerCream.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text("UNLOCKED", color = Color(0xFFA5D6A7), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackToMap,
            colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("celebration_continue_button")
        ) {
            Icon(Icons.Default.Map, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Kembali ke Peta Petualangan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }
    }
}
