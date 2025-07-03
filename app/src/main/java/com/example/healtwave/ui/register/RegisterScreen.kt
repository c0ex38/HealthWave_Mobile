package com.example.healtwave.ui.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.healtwave.data.remote.RegisterRequest
import com.example.healtwave.data.remote.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Animasyonlar
    val logoScale by animateFloatAsState(
        targetValue = if (isLoading) 0.8f else 1f,
        animationSpec = tween(300),
        label = "logoScale"
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0.6f else 1f,
        animationSpec = tween(300),
        label = "logoAlpha"
    )

    val animatedSpacerTop by animateDpAsState(targetValue = 24.dp, label = "spacerTop")
    val animatedSpacerBetweenFields by animateDpAsState(targetValue = 12.dp, label = "spacerBetweenFields")
    val animatedSpacerButton by animateDpAsState(targetValue = 24.dp, label = "spacerButton")
    val animatedSpacerError by animateDpAsState(targetValue = if (showError) 12.dp else 0.dp, label = "spacerError")
    val animatedSpacerSuccess by animateDpAsState(targetValue = if (message.isNotEmpty()) 16.dp else 0.dp, label = "spacerSuccess")
    val animatedSpacerBack by animateDpAsState(targetValue = 16.dp, label = "spacerBack")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo ve Başlık Alanı
            Card(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "H+",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Uygulama Başlığı
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(800))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kayıt Ol",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "HealthWave ailesine katılın",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(animatedSpacerTop))

            // Kayıt Formu
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hesap Oluştur",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Kişisel Bilgiler Bölümü
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Ad
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { -100 },
                                animationSpec = tween(500)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = {
                                    firstName = it
                                    showError = false
                                },
                                label = { Text("Ad") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Soyad
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { -100 },
                                animationSpec = tween(550)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = {
                                    lastName = it
                                    showError = false
                                },
                                label = { Text("Soyad") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(animatedSpacerBetweenFields))

                    // Kullanıcı Adı
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { -100 },
                            animationSpec = tween(600)
                        )
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                showError = false
                            },
                            label = { Text("Kullanıcı Adı") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(animatedSpacerBetweenFields))

                    // E-posta
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { -100 },
                            animationSpec = tween(650)
                        )
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                showError = false
                            },
                            label = { Text("E-posta") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(animatedSpacerBetweenFields))

                    // Şifre
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { -100 },
                            animationSpec = tween(700)
                        )
                    ) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                showError = false
                            },
                            label = { Text("Şifre") },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Şifreyi Gizle" else "Şifreyi Göster"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(animatedSpacerBetweenFields))

                    // Şifre Tekrar
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { -100 },
                            animationSpec = tween(750)
                        )
                    ) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                showError = false
                            },
                            label = { Text("Şifre Tekrar") },
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (confirmPasswordVisible) "Şifreyi Gizle" else "Şifreyi Göster"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(animatedSpacerError))

                    // Hata mesajı
                    AnimatedVisibility(
                        visible = showError,
                        enter = fadeIn(animationSpec = tween(300))
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "⚠️ $errorMessage",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(animatedSpacerSuccess))

                    // Başarı mesajı
                    AnimatedVisibility(
                        visible = message.isNotEmpty() && !showError,
                        enter = fadeIn(animationSpec = tween(500))
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "✅ $message",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(animatedSpacerButton))

                    // Kayıt Ol Butonu/Yükleme
                    AnimatedContent(
                        targetState = isLoading,
                        label = "registerButtonContent"
                    ) { loading ->
                        if (loading) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Hesap oluşturuluyor...",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    // Form doğrulama
                                    when {
                                        username.isBlank() -> {
                                            showError = true
                                            errorMessage = "Kullanıcı adı boş olamaz"
                                        }
                                        email.isBlank() -> {
                                            showError = true
                                            errorMessage = "E-posta boş olamaz"
                                        }
                                        firstName.isBlank() -> {
                                            showError = true
                                            errorMessage = "Ad boş olamaz"
                                        }
                                        lastName.isBlank() -> {
                                            showError = true
                                            errorMessage = "Soyad boş olamaz"
                                        }
                                        password.length < 6 -> {
                                            showError = true
                                            errorMessage = "Şifre en az 6 karakter olmalıdır"
                                        }
                                        password != confirmPassword -> {
                                            showError = true
                                            errorMessage = "Şifreler eşleşmiyor"
                                        }
                                        else -> {
                                            isLoading = true
                                            showError = false
                                            coroutineScope.launch {
                                                try {
                                                    val response = RetrofitInstance.api.register(
                                                        RegisterRequest(
                                                            username = username,
                                                            email = email,
                                                            password = password,
                                                            password_confirm = confirmPassword,
                                                            first_name = firstName,
                                                            last_name = lastName
                                                        )
                                                    )
                                                    message = response.message
                                                } catch (e: retrofit2.HttpException) {
                                                    val errorBody = e.response()?.errorBody()?.string()
                                                    showError = true
                                                    errorMessage = when (e.code()) {
                                                        400 -> "Geçersiz bilgiler. Lütfen kontrol edin."
                                                        409 -> "Bu kullanıcı adı veya e-posta zaten kullanılıyor"
                                                        else -> "Kayıt olurken bir hata oluştu"
                                                    }
                                                    println("❌ HTTP error: ${e.code()} ${e.message()}")
                                                    println("❌ Error body: $errorBody")
                                                } catch (e: Exception) {
                                                    showError = true
                                                    errorMessage = "Bağlantı hatası. Lütfen tekrar deneyin."
                                                    println("❌ Unknown error: ${e.localizedMessage}")
                                                    e.printStackTrace()
                                                }
                                                isLoading = false
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text(
                                    text = "Kayıt Ol",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(animatedSpacerBack))

            // Geri Dön Bağlantısı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                TextButton(
                    onClick = { onNavigateBack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Zaten hesabınız var mı? Giriş Yapın",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}