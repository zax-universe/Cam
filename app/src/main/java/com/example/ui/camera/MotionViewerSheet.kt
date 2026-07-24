package com.example.ui.camera

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MotionPhoto
import com.example.ui.theme.FlashYellow
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceContainerDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotionViewerSheet(
    photo: MotionPhoto,
    isPlayingMotion: Boolean,
    activeFrameIndex: Int,
    onStartPlayback: () -> Unit,
    onStopPlayback: () -> Unit,
    onSetFrameIndex: (Int) -> Unit,
    onExportFormat: (String) -> Unit,
    onDeletePhoto: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showExportOptions by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        contentColor = TextLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = if (photo.isLivePhoto) "Live Motion Photo • 3.0 detik" else "Foto Statis",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted
                    )
                }
            }

            // Image Viewport with Press-to-Play motion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (photo.isLivePhoto) {
                                    onStartPlayback()
                                    tryAwaitRelease()
                                    onStopPlayback()
                                }
                            }
                        )
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = photo.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Motion overlay badge
                if (photo.isLivePhoto) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MotionPhotosOn,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlayingMotion) "MEMUTAR GERAKAN" else "TAHAN UNTUK BERGERAK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Play / Pause Floating Toggle
                    IconButton(
                        onClick = {
                            if (isPlayingMotion) onStopPlayback() else onStartPlayback()
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, PrimaryBlue, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlayingMotion) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Putar Gerakan",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Frame scrubber for Motion Photos
            if (photo.isLivePhoto) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pre-roll & Post-roll Moments",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                            Text(
                                text = "Frame ${activeFrameIndex + 1} / ${photo.frameCount}",
                                fontSize = 12.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom Frame Index Slider
                        Slider(
                            value = activeFrameIndex.toFloat(),
                            onValueChange = { onSetFrameIndex(it.toInt()) },
                            valueRange = 0f..(photo.frameCount - 1).toFloat(),
                            steps = photo.frameCount - 2,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryBlue,
                                activeTrackColor = PrimaryBlue,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Pilih frame terbaik (Keyframe) untuk disimpan sebagai foto utama",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { showExportOptions = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Ekspor",
                        tint = SurfaceDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ekspor / Simpan", color = SurfaceDark, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDeletePhoto,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2020)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color(0xFFFFB4AB),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus", color = Color(0xFFFFB4AB), fontWeight = FontWeight.Bold)
                }
            }

            // Export Options Modal / Expansion
            if (showExportOptions) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PILIH FORMAT EKSPOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        ExportFormatItem(
                            title = "Standard Live Photo (.XMP Embedded)",
                            subtitle = "Bergerak langsung di Galeri HP Android (Google Photos/Samsung)",
                            onClick = {
                                onExportFormat("Standard Live Photo (.XMP)")
                                showExportOptions = false
                            }
                        )

                        ExportFormatItem(
                            title = "Video Pendek (.MP4)",
                            subtitle = "Durasi 3 detik, kompatibel untuk WhatsApp & Social Media",
                            onClick = {
                                onExportFormat("Video Pendek MP4")
                                showExportOptions = false
                            }
                        )

                        ExportFormatItem(
                            title = "Gambar Animasi (.GIF)",
                            subtitle = "GIF berulang ringan tanpa suara",
                            onClick = {
                                onExportFormat("Animated GIF")
                                showExportOptions = false
                            }
                        )

                        ExportFormatItem(
                            title = "Foto Statis Best Shot (.JPG)",
                            subtitle = "Simpan frame yang dipilih saat ini sebagai foto resolusi tinggi",
                            onClick = {
                                onExportFormat("Foto Statis Best Frame (.JPG)")
                                showExportOptions = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExportFormatItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
