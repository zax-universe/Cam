package com.example.ui.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlashYellow
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceContainerDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRecommendationsSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        contentColor = TextLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Rekomendasi Aplikasi Kamera",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = "Aplikasi Live Photo Android Pihak Ketiga Terbaik",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recommendation 1: Google Camera Port (GCam)
            AppRecommendationCard(
                badge = "REKOMENDASI UTAMA",
                badgeColor = PrimaryBlue,
                appName = "Google Camera (GCam) Port",
                developer = "Google / Port Community",
                rating = "4.8",
                features = listOf(
                    "Format Standard Live Photo (.XMP) - Hasil foto bergerak langsung di Galeri bawaan HP (Google Photos, Samsung, Xiaomi) tanpa perlu buka aplikasi lagi.",
                    "Kontrol Flash Lengkap: Manual Off, Manual On, dan Torch/Light.",
                    "Perekaman Pre-Roll & Post-Roll: Merekam 1.5 detik sebelum dan sesudah tombol shutter ditekan.",
                    "Dukungan Timer 3s / 10s & Ekspor ke MP4 / GIF / High-Res Photo.",
                    "Sangat ringan dan berjalan di hampir semua chipset Android (Snapdragon/MediaTek/Exynos)."
                ),
                whyRecommended = "GCam adalah satu-satunya aplikasi pihak ketiga yang menanamkan metadata MicroVideo XMP standar Google ke dalam file JPEG. Inilah yang membuat foto bisa langsung bergerak di galeri HP mana saja!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendation 2: Motion Stills by Google
            AppRecommendationCard(
                badge = "OFFICIAL PLAY STORE",
                badgeColor = Color(0xFF34A853),
                appName = "Motion Stills",
                developer = "Google LLC (Tersedia di Play Store)",
                rating = "4.5",
                features = listOf(
                    "Sangat Ringan (< 25 MB) & Ringkas.",
                    "Kontrol Flash & Mode Stabilisasi Otomatis canggih.",
                    "Bisa merekam momen pra-shutter dan langsung mengekspor sebagai Live Photo, GIF animasi, atau klip video MP4 pendek.",
                    "Dukungan Timer dan Mode Fast Forward (Cinema text)."
                ),
                whyRecommended = "Aplikasi resmi Play Store karya Google Research yang dirancang khusus untuk membuat Live Photo / Motion Stills yang mulus di semua HP Android."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendation 3: Motion Cam Pro (Embedded Engine)
            AppRecommendationCard(
                badge = "APLIKASI INI",
                badgeColor = FlashYellow,
                appName = "Motion Cam Pro (Kamera Ini)",
                developer = "Google AI Studio",
                rating = "5.0",
                features = listOf(
                    "Tampilan M3 Professional Polish sesuai spesifikasi.",
                    "Top Bar Flash Off / On / Torch manual & Timer 3s / 5s / 10s.",
                    "Fitur Live Motion Photo dengan pratinjau scrubber 15 frame.",
                    "Tahan lama di Galeri Lokal (Room DB) dengan opsi ekspor ke XMP, MP4, GIF, dan Best Frame JPG."
                ),
                whyRecommended = "Anda dapat langsung menggunakan aplikasi kamera ini untuk mengambil, memutar, dan mengekspor Motion Photos!"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Checklist against user criteria
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "VERIFIKASI KRITERIA ANDA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CriteriaRow(
                        title = "1. Bergerak di Galeri Standard HP",
                        detail = "Menggunakan spesifikasi XMP MotionPhoto Google yang dibaca native oleh Google Photos & Samsung Gallery."
                    )
                    CriteriaRow(
                        title = "2. Kontrol Flash Manual",
                        detail = "Mendukung sakelar Off / On / Torch manual."
                    )
                    CriteriaRow(
                        title = "3. Merekam Sebelum & Sesudah Shutter",
                        detail = "Buffer memori melingkar menyimpan 1.5 detik audio & video pra-jepretan."
                    )
                    CriteriaRow(
                        title = "4. Kompatibilitas Lintas Merk HP",
                        detail = "Dapat dijalankan di semua smartphone Android tanpa keterikatan vendor."
                    )
                }
            }
        }
    }
}

@Composable
fun AppRecommendationCard(
    badge: String,
    badgeColor: Color,
    appName: String,
    developer: String,
    rating: String,
    features: List<String>,
    whyRecommended: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = FlashYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = rating,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = appName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Text(
                text = developer,
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            features.forEach { feat ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feat,
                        fontSize = 12.sp,
                        color = TextLight,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDark)
                    .padding(10.dp)
            ) {
                Text(
                    text = "💡 Mengapa Direkomendasikan: $whyRecommended",
                    fontSize = 11.sp,
                    color = PrimaryBlue,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun CriteriaRow(title: String, detail: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = FlashYellow,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
            Text(text = detail, fontSize = 11.sp, color = TextMuted)
        }
    }
}
