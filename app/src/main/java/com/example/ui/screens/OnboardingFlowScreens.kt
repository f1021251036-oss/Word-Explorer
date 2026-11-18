package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.School
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
import com.example.data.models.LearningPath
import com.example.data.seed.InitialDataSeed
import com.example.ui.theme.*
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun OnboardingScreen(viewModel: WordExplorerViewModel) {
    var step by remember { mutableStateOf(0) }

    val slides = listOf(
        Triple(
            "🗺️ Selamat Datang di Word Explorer!",
            "Game petualangan edukatif untuk siswa SMP. Taklukkan 5 dunia fantasi dan kuasai ribuan kosakata bahasa Inggris dengan seru!",
            "Next"
        ),
        Triple(
            "🧭 Peta Petualangan 3D",
            "Jelajahi Green Forest, Lost Island, Roman Ruins, Mountain Passage, dan Crystal Cave. Pecahkan teka-teki kata dan buka level berikutnya!",
            "Next"
        ),
        Triple(
            "🦊 Kuis Adaptif & Sahabat Hewan",
            "Sistem remedial adaptif memastikanmu benar-benar paham. Raih streak harian untuk membuka sahabat hewan mitologis!",
            "Mulai Kuesioner"
        )
    )

    val currentSlide = slides[step]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Forest900,
                border = androidx.compose.foundation.BorderStroke(3.dp, Sand),
                shadowElevation = 8.dp,
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (step) {
                            0 -> "🤠"
                            1 -> "🗺️"
                            else -> "🦊"
                        },
                        fontSize = 44.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = currentSlide.first,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GoldYellow,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = currentSlide.second,
                style = MaterialTheme.typography.bodyLarge,
                color = ExplorerCream,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Step Indicator Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in slides.indices) {
                    Box(
                        modifier = Modifier
                            .size(if (i == step) 24.dp else 10.dp, 10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (i == step) Honey else Forest500)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    if (step < slides.size - 1) {
                        step++
                    } else {
                        viewModel.completeOnboardingIntro()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Honey,
                    contentColor = Forest900
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_continue_button")
            ) {
                Text(
                    text = currentSlide.third,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Forest900
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Forest900)
            }
        }
    }
}

@Composable
fun QuestionnaireScreen(viewModel: WordExplorerViewModel) {
    val questions = listOf(
        Pair(
            "Berapa lama kamu telah belajar bahasa Inggris di sekolah?",
            listOf("Baru mulai di SMP", "1–2 tahun", "3–4 tahun", "Lebih dari 4 tahun")
        ),
        Pair(
            "Seberapa percaya diri kamu dengan kosakata bahasa Inggris sehari-hari?",
            listOf("Masih butuh bantuan terjemahan", "Cukup bisa kata-kata dasar", "Paham banyak kata umum", "Sangat percaya diri")
        ),
        Pair(
            "Berapa frekuensi belajar bahasa Inggris yang kamu inginkan?",
            listOf("5–10 menit sehari", "15–20 menit sehari", "30 menit sehari", "Setiap akhir pekan")
        ),
        Pair(
            "Seberapa nyaman kamu membaca kalimat atau cerita pendek dalam bahasa Inggris?",
            listOf("Lebih suka dengan arti bahasa Indonesia", "Bisa paham dengan bantuan gambar", "Nyaman membaca cerita sederhana", "Bisa membaca teks panjang")
        ),
        Pair(
            "Apa kesulitan utama yang kamu rasakan dalam belajar kosakata?",
            listOf("Mudah lupa arti kata", "Bingung menggunakannya dalam kalimat", "Pelafalan (pronunciation)", "Kurang motivasi belajar")
        ),
        Pair(
            "Apa tujuan utama belajarmu di Word Explorer?",
            listOf("Meningkatkan nilai ujian sekolah", "Bisa bercakap-cakap santai", "Membaca komik/buku bahasa Inggris", "Menjelajahi dunia game edukasi")
        )
    )

    var currentQIndex by remember { mutableStateOf(0) }
    val currentQ = questions[currentQIndex]
    val selectedAns = viewModel.questionnaireAnswers[currentQIndex]

    BackHandler(enabled = currentQIndex > 0) {
        currentQIndex--
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Forest700)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            // Fixed Top Header & Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kuesioner Pengalaman Belajar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Sand,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Forest900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                    ) {
                        Text(
                            text = "${currentQIndex + 1} / ${questions.size}",
                            color = Cream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Forest900)
                        .border(1.dp, DarkBorderSubtle, RoundedCornerShape(6.dp))
                ) {
                    LinearProgressIndicator(
                        progress = { (currentQIndex + 1).toFloat() / questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = Honey,
                        trackColor = Color.Transparent
                    )
                }
            }

            // Scrollable Question & Elevated Option Cards
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            ) {
                // Question Box
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Forest900),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkBorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "PERTANYAAN ${currentQIndex + 1} DARI ${questions.size}",
                            color = LeafBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentQ.first,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Elevated High-Contrast Options
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    currentQ.second.forEachIndexed { optIdx, option ->
                        val isSelected = selectedAns == option
                        val optionLetter = ('A' + optIdx).toString()

                        Surface(
                            onClick = { viewModel.submitQuestionnaireAnswer(currentQIndex, option) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) HoneyTint else PureWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.5.dp,
                                color = if (isSelected) HoneyPressed else Sage
                            ),
                            shadowElevation = if (isSelected) 6.dp else 3.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 56.dp)
                                .testTag("option_${option.take(8)}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) Honey else CreamSoft,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isSelected) Forest900 else Sage
                                    ),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = optionLetter,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Forest900
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = option,
                                    color = Forest900,
                                    fontSize = 15.sp,
                                    lineHeight = 21.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Terpilih",
                                        tint = Leaf,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .border(2.dp, Sage, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Docked Bottom Action Bar (Always visible & high-contrast in both states)
            Surface(
                color = Forest900,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedAns == null) {
                        Text(
                            text = "Pilih salah satu jawaban di atas untuk melanjutkan",
                            color = Cream,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentQIndex > 0) {
                            OutlinedButton(
                                onClick = { currentQIndex-- },
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Sand),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Forest700,
                                    contentColor = Sand
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 52.dp)
                                    .testTag("questionnaire_back_button")
                            ) {
                                Text(
                                    text = "Kembali",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (currentQIndex < questions.size - 1) {
                                    currentQIndex++
                                } else {
                                    viewModel.completeQuestionnaire()
                                }
                            },
                            enabled = selectedAns != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Honey,
                                contentColor = Forest900,
                                disabledContainerColor = DisabledBg,
                                disabledContentColor = DisabledFg
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (selectedAns != null) Forest900 else Sage
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 1.dp,
                                disabledElevation = 1.dp
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 52.dp)
                                .testTag("questionnaire_next_button")
                        ) {
                            Text(
                                text = if (currentQIndex < questions.size - 1) "Pertanyaan Berikutnya" else "Lanjut ke Diagnostic Test",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                color = if (selectedAns != null) Forest900 else DisabledFg
                            )
                            if (selectedAns != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Forest900,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticScreen(viewModel: WordExplorerViewModel) {
    val qIndex by viewModel.diagnosticIndex.collectAsState()
    val score by viewModel.diagnosticScore.collectAsState()
    val finished by viewModel.diagnosticFinished.collectAsState()
    val assignedPath by viewModel.recommendedPath.collectAsState()

    val questions = InitialDataSeed.DIAGNOSTIC_QUESTIONS
    val currentQ = questions.getOrNull(qIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        if (!finished && currentQ != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Diagnostic Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Diagnostic Test",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow
                        )
                        Text(
                            text = "Menentukan jalur belajar yang paling sesuai",
                            style = MaterialTheme.typography.bodySmall,
                            color = ExplorerCream
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Forest500
                    ) {
                        Text(
                            text = "Soal ${qIndex + 1}/${questions.size}",
                            fontWeight = FontWeight.Bold,
                            color = Sand,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { (qIndex + 1).toFloat() / questions.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = LeafBright,
                    trackColor = Forest500
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Diagnostic Prompt Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Forest900),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Lagoon
                            ) {
                                Text(
                                    text = "CEFR ${currentQ.cefrLevel}",
                                    color = PureWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentQ.prompt,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Options
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentQ.options.forEachIndexed { optIndex, optionText ->
                        Surface(
                            onClick = { viewModel.answerDiagnosticQuestion(optIndex) },
                            shape = RoundedCornerShape(14.dp),
                            color = Forest900,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkBorderSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("diag_opt_$optIndex")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Forest500,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = ('A' + optIndex).toString(),
                                            fontWeight = FontWeight.Bold,
                                            color = Sand
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = optionText,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Diagnostic Result Reveal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = DeepJungleGreen,
                    border = androidx.compose.foundation.BorderStroke(3.dp, GoldYellow),
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(88.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🎯", fontSize = 40.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Hasil Diagnostic Test",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = GoldYellow
                )

                Text(
                    text = "Skor Kamu: $score / ${questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = ExplorerCream,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Assigned Path Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Forest900),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Honey),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Jalur Pembelajaranmu:",
                            color = Cream,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = assignedPath.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Sand
                        )
                        Text(
                            text = "Standar Target: ${assignedPath.cefrLabel} | Target: ${assignedPath.targetWordsPerDay} kata/hari",
                            color = LeafBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = assignedPath.description,
                            color = PureWhite,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Catatan: Hasil ini adalah panduan awal belajar di Word Explorer, bukan sertifikasi resmi CEFR. Kamu tetap dapat mengulang atau berpindah jalur kapan saja.",
                    color = SageLight,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.enterAdventureAfterDiagnostic() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Honey,
                        contentColor = Forest900
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("enter_adventure_button")
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = Forest900)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Masuki Peta Petualangan!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Forest900
                    )
                }
            }
        }
    }
}
