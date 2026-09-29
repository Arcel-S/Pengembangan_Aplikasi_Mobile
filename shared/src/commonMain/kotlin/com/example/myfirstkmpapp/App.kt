package com.example.myfirstkmpapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================================
// DESIGN THEME TOKENS (Clean, Simple, Professional Slate-Blue Palette)
// ============================================================================
private val SlateDark = Color(0xFF0F172A)
private val SlateMedium = Color(0xFF334155)
private val SlateMuted = Color(0xFF64748B)
private val PrimaryBlue = Color(0xFF2563EB)
private val PrimaryContainerBlue = Color(0xFFEFF6FF)
private val BackgroundSurface = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val ActiveStatusGreen = Color(0xFF10B981)

val AppColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = PrimaryBlue,
    surface = Color.White,
    background = BackgroundSurface,
    onBackground = SlateDark,
    onSurface = SlateDark
)

/**
 * Main Application Composable
 * Tugas Praktikum Pertemuan 3: "My Profile App"
 */
@Composable
@Preview
fun App(
    initialAcademicDetailsVisible: Boolean = false,
    initialContactDialogVisible: Boolean = false,
    initialScrollPosition: Int = 0
) {
    MaterialTheme(colorScheme = AppColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val scrollState = rememberScrollState(initialScrollPosition)
            ProfileScreen(
                scrollState = scrollState,
                initialAcademicDetailsVisible = initialAcademicDetailsVisible,
                initialContactDialogVisible = initialContactDialogVisible
            )
        }
    }
}

/**
 * Screen Utama: Menampilkan seluruh layout profil
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    scrollState: androidx.compose.foundation.ScrollState = rememberScrollState(),
    initialAcademicDetailsVisible: Boolean = false,
    initialContactDialogVisible: Boolean = false
) {
    // State interaktif untuk fitur bonus (AnimatedVisibility)
    var isAcademicDetailsVisible by remember { mutableStateOf(initialAcademicDetailsVisible) }

    // State dialog popup ketika tombol Hubungi diklik
    var showContactDialog by remember { mutableStateOf(initialContactDialogVisible) }

    val userEmail = "marcel.12310054@student.itera.ac.id"

    Box(modifier = modifier.fillMaxSize()) {
        // Layer 1: Konten Utama Profil (Scrollable)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Title / Top Bar Mini
            Text(
                text = "My Profile App",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // 1. REUSABLE COMPOSABLE: ProfileHeader (Avatar icon kontak standar, nama, role)
            ProfileHeader(
                name = "Marcel",
                role = "Mahasiswa Teknik Informatika",
                avatarPainter = rememberVectorPainter(Icons.Default.AccountCircle)
            )

            // 2. REUSABLE COMPOSABLE: ProfileCard ("Tentang Saya")
            ProfileCard(title = "Tentang Saya") {
                Text(
                    text = "Mahasiswa Informatika ITERA yang antusias mempelajari mobile application development dengan Kotlin Multiplatform dan Jetpack Compose.",
                    fontSize = 13.sp,
                    color = SlateMuted,
                    lineHeight = 19.sp
                )
            }

            // 3. REUSABLE COMPOSABLE: ProfileCard (Daftar Informasi Kontak Utama)
            ProfileCard(title = "Informasi Kontak") {
                InfoItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = userEmail
                )
                HorizontalDivider(
                    color = CardBorderColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                InfoItem(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    value = "+62 812-3456-7890"
                )
                HorizontalDivider(
                    color = CardBorderColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                InfoItem(
                    icon = Icons.Default.LocationOn,
                    label = "Location",
                    value = "Lampung Selatan, Indonesia"
                )
            }

            // 4. FITUR BONUS (+10%): AnimatedVisibility untuk Detail Akademik & Keahlian
            ProfileCard(
                title = "Detail Akademik & Minat",
                modifier = Modifier.clickable { isAcademicDetailsVisible = !isAcademicDetailsVisible }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isAcademicDetailsVisible) "Sembunyikan detail" else "Ketuk untuk melihat detail",
                        fontSize = 13.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = if (isAcademicDetailsVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Detail",
                        tint = PrimaryBlue
                    )
                }

                AnimatedVisibility(
                    visible = isAcademicDetailsVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

                        InfoItem(
                            icon = Icons.Default.School,
                            label = "NIM",
                            value = "12310054"
                        )
                        InfoItem(
                            icon = Icons.Default.School,
                            label = "Program Studi",
                            value = "S1 Teknik Informatika"
                        )
                        InfoItem(
                            icon = Icons.Default.LocationOn,
                            label = "Institut",
                            value = "Institut Teknologi Sumatera (ITERA)"
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fokus & Minat Belajar:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateMedium
                        )

                        // Skill tags sederhana menggunakan Row & Box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SkillChip("Compose")
                            SkillChip("KMP")
                            SkillChip("Kotlin")
                            SkillChip("Android")
                        }
                    }
                }
            }

            // 5. ACTION BUTTON: Tombol "Hubungi" (Full Width, single CTA)
            Button(
                onClick = { showContactDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hubungi",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Layer 2 (Modal Stack): Pop-up Dialog yang muncul sekali di tengah saat tombol Hubungi diklik
        if (showContactDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { showContactDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainerBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Berhasil Menghubungi",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Telah berhasil menghubungi email:\n$userEmail",
                            fontSize = 13.sp,
                            color = SlateMedium,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { showContactDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("OK", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 1. REUSABLE COMPOSABLE: ProfileHeader
// ============================================================================
/**
 * Komponen reusable untuk menampilkan header profil: foto profil (circular),
 * status badge aktif, nama lengkap, role, dan bio singkat.
 */
@Composable
fun ProfileHeader(
    name: String,
    role: String,
    avatarPainter: Painter,
    modifier: Modifier = Modifier,
    bio: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Box Layout: Foto profil circular dengan status dot aktif di pojok bawah
        Box(
            modifier = Modifier.size(108.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            // Container melingkar untuk avatar kontak
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainerBlue)
                    .border(3.dp, Color.White, CircleShape)
                    .border(4.dp, PrimaryBlue.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = avatarPainter,
                    contentDescription = "Foto Profil $name",
                    modifier = Modifier.size(76.dp),
                    colorFilter = ColorFilter.tint(PrimaryBlue),
                    contentScale = ContentScale.Fit
                )
            }

            // Status active indicator dot
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(ActiveStatusGreen)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nama Profil
        Text(
            text = name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Role / Status Mahasiswa
        Text(
            text = role,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryBlue
        )

        // Deskripsi Bio (jika ada)
        if (!bio.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bio,
                fontSize = 13.sp,
                color = SlateMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

// ============================================================================
// 2. REUSABLE COMPOSABLE: InfoItem
// ============================================================================
/**
 * Komponen reusable untuk menampilkan baris informasi (Icon, Label, Value).
 * Digunakan untuk Email, Phone, Location, dan detail informasi lainnya.
 */
@Composable
fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Box dengan latar belakang lembut untuk Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryContainerBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Column teks (Label dan Value)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = SlateMuted,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = SlateDark,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ============================================================================
// 3. REUSABLE COMPOSABLE: ProfileCard
// ============================================================================
/**
 * Komponen reusable Card container dengan styling konsisten, elevasi, rounded
 * corner, dan border halus. Membungkus konten apapun di dalamnya.
 */
@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            content()
        }
    }
}

// ============================================================================
// HELPER COMPONENT: SkillChip
// ============================================================================
/**
 * Chip sederhana untuk menampilkan tag minat / keahlian
 */
@Composable
fun SkillChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryContainerBlue)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBlue
        )
    }
}
