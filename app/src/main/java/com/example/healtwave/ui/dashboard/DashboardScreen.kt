package com.example.healtwave.ui.dashboard

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healtwave.data.remote.RetrofitInstance
import com.example.healtwave.utils.AudioManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Dashboard istatistik veri sınıfı
data class DashboardStats(
    val surveys_count: Int = 0,
    val sessions_count: Int = 0,
    val reports_count: Int = 0,
    val chatbot_logs_count: Int = 0,
    val last_activity: String = "",
    val member_since: String = "",
    val total_listening_time: Int = 0,
    val most_used_frequency: String = ""
)

// Günlük log veri sınıfları
data class DailyLogRequest(
    val title: String,
    val content: String,
    val stress: Int,
    val sleep_duration: Double,
    val restfulness: Int,
    val pulse: Int,
    val focus: Int,
    val mood: String,
    val physical_activity: String
)

data class DailyLogResponse(
    val id: Int,
    val user_name: String,
    val date_formatted: String,
    val date: String,
    val created_at: String,
    val note: String?,
    val stress: Int,
    val sleep_duration: Double,
    val restfulness: Int,
    val pulse: Int,
    val focus: Int,
    val mood: String,
    val physical_activity: String,
    val user: Int
)

data class DailyLogsResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<DailyLogResponse>
)

// Session veri sınıfları
data class SessionRequest(
    val frequency: Int,
    val duration_seconds: Int,
    val completed: Boolean,
    val rating: Int
)

data class SessionResponse(
    val id: Int,
    val user_name: String,
    val duration_formatted: String,
    val listened_at_formatted: String,
    val frequency_name: String,
    val listened_at: String,
    val duration_seconds: Int,
    val completed: Boolean,
    val rating: Int,
    val user: Int,
    val frequency: Int
)

data class SessionsResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<SessionResponse>
)

// Frekans bilgileri
data class FrequencyOption(
    val id: Int,
    val name: String,
    val description: String
)

@Composable
fun DashboardScreen(
    userName: String,
    onLogout: () -> Unit
) {
    var dashboardStats by remember { mutableStateOf(DashboardStats()) }
    var isLoading by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var showDailyLogDialog by remember { mutableStateOf(false) }
    var showDailyLogsHistory by remember { mutableStateOf(false) }
    var showNewSessionDialog by remember { mutableStateOf(false) }
    var showSessionsHistory by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // API çağrısı
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // TODO: API entegrasyonu yapılacak
                // val response = RetrofitInstance.api.getDashboardStats()
                // dashboardStats = response
                
                // Şimdilik mock data
                dashboardStats = DashboardStats(
                    surveys_count = 1,
                    sessions_count = 2,
                    reports_count = 2,
                    chatbot_logs_count = 1,
                    last_activity = "2025-07-03T15:40:17.927208Z",
                    member_since = "2025-07-02T23:05:23.120946Z",
                    total_listening_time = 302,
                    most_used_frequency = "La - Kendine Dönüş"
                )
                isLoading = false
            } catch (e: Exception) {
                showError = true
                isLoading = false
                e.printStackTrace()
            }
        }
    }

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
        if (isLoading) {
            // Yükleme durumu
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Dashboard yükleniyor...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Header
                    DashboardHeader(userName = userName, onLogout = onLogout)
                }
                
                item {
                    // Hoşgeldin kartı
                    WelcomeCard(
                        userName = userName,
                        memberSince = dashboardStats.member_since,
                        lastActivity = dashboardStats.last_activity
                    )
                }
                
                item {
                    // İstatistik kartları
                    StatisticsGrid(dashboardStats = dashboardStats)
                }
                
                item {
                    // Dinleme bilgileri
                    ListeningInfoCard(
                        totalTime = dashboardStats.total_listening_time,
                        mostUsedFrequency = dashboardStats.most_used_frequency
                    )
                }
                
                item {
                    // Hızlı erişim
                    QuickAccessCard(
                        onNewDailyLog = { showDailyLogDialog = true },
                        onViewHistory = { showDailyLogsHistory = true },
                        onNewSession = { showNewSessionDialog = true },
                        onViewSessions = { showSessionsHistory = true }
                    )
                }
            }
        }
        
        // Günlük Log Dialog
        if (showDailyLogDialog) {
            DailyLogDialog(
                onDismiss = { showDailyLogDialog = false },
                onSave = { dailyLog ->
                    coroutineScope.launch {
                        try {
                            // TODO: API entegrasyonu
                            // RetrofitInstance.api.createDailyLog(dailyLog)
                            showDailyLogDialog = false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
        }
        
        // Günlük Geçmişi Dialog
        if (showDailyLogsHistory) {
            DailyLogsHistoryDialog(
                onDismiss = { showDailyLogsHistory = false }
            )
        }
        
        // Yeni Session Dialog
        if (showNewSessionDialog) {
            NewSessionDialog(
                onDismiss = { showNewSessionDialog = false },
                onSave = { sessionRequest ->
                    coroutineScope.launch {
                        try {
                            // TODO: API entegrasyonu
                            // RetrofitInstance.api.createSession(sessionRequest)
                            showNewSessionDialog = false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
        }
        
        // Sessions Geçmişi Dialog
        if (showSessionsHistory) {
            SessionsHistoryDialog(
                onDismiss = { showSessionsHistory = false }
            )
        }
    }
}

@Composable
fun DashboardHeader(
    userName: String,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Card(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "HealthWave",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Text(
                        text = "Hoş geldin, $userName",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    )
                }
            }
            
            // Çıkış butonu
            IconButton(
                onClick = onLogout,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Çıkış",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun WelcomeCard(
    userName: String,
    memberSince: String,
    lastActivity: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sağlık Yolculuğun",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "HealthWave ile sağlıklı yaşam yolculuğunda ilerliyorsun. Bugün de kendine iyi bak! 🌊",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Üye olma tarihi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = formatDate(memberSince),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Son aktivite",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = formatDate(lastActivity),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticsGrid(dashboardStats: DashboardStats) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "İstatistikler",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Anket",
                    value = dashboardStats.surveys_count.toString(),
                    icon = Icons.Default.Assignment,
                    color = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Seans",
                    value = dashboardStats.sessions_count.toString(),
                    icon = Icons.Default.PlayArrow,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Rapor",
                    value = dashboardStats.reports_count.toString(),
                    icon = Icons.Default.BarChart,
                    color = MaterialTheme.colorScheme.tertiary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Chat",
                    value = dashboardStats.chatbot_logs_count.toString(),
                    icon = Icons.Default.Chat,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ListeningInfoCard(
    totalTime: Int,
    mostUsedFrequency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Headset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dinleme Bilgileri",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Toplam Dinleme Süresi",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "${totalTime / 60} dakika ${totalTime % 60} saniye",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column {
                Text(
                    text = "En Çok Kullanılan Frekans",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = mostUsedFrequency,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}

@Composable
fun QuickAccessCard(
    onNewDailyLog: () -> Unit = {},
    onViewHistory: () -> Unit = {},
    onNewSession: () -> Unit = {},
    onViewSessions: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hızlı Erişim",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Günlük Doldur",
                        icon = Icons.Default.Assignment,
                        onClick = onNewDailyLog
                    )
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Yeni Seans",
                        icon = Icons.Default.PlayArrow,
                        onClick = onNewSession
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Günlük Geçmiş",
                        icon = Icons.Default.History,
                        onClick = onViewHistory
                    )
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Seans Geçmiş",
                        icon = Icons.Default.Headset,
                        onClick = onViewSessions
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Raporlar",
                        icon = Icons.Default.Analytics,
                        onClick = { /* TODO */ }
                    )
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Sohbet",
                        icon = Icons.Default.Chat,
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Tarih formatlama fonksiyonu
fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Bilinmiyor"
    }
}

// Günlük doldurmak için dialog
@Composable
fun DailyLogDialog(
    onDismiss: () -> Unit,
    onSave: (DailyLogRequest) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var stress by remember { mutableStateOf(5f) }
    var sleepDuration by remember { mutableStateOf(8f) }
    var restfulness by remember { mutableStateOf(5f) }
    var pulse by remember { mutableStateOf(70f) }
    var focus by remember { mutableStateOf(5f) }
    var mood by remember { mutableStateOf("") }
    var physicalActivity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Günlük Doldur",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Başlık") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("İçerik") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                item {
                    Column {
                        Text(
                            text = "Stres Seviyesi: ${stress.toInt()}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Slider(
                            value = stress,
                            onValueChange = { stress = it },
                            valueRange = 1f..10f,
                            steps = 8
                        )
                    }
                }
                
                item {
                    Column {
                        Text(
                            text = "Uyku Süresi: ${sleepDuration.toInt()} saat",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Slider(
                            value = sleepDuration,
                            onValueChange = { sleepDuration = it },
                            valueRange = 1f..12f,
                            steps = 10
                        )
                    }
                }
                
                item {
                    Column {
                        Text(
                            text = "Dinlenme Kalitesi: ${restfulness.toInt()}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Slider(
                            value = restfulness,
                            onValueChange = { restfulness = it },
                            valueRange = 1f..10f,
                            steps = 8
                        )
                    }
                }
                
                item {
                    Column {
                        Text(
                            text = "Nabız: ${pulse.toInt()} bpm",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Slider(
                            value = pulse,
                            onValueChange = { pulse = it },
                            valueRange = 50f..120f,
                            steps = 69
                        )
                    }
                }
                
                item {
                    Column {
                        Text(
                            text = "Odaklanma: ${focus.toInt()}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Slider(
                            value = focus,
                            onValueChange = { focus = it },
                            valueRange = 1f..10f,
                            steps = 8
                        )
                    }
                }
                
                item {
                    OutlinedTextField(
                        value = mood,
                        onValueChange = { mood = it },
                        label = { Text("Ruh Hali") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = physicalActivity,
                        onValueChange = { physicalActivity = it },
                        label = { Text("Fiziksel Aktivite") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(
                            DailyLogRequest(
                                title = title,
                                content = content,
                                stress = stress.toInt(),
                                sleep_duration = sleepDuration.toDouble(),
                                restfulness = restfulness.toInt(),
                                pulse = pulse.toInt(),
                                focus = focus.toInt(),
                                mood = mood,
                                physical_activity = physicalActivity
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("İptal")
            }
        }
    )
}

// Günlük geçmişi dialog
@Composable
fun DailyLogsHistoryDialog(
    onDismiss: () -> Unit
) {
    var dailyLogs by remember { mutableStateOf<List<DailyLogResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // TODO: API entegrasyonu
                // val response = RetrofitInstance.api.getDailyLogs()
                // dailyLogs = response.results
                
                // Mock data
                dailyLogs = listOf(
                    DailyLogResponse(
                        id = 5,
                        user_name = "admin",
                        date_formatted = "03/07/2025",
                        date = "2025-07-03",
                        created_at = "2025-07-03T01:30:59.436713Z",
                        note = null,
                        stress = 3,
                        sleep_duration = 7.5,
                        restfulness = 8,
                        pulse = 70,
                        focus = 7,
                        mood = "Enerjim iyi.",
                        physical_activity = "Sabah koşusu yaptım.",
                        user = 2
                    )
                )
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                e.printStackTrace()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Günlük Geçmişi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(dailyLogs.size) { index ->
                        val log = dailyLogs[index]
                        DailyLogItem(log = log)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Kapat")
            }
        }
    )
}

@Composable
fun DailyLogItem(log: DailyLogResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.date_formatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = when {
                            log.stress <= 3 -> Color.Green
                            log.stress <= 6 -> Color.Yellow
                            else -> Color.Red
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Stres: ${log.stress}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = log.mood,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🛌 ${log.sleep_duration}h",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "❤️ ${log.pulse} bpm",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "🎯 ${log.focus}/10",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "😴 ${log.restfulness}/10",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (log.physical_activity.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🏃‍♂️ ${log.physical_activity}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}

// Yeni session oluşturmak için dialog - AudioManager ile geliştirilmiş
@Composable
fun NewSessionDialog(
    onDismiss: () -> Unit,
    onSave: (SessionRequest) -> Unit
) {
    var selectedFrequency by remember { mutableStateOf(1) }
    var duration by remember { mutableStateOf(5f) } // dakika
    var isPlaying by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf(0) } // saniye
    var sessionCompleted by remember { mutableStateOf(false) }
    var showRating by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(5f) }
    var currentProgress by remember { mutableStateOf(0f) }
    
    val context = LocalContext.current
    val audioManager = remember { AudioManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Audio durumlarını izle
    val isLoading by audioManager.isLoading.collectAsState()
    val hasError by audioManager.hasError.collectAsState()
    val errorMessage by audioManager.errorMessage.collectAsState()

    // Cleanup AudioManager when component is disposed
    DisposableEffect(Unit) {
        onDispose {
            audioManager.releasePlayer()
        }
    }

    // Mock frekans seçenekleri
    val frequencies = listOf(
        FrequencyOption(1, "Ut (Do) - Korku ve Suçluluk Direnci", "396.0 Hz - Korku Direnci"),
        FrequencyOption(2, "Re - Değişim ve Dönüşüm", "417.0 Hz - Değişim"),
        FrequencyOption(3, "Mi - Mucizeler ve Şifa", "528.0 Hz - Şifa"),
        FrequencyOption(4, "Fa - Frekans Dengeleme", "639.0 Hz - İlişkiler"),
        FrequencyOption(5, "Sol - İfade ve Çözümler", "741.0 Hz - İfade"),
        FrequencyOption(6, "La - Kendine Dönüş", "852.0 Hz - Farkındalık")
    )

    // Timer effect
    LaunchedEffect(isPlaying, remainingTime) {
        if (isPlaying && remainingTime > 0) {
            kotlinx.coroutines.delay(1000)
            remainingTime -= 1
            val totalSeconds = (duration * 60).toInt()
            currentProgress = (totalSeconds - remainingTime) / totalSeconds.toFloat()
            
            if (remainingTime <= 0) {
                isPlaying = false
                sessionCompleted = true
                showRating = true
                audioManager.stop()
            }
        }
    }

    fun startSession() {
        remainingTime = (duration * 60).toInt()
        currentProgress = 0f
        
        audioManager.prepareAudio(selectedFrequency) {
            audioManager.start()
            isPlaying = true
        }
    }

    fun pauseSession() {
        isPlaying = false
        audioManager.pause()
    }

    fun resumeSession() {
        isPlaying = true
        audioManager.start()
    }

    fun stopSession() {
        isPlaying = false
        remainingTime = 0
        currentProgress = 0f
        showRating = true
        audioManager.stop()
    }

    fun retryConnection() {
        audioManager.retry(selectedFrequency) {
            if (isPlaying) {
                audioManager.start()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (sessionCompleted) "Seans Tamamlandı!" else "Dinleme Seansı",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            if (showRating) {
                // Değerlendirme ekranı
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Text(
                        text = "Seansınız başarıyla tamamlandı!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = frequencies.find { it.id == selectedFrequency }?.name ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Süre: ${duration.toInt()} dakika",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Bu seansı nasıl değerlendiriyorsunuz?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                    
                    Text(
                        text = "Değerlendirme: ${rating.toInt()}/10",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    Slider(
                        value = rating,
                        onValueChange = { rating = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(rating.toInt()) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        repeat(10 - rating.toInt()) {
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            } else if (isPlaying || remainingTime > 0 || isLoading) {
                // Çalıyor ekranı veya yükleme ekranı
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = frequencies.find { it.id == selectedFrequency }?.name ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = frequencies.find { it.id == selectedFrequency }?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isLoading) {
                        // Yükleme durumu
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Ses dosyası yükleniyor...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    } else if (hasError) {
                        // Hata durumu
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Bağlantı Hatası",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            )
                            Text(
                                text = errorMessage.ifEmpty { "Ses dosyası yüklenemedi. İnternet bağlantınızı kontrol edin." },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { retryConnection() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tekrar Dene")
                            }
                        }
                    } else {
                        // Normal çalma durumu
                        // Progress Circle
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = currentProgress,
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 6.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${remainingTime / 60}:${String.format("%02d", remainingTime % 60)}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "kalan",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Kontrol butonları
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(
                                onClick = { 
                                    if (isPlaying) {
                                        pauseSession() 
                                    } else {
                                        resumeSession()
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Duraklat" else "Devam Et",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            IconButton(
                                onClick = { stopSession() },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Durdur",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        
                        LinearProgressIndicator(
                            progress = currentProgress,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                // Başlangıç ekranı
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    item {
                        Text(
                            text = "Frekans Seçimi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    
                    items(frequencies.size) { index ->
                        val frequency = frequencies[index]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedFrequency == frequency.id) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            ),
                            onClick = { selectedFrequency = frequency.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = if (selectedFrequency == frequency.id) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = frequency.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = if (selectedFrequency == frequency.id) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    )
                                    Text(
                                        text = frequency.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (selectedFrequency == frequency.id) {
                                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Süre: ${duration.toInt()} dakika",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Slider(
                                    value = duration,
                                    onValueChange = { duration = it },
                                    valueRange = 1f..60f,
                                    steps = 58
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (showRating) {
                Button(
                    onClick = {
                        onSave(
                            SessionRequest(
                                frequency = selectedFrequency,
                                duration_seconds = (duration * 60).toInt(),
                                completed = true,
                                rating = rating.toInt()
                            )
                        )
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kaydet")
                }
            } else if (isPlaying || remainingTime > 0) {
                // Çalışırken buton gösterme
                null
            } else {
                Button(
                    onClick = { startSession() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Seansı Başlat")
                }
            }
        },
        dismissButton = {
            if (showRating) {
                TextButton(
                    onClick = {
                        // Değerlendirme yapmadan kaydet
                        onSave(
                            SessionRequest(
                                frequency = selectedFrequency,
                                duration_seconds = (duration * 60).toInt(),
                                completed = true,
                                rating = 5 // Varsayılan değer
                            )
                        )
                        onDismiss()
                    }
                ) {
                    Text("Değerlendirme Yapmadan Kaydet")
                }
            } else {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("İptal")
                }
            }
        }
    )
}

// Sessions geçmişi dialog
@Composable
fun SessionsHistoryDialog(
    onDismiss: () -> Unit
) {
    var sessions by remember { mutableStateOf<List<SessionResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // TODO: API entegrasyonu
                // val response = RetrofitInstance.api.getSessions()
                // sessions = response.results
                
                // Mock data
                sessions = listOf(
                    SessionResponse(
                        id = 3,
                        user_name = "admin",
                        duration_formatted = "00:02",
                        listened_at_formatted = "03/07/2025 01:55",
                        frequency_name = "La - Kendine Dönüş - 852.0 Hz - Farkındalık",
                        listened_at = "2025-07-03T01:55:02.695207Z",
                        duration_seconds = 2,
                        completed = false,
                        rating = 5,
                        user = 2,
                        frequency = 6
                    ),
                    SessionResponse(
                        id = 2,
                        user_name = "admin",
                        duration_formatted = "05:00",
                        listened_at_formatted = "03/07/2025 01:28",
                        frequency_name = "Ut (Do) - Korku ve Suçluluk Direnci - 396.0 Hz - Korku Direnci",
                        listened_at = "2025-07-03T01:28:29.096599Z",
                        duration_seconds = 300,
                        completed = true,
                        rating = 4,
                        user = 2,
                        frequency = 1
                    )
                )
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                e.printStackTrace()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Headset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dinleme Geçmişi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(sessions.size) { index ->
                        val session = sessions[index]
                        SessionItem(session = session)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Kapat")
            }
        }
    )
}

@Composable
fun SessionItem(session: SessionResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.listened_at_formatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (session.completed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (session.completed) Color.Green else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (session.completed) "Tamamlandı" else "Yarım",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = session.frequency_name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱️ ${session.duration_formatted}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(session.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    repeat(10 - session.rating) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${session.rating}/10",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}