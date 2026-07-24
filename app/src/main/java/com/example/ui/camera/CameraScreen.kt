package com.example.ui.camera

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.MotionPhotosPaused
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timer10
import androidx.compose.material.icons.filled.Timer3
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.FlashYellow
import com.example.ui.theme.LiveRecordingRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceContainerDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val flashMode by viewModel.flashMode.collectAsState()
    val isLivePhotoEnabled by viewModel.isLivePhotoEnabled.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val cameraMode by viewModel.cameraMode.collectAsState()
    val zoomLevel by viewModel.zoomLevel.collectAsState()
    val cameraFacing by viewModel.cameraFacing.collectAsState()
    val isGridVisible by viewModel.isGridVisible.collectAsState()
    val isCapturing by viewModel.isCapturing.collectAsState()
    val countdownValue by viewModel.countdownValue.collectAsState()

    val latestPhoto by viewModel.latestPhoto.collectAsState()
    val selectedViewerPhoto by viewModel.selectedPhotoForViewer.collectAsState()
    val showAppRecommendations by viewModel.showAppRecommendationsDialog.collectAsState()
    val isPlayingMotion by viewModel.isPlayingMotion.collectAsState()
    val activeFrameIndex by viewModel.activeFrameIndex.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        containerColor = SurfaceDark,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceDark)
        ) {
            // 1. Top Control Bar
            TopControlBar(
                flashMode = flashMode,
                isLivePhotoEnabled = isLivePhotoEnabled,
                timerSeconds = timerSeconds,
                isGridVisible = isGridVisible,
                onToggleFlash = { viewModel.toggleFlash() },
                onToggleLive = { viewModel.toggleLivePhoto() },
                onCycleTimer = { viewModel.cycleTimer() },
                onToggleGrid = { viewModel.toggleGrid() },
                onOpenRecommendations = { viewModel.openAppRecommendations() }
            )

            // 2. Viewfinder Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                CameraPreviewView(
                    hasCameraPermission = cameraPermissionState.status.isGranted,
                    cameraFacing = cameraFacing,
                    flashMode = flashMode,
                    isGridVisible = isGridVisible,
                    isLiveRecording = isLivePhotoEnabled && (cameraMode == CameraMode.MOTION || cameraMode == CameraMode.PHOTO),
                    zoomLevel = zoomLevel,
                    modifier = Modifier.fillMaxSize()
                )

                // Top-Left Live Recording Status Badge
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isLivePhotoEnabled) LiveRecordingRed else TextMuted,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLivePhotoEnabled) "LIVE RECORDING" else "READY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Countdown Overlay if timer is running
                if (countdownValue > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = countdownValue.toString(),
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black,
                            color = FlashYellow
                        )
                    }
                }

                // Floating Zoom Level Pill Controls at bottom center
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ZoomLevel.entries.forEach { zoom ->
                        val isSelected = zoomLevel == zoom
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { viewModel.setZoomLevel(zoom) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = zoom.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            // 3. Bottom Controls Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mode Selector Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CameraMode.entries.forEach { mode ->
                        val isSelected = cameraMode == mode
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.setCameraMode(mode)
                            }
                        ) {
                            Text(
                                text = mode.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryBlue else TextMuted,
                                letterSpacing = 1.2.sp
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(PrimaryBlue, CircleShape)
                                )
                            }
                        }
                    }
                }

                // Capture Action Row (Gallery Thumbnail, Big Shutter, Switch Camera)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery Preview Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                            .background(SurfaceContainerDark)
                            .clickable {
                                latestPhoto?.let { photo -> viewModel.openViewer(photo) }
                                    ?: viewModel.showSnackbar("Belum ada foto tersimpan")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (latestPhoto != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(latestPhoto?.imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Pratinjau Galeri",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (latestPhoto?.isLivePhoto == true) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(PrimaryBlue, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.MotionPhotosOn,
                                contentDescription = "Galeri Kosong",
                                tint = TextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Tactile Material Shutter Button
                    val scaleFactor by animateFloatAsState(if (isCapturing) 0.9f else 1.0f, label = "shutter")
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(scaleFactor)
                            .clip(CircleShape)
                            .border(4.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .padding(4.dp)
                            .clickable { viewModel.capturePhoto() },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color.Black.copy(alpha = 0.1f), CircleShape)
                        )
                    }

                    // Flip Camera Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { viewModel.toggleCameraFacing() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Ganti Kamera",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // 4. Motion Viewer Modal Sheet
        selectedViewerPhoto?.let { photo ->
            MotionViewerSheet(
                photo = photo,
                isPlayingMotion = isPlayingMotion,
                activeFrameIndex = activeFrameIndex,
                onStartPlayback = { viewModel.startMotionPlayback() },
                onStopPlayback = { viewModel.stopMotionPlayback() },
                onSetFrameIndex = { idx -> viewModel.setFrameIndex(idx) },
                onExportFormat = { fmt -> viewModel.exportPhotoFormat(fmt) },
                onDeletePhoto = { viewModel.deleteCurrentPhoto() },
                onDismiss = { viewModel.closeViewer() }
            )
        }

        // 5. Play Store Recommendations Modal Sheet
        if (showAppRecommendations) {
            AppRecommendationsSheet(
                onDismiss = { viewModel.closeAppRecommendations() }
            )
        }
    }
}

@Composable
fun TopControlBar(
    flashMode: FlashMode,
    isLivePhotoEnabled: Boolean,
    timerSeconds: Int,
    isGridVisible: Boolean,
    onToggleFlash: () -> Unit,
    onToggleLive: () -> Unit,
    onCycleTimer: () -> Unit,
    onToggleGrid: () -> Unit,
    onOpenRecommendations: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Flash Toggle Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onToggleFlash() }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = when (flashMode) {
                        FlashMode.OFF -> Icons.Default.FlashOff
                        FlashMode.ON -> Icons.Default.FlashOn
                        FlashMode.TORCH -> Icons.Default.FlashAuto
                    },
                    contentDescription = "Flash",
                    tint = if (flashMode != FlashMode.OFF) FlashYellow else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = flashMode.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (flashMode != FlashMode.OFF) FlashYellow else TextMuted
                )
            }

            // Live Mode Toggle Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onToggleLive() }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isLivePhotoEnabled) Icons.Default.MotionPhotosOn else Icons.Default.MotionPhotosPaused,
                    contentDescription = "Live Photo Mode",
                    tint = if (isLivePhotoEnabled) PrimaryBlue else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = if (isLivePhotoEnabled) "LIVE" else "OFF",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLivePhotoEnabled) PrimaryBlue else TextMuted
                )
            }

            // Grid Toggle Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onToggleGrid() }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "Grid",
                    tint = if (isGridVisible) Color.White else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = if (isGridVisible) "GRID" else "OFF",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGridVisible) Color.White else TextMuted
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Timer Button
            IconButton(onClick = onCycleTimer) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (timerSeconds) {
                            3 -> Icons.Default.Timer3
                            10 -> Icons.Default.Timer10
                            else -> Icons.Default.Timer
                        },
                        contentDescription = "Timer",
                        tint = if (timerSeconds > 0) FlashYellow else Color.White
                    )
                    if (timerSeconds > 0) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${timerSeconds}s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FlashYellow
                        )
                    }
                }
            }

            // App Recommendations Guide Button
            IconButton(onClick = onOpenRecommendations) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Rekomendasi Aplikasi Live Photo Play Store",
                    tint = PrimaryBlue
                )
            }
        }
    }
}
