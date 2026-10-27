package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_explorer_world_1793115165361),
                    contentDescription = "Word Explorer World",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0F261E))
                            )
                        )
                )
                // Logo Title overlay
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
                        text = "SMP English Vocabulary Adventure",
                        color = ExplorerCream,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Card
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
                    if (authState != AuthScreenState.ForgotPassword) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E4637))
                                .padding(4.dp)
                        ) {
                            val isLogin = authState == AuthScreenState.Login
                            Button(
                                onClick = { viewModel.authScreenState.value = AuthScreenState.Login },
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
                                onClick = { viewModel.authScreenState.value = AuthScreenState.Register },
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

                    // Registration Display Name
                    AnimatedVisibility(visible = authState == AuthScreenState.Register) {
                        Column {
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { viewModel.authDisplayName.value = it },
                                label = { Text("Nama Penjelajah") },
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
                        label = { Text("Email Siswa") },
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
                            onClick = { viewModel.authScreenState.value = AuthScreenState.ForgotPassword },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Action Button
                    Button(
                        onClick = {
                            when (authState) {
                                AuthScreenState.Login -> viewModel.login()
                                AuthScreenState.Register -> viewModel.register()
                                AuthScreenState.ForgotPassword -> viewModel.resetPassword()
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
                                    AuthScreenState.Login -> "Mulai Petualangan"
                                    AuthScreenState.Register -> "Daftar Akun Baru"
                                    AuthScreenState.ForgotPassword -> "Simpan Kata Sandi Baru"
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

                    Spacer(modifier = Modifier.height(18.dp))

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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google Sign In (One-Tap styled)
                    OutlinedButton(
                        onClick = { viewModel.quickDemoLogin() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4C7566)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("google_login_button")
                    ) {
                        Text("🌐 Masuk dengan Google", fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Demo Explorer Button
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
                        Text("Jelajahi Demo Langsung (Arka Explorer)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer note
            Text(
                text = "Dirancang untuk siswa SMP belajar kosakata bahasa Inggris dengan seru dan mandiri.",
                color = ExplorerCream.copy(alpha = 0.7f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
