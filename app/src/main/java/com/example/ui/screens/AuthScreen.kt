package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthScreenState
import com.example.ui.viewmodel.WordExplorerViewModel

@Composable
fun AuthScreen(viewModel: WordExplorerViewModel) {
    val authState by viewModel.authScreenState.collectAsState()
    val email by viewModel.authEmail.collectAsState()
    val password by viewModel.authPassword.collectAsState()
    val displayName by viewModel.authDisplayName.collectAsState()
    val error by viewModel.authError.collectAsState()
    val successMsg by viewModel.authSuccessMessage.collectAsState()
    val isLoading by viewModel.isAuthLoading.collectAsState()

    // Email verification state
    val verificationCode by viewModel.authVerificationCode.collectAsState()
    val activeEmail by viewModel.activeVerificationEmail.collectAsState()
    val sentCode by viewModel.activeSentCode.collectAsState()
    val cooldownSeconds by viewModel.resendCooldownSeconds.collectAsState()

    // Backend config status dialog
    val showBackendDialog by viewModel.showBackendStatusDialog.collectAsState()
    val backendStatus by viewModel.backendConfigStatus.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero World Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_explorer_world_1793115165361),
                    contentDescription = "Word Explorer World",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0F261E))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "WORD EXPLORER",
                        color = GoldYellow,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "SMP English Vocabulary Adventure Map",
                        color = ExplorerCream,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Auth Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Mode Selector Tabs (Login / Register)
                    if (authState == AuthScreenState.Login || authState == AuthScreenState.Register) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E4637))
                                .padding(4.dp)
                        ) {
                            val isLogin = authState == AuthScreenState.Login
                            Button(
                                onClick = {
                                    viewModel.authError.value = null
                                    viewModel.authSuccessMessage.value = null
                                    viewModel.authScreenState.value = AuthScreenState.Login
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLogin) AdventureOrange else Color.Transparent,
                                    contentColor = if (isLogin) Color.White else ExplorerCream
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tab_login")
                            ) {
                                Text("Masuk", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    viewModel.authError.value = null
                                    viewModel.authSuccessMessage.value = null
                                    viewModel.authScreenState.value = AuthScreenState.Register
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isLogin) AdventureOrange else Color.Transparent,
                                    contentColor = if (!isLogin) Color.White else ExplorerCream
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tab_register")
                            ) {
                                Text("Daftar", fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Feedback messages
                    error?.let {
                        Surface(
                            color = ErrorRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "⚠️ $it",
                                color = Color(0xFFFF8A80),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    successMsg?.let {
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "✓ $it",
                                color = Color(0xFFA5D6A7),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // --- SCREEN: Email Verification Mode (Real OTP Hashing) ---
                    if (authState == AuthScreenState.EmailVerification) {
                        Text(
                            text = "Verifikasi Kode OTP Email",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Masukkan 6-digit kode OTP yang dikirimkan ke Gmail Anda:",
                            fontSize = 12.sp,
                            color = ExplorerCream
                        )
                        Text(
                            text = activeEmail,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // OTP Security Notice
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x331E4637),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Keamanan Server: OTP SHA-256",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = GoldYellow
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "• Berlaku strictly 5 menit sejak dikirim\n• Maksimal 5 kali percobaan salah\n• Terikat pada akun kanonikal Word Explorer",
                                    fontSize = 11.sp,
                                    color = ExplorerCream.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // Sandbox Inspection Card (Discloses test code in sandbox if SMTP unconfigured)
                        if (!backendStatus.isReadyForRealSmtp && sentCode != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x22FFA000),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "ℹ️ Sandbox Developer Inspection",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = GoldYellow
                                    )
                                    Text(
                                        text = "SMTP belum diisi di Secrets. Kode OTP Server: $sentCode",
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Verifikasi tetap divalidasi server menggunakan enkripsi SHA-256.",
                                        fontSize = 10.sp,
                                        color = ExplorerCream.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        // 6-digit OTP code textfield
                        OutlinedTextField(
                            value = verificationCode,
                            onValueChange = { if (it.length <= 6) viewModel.authVerificationCode.value = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Kode OTP (6-Digit)") },
                            leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = GoldYellow) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldYellow,
                                unfocusedBorderColor = ForestGreen,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_verification_code_field")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.verifyEmailCode() },
                            enabled = !isLoading && verificationCode.length == 6,
                            colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("verify_code_submit_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Verifikasi OTP & Masuk", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Resend Code button with 60s cooldown timer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { viewModel.resendVerificationCode() },
                                enabled = cooldownSeconds == 0 && !isLoading,
                                modifier = Modifier.testTag("resend_code_button")
                            ) {
                                Text(
                                    text = if (cooldownSeconds > 0) "Kirim Ulang ($cooldownSeconds dtk)" else "Kirim Ulang OTP",
                                    color = if (cooldownSeconds > 0) Color.Gray else ExplorerCream,
                                    fontSize = 13.sp
                                )
                            }
                            TextButton(
                                onClick = {
                                    viewModel.authError.value = null
                                    viewModel.authSuccessMessage.value = null
                                    viewModel.authScreenState.value = AuthScreenState.Login
                                }
                            ) {
                                Text("Kembali ke Masuk", color = GoldYellow, fontSize = 13.sp)
                            }
                        }
                    } else {
                        // --- SCREEN: Login / Register / ForgotPassword Form ---

                        // Registration Display Name
                        AnimatedVisibility(visible = authState == AuthScreenState.Register) {
                            Column {
                                OutlinedTextField(
                                    value = displayName,
                                    onValueChange = { viewModel.authDisplayName.value = it },
                                    label = { Text("Nama Lengkap Siswa") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ExplorerCream) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldYellow,
                                        unfocusedBorderColor = ForestGreen,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_name_field")
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.authEmail.value = it },
                            label = { Text("Alamat Gmail Aktif") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ExplorerCream) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldYellow,
                                unfocusedBorderColor = ForestGreen,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.authPassword.value = it },
                            label = { Text(if (authState == AuthScreenState.ForgotPassword) "Kata Sandi Baru" else "Kata Sandi") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ExplorerCream) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = ExplorerCream
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldYellow,
                                unfocusedBorderColor = ForestGreen,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_field")
                        )

                        // Forgot Password Link
                        if (authState == AuthScreenState.Login) {
                            TextButton(
                                onClick = {
                                    viewModel.authError.value = null
                                    viewModel.authSuccessMessage.value = null
                                    viewModel.authScreenState.value = AuthScreenState.ForgotPassword
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("forgot_password_button")
                            ) {
                                Text(
                                    "Lupa Kata Sandi?",
                                    color = ExplorerCream,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                when (authState) {
                                    AuthScreenState.Login -> viewModel.login()
                                    AuthScreenState.Register -> viewModel.register()
                                    AuthScreenState.ForgotPassword -> viewModel.resetPassword()
                                    else -> {}
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_submit_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = when (authState) {
                                        AuthScreenState.Login -> "Masuk ke Petualangan"
                                        AuthScreenState.Register -> "Daftar Akun Baru (Kirim OTP)"
                                        AuthScreenState.ForgotPassword -> "Simpan Kata Sandi Baru"
                                        else -> "Lanjutkan"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }

                        if (authState == AuthScreenState.ForgotPassword) {
                            TextButton(
                                onClick = { viewModel.authScreenState.value = AuthScreenState.Login },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Kembali ke Masuk", color = ExplorerCream)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divider with "OR"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF285434))
                            Text(
                                text = "  ATAU  ",
                                color = ExplorerCream.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF285434))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Official Google Sign-In with Account Linking
                        OutlinedButton(
                            onClick = { viewModel.signInWithGoogle() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldYellow),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("google_signin_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌐", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Masuk dengan Google (Akun Kanonikal)", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Demo Explorer Account
                        Button(
                            onClick = { viewModel.quickDemoLogin() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("quick_demo_button")
                        ) {
                            Icon(Icons.Default.Explore, contentDescription = null, tint = GoldYellow)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Jelajahi Demo (Arka Explorer)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Backend & Security Status Disclosure Button
            OutlinedButton(
                onClick = { viewModel.toggleBackendStatusDialog() },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExplorerCream),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B6350)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("backend_status_button")
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Status Backend & Keamanan Integrasi", fontSize = 12.sp, color = ExplorerCream)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer note
            Text(
                text = "Dirancang untuk siswa SMP belajar kosakata bahasa Inggris dengan seru dan mandiri.",
                color = ExplorerCream.copy(alpha = 0.7f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }

        // Backend Configuration Status Dialog (Mandatory Disclosure)
        if (showBackendDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleBackendStatusDialog() },
                confirmButton = {
                    Button(
                        onClick = { viewModel.toggleBackendStatusDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = AdventureOrange)
                    ) {
                        Text("Tutup", fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldYellow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Status Konfigurasi Backend", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Keterbukaan Status Layanan & Kredensial:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow
                        )

                        // 1. Google OAuth
                        StatusItemRow(
                            title = "Google OAuth 2.0 Web Client ID",
                            isConfigured = backendStatus.googleOAuthClientIdConfigured,
                            details = if (backendStatus.googleOAuthClientIdConfigured) "Terkonfigurasi (${backendStatus.googleOAuthClientId?.take(12)}...)" else "Belum diisi di GOOGLE_WEB_CLIENT_ID"
                        )

                        // 2. Transactional SMTP
                        StatusItemRow(
                            title = "Layanan Pengiriman Email Transaksional (SMTP)",
                            isConfigured = backendStatus.smtpConfigured,
                            details = if (backendStatus.smtpConfigured) "Host: ${backendStatus.smtpHost}:${backendStatus.smtpPort} (${backendStatus.smtpSenderEmail})" else "Kredensial SMTP (SMTP_HOST, SMTP_USERNAME, SMTP_PASSWORD) belum diisi di Secrets."
                        )

                        // 3. OTP Security Engine
                        StatusItemRow(
                            title = "Validasi OTP Server",
                            isConfigured = true,
                            details = backendStatus.otpAlgorithm
                        )

                        // 4. Canonical Account Linking
                        StatusItemRow(
                            title = "Penautan Akun Kanonikal",
                            isConfigured = true,
                            details = backendStatus.accountLinkingStatus
                        )

                        // 5. Anti-Exploit 3 Levels/Day Limit
                        StatusItemRow(
                            title = "Batas Petualangan Harian (3 Level/Hari)",
                            isConfigured = true,
                            details = backendStatus.dailyLimitStatus
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x331E4637),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "💡 Untuk mengaktifkan SMTP nyata atau Google OAuth resmi, tambahkan kunci ke Secrets panel AI Studio.",
                                fontSize = 11.sp,
                                color = ExplorerCream,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                },
                containerColor = SurfaceDark
            )
        }
    }
}

@Composable
private fun StatusItemRow(title: String, isConfigured: Boolean, details: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isConfigured) Color(0x224A943E) else Color(0x22FFA000),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isConfigured) ForestGreen else GoldYellow.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = if (isConfigured) "AKTIF ✓" else "PENDING ⚠️",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isConfigured) Color(0xFFA5D6A7) else GoldYellow
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = details, fontSize = 11.sp, color = ExplorerCream.copy(alpha = 0.85f))
        }
    }
}
