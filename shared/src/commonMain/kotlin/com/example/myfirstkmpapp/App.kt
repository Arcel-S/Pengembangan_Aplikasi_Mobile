package com.example.myfirstkmpapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstkmpapp.newsfeed.DisplayNewsItem
import com.example.myfirstkmpapp.newsfeed.NewsFeedManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Antarmuka Utama Aplikasi News Feed Simulator
 * Tugas 2: Pemrograman Aplikasi Mobile (IF25-22017) ITERA
 * Topik: Advanced Kotlin, Coroutines, dan Flow
 */
@Composable
fun App() {
    // Tema dasar Material 3 dengan palet warna standar
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF0288D1),
            background = Color(0xFFF5F5F5),
            surface = Color.White
        )
    ) {
        val manager = remember { NewsFeedManager() }
        val scope = rememberCoroutineScope()

        // Mengambil data dari StateFlow pada NewsFeedManager
        val readCount by manager.readCount.collectAsState()
        val readNewsIds by manager.readNewsIds.collectAsState()
        val currentCategory by manager.selectedCategory.collectAsState()
        val logs by manager.processingLogs.collectAsState()
        val lastError by manager.lastError.collectAsState()
        val activeDetail by manager.currentDetail.collectAsState()
        val isLoadingDetail by manager.isLoadingDetail.collectAsState()

        // State lokal untuk aliran berita dan tampilan UI
        var feedItems by remember { mutableStateOf<List<DisplayNewsItem>>(emptyList()) }
        var isStreaming by remember { mutableStateOf(true) }
        var streamJob by remember { mutableStateOf<Job?>(null) }
        var showLogs by remember { mutableStateOf(false) }

        // Fungsi untuk memulai pengumpulan (collect) aliran Flow
        fun startStream(category: String, simulateError: Boolean = false) {
            streamJob?.cancel()
            streamJob = scope.launch {
                isStreaming = true
                feedItems = emptyList()

                // Memulai observasi Flow dengan rantai operator: filter -> onEach -> map -> catch
                manager.createNewsFeedFlow(
                    categoryFilter = category,
                    intervalMs = 2000L,
                    simulateErrorAtCount = if (simulateError) 4 else -1
                ).collect { newItem ->
                    // Memperbarui list berita di layar (item baru ditambahkan di paling atas)
                    feedItems = listOf(newItem) + feedItems.filter { it.id != newItem.id }
                }
            }
        }

        // Jalankan stream berita saat pertama kali dibuka atau kategori berubah
        LaunchedEffect(currentCategory) {
            startStream(currentCategory, simulateError = false)
        }

        Scaffold(
            topBar = {
                // Header aplikasi sederhana
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Aplikasi News Feed Simulator",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tugas 2 Praktikum PAM - Teknik Informatika ITERA",
                            fontSize = 12.sp,
                            color = Color(0xFFE3F2FD)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                // Bagian 1: Status StateFlow (Jumlah berita yang sudah dibaca)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Status Pembacaan (StateFlow)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Total Berita Dibaca: $readCount artikel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = { manager.resetReadCount() },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Reset", fontSize = 12.sp)
                        }
                    }
                }

                // Bagian 2: Filter Kategori (Operator .filter)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Filter Kategori (Operator .filter):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val categories = listOf("ALL", "Technology", "Science", "Sports", "Business")
                            categories.forEach { category ->
                                val isSelected = currentCategory.equals(category, ignoreCase = true)
                                val label = if (category == "ALL") "Semua" else category

                                if (isSelected) {
                                    Button(
                                        onClick = { manager.setCategory(category) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(label, fontSize = 12.sp)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { manager.setCategory(category) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(label, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Bagian 3: Tombol Kontrol Aliran (Flow) & Pengujian Error (.catch)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isStreaming) {
                                    streamJob?.cancel()
                                    isStreaming = false
                                } else {
                                    startStream(currentCategory, simulateError = false)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text(if (isStreaming) "Jeda Stream" else "Mulai Stream (2s)", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                startStream(currentCategory, simulateError = true)
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Tes Error (.catch)", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showLogs = !showLogs },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(if (showLogs) "Tutup Log" else "Log", fontSize = 12.sp)
                        }
                    }
                }

                // Bagian 4: Kotak Peringatan jika Terjadi Error pada Stream
                if (lastError != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pesan Error: $lastError",
                                fontSize = 12.sp,
                                color = Color(0xFFC62828),
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { manager.clearError() }) {
                                Text("Tutup", fontSize = 12.sp, color = Color(0xFFC62828))
                            }
                        }
                    }
                }

                // Bagian 5: Log Pemrosesan Operator .onEach
                AnimatedVisibility(visible = showLogs) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Catatan Log Pemrosesan (.onEach):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyColumn {
                                items(logs) { log ->
                                    Text(
                                        text = "- $log",
                                        fontSize = 11.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                // Bagian 6: Daftar Aliran Berita (LazyColumn & Transformasi .map)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (feedItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isStreaming) "Menunggu data berita baru (setiap 2 detik)..." else "Aliran berita sedang dijeda.",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    items(feedItems, key = { it.id }) { item ->
                        val isRead = readNewsIds.contains(item.id)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isRead) Color(0xFFF9F9F9) else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "[${item.categoryBadge}]",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1976D2)
                                    )
                                    Text(
                                        text = if (isRead) "Sudah dibaca" else "Belum dibaca",
                                        fontSize = 11.sp,
                                        color = if (isRead) Color(0xFF2E7D32) else Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Judul hasil transformasi operator .map
                                Text(
                                    text = item.formattedTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.summaryPreview,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.sourceText} (${item.formattedDate})",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )

                                    // Mengambil detail berita menggunakan Coroutines async/await
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                manager.getNewsDetailAsync(scope, item.id)
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("Baca Detail", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog Menampilkan Detail Berita (Hasil Async/Await)
        if (activeDetail != null) {
            val detail = activeDetail!!
            AlertDialog(
                onDismissRequest = { manager.dismissDetailDialog() },
                title = {
                    Text(
                        text = detail.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Penulis: ${detail.author} | Estimasi: ${detail.readTimeMinutes} menit",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Dimuat via Coroutine async dalam ${detail.fetchDurationMs} ms",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = detail.content,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { manager.dismissDetailDialog() }) {
                        Text("Tutup")
                    }
                }
            )
        }

        // Dialog Loading saat Mengambil Detail Berita
        if (isLoadingDetail) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text("Sedang mengambil detail berita...", fontSize = 13.sp)
                    }
                }
            )
        }
    }
}