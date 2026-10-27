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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
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
                color = DeepJungleGreen,
                border = androidx.compose.foundation.BorderStroke(3.dp, GoldYellow),
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
                            .background(if (i == step) AdventureOrange else Color(0xFF285434))
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
                colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
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
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kuesioner Pengalaman Belajar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldYellow
                )
                Text(
                    text = "${currentQIndex + 1} / ${questions.size}",
                    color = ExplorerCream,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (currentQIndex + 1).toFloat() / questions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AdventureOrange,
                trackColor = Color(0xFF285434)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Question Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Pertanyaan ${currentQIndex + 1}:",
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentQ.first,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Options
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                currentQ.second.forEach { option ->
                    val isSelected = selectedAns == option
                    Surface(
                        onClick = { viewModel.submitQuestionnaireAnswer(currentQIndex, option) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF285434) else SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) GoldYellow else ForestGreen.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("option_${option.take(8)}")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldYellow else Color.Transparent)
                                    .border(2.dp, if (isSelected) GoldYellow else ExplorerCream, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = DeepJungleGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = option,
                                color = if (isSelected) Color.White else ExplorerCream,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Next / Finish Button
            Button(
                onClick = {
                    if (currentQIndex < questions.size - 1) {
                        currentQIndex++
                    } else {
                        viewModel.completeQuestionnaire()
                    }
                },
                enabled = selectedAns != null,
                colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("questionnaire_next_button")
            ) {
                Text(
                    text = if (currentQIndex < questions.size - 1) "Pertanyaan Berikutnya" else "Lanjut ke Diagnostic Test",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
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
                    Column {
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
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF285434)
                    ) {
                        Text(
                            text = "Soal ${qIndex + 1}/${questions.size}",
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow,
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
                    color = ForestGreen,
                    trackColor = Color(0xFF285434)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Diagnostic Prompt Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AdventureOrange
                            ) {
                                Text(
                                    text = "CEFR ${currentQ.cefrLevel}",
                                    color = Color.White,
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
                            color = Color.White,
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
                            color = SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2E6348)),
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
                                    color = Color(0xFF285434),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = ('A' + optIndex).toString(),
                                            fontWeight = FontWeight.Bold,
                                            color = GoldYellow
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
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(2.dp, AdventureOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Jalur Pembelajaranmu:",
                            color = ExplorerCream,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = assignedPath.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = GoldYellow
                        )
                        Text(
                            text = "Standar Target: ${assignedPath.cefrLabel} | Target: ${assignedPath.targetWordsPerDay} kata/hari",
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = assignedPath.description,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Catatan: Hasil ini adalah panduan awal belajar di Word Explorer, bukan sertifikasi resmi CEFR. Kamu tetap dapat mengulang atau berpindah jalur kapan saja.",
                    color = ExplorerCream.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.enterAdventureAfterDiagnostic() },
                    colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("enter_adventure_button")
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Masuki Peta Petualangan!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
