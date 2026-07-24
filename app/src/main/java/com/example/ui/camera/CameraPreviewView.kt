package com.example.ui.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.FlashYellow
import com.example.ui.theme.LiveRecordingRed
import com.example.ui.theme.ViewfinderBackground
import kotlin.math.roundToInt

@Composable
fun CameraPreviewView(
    hasCameraPermission: Boolean,
    cameraFacing: CameraFacing,
    flashMode: FlashMode,
    isGridVisible: Boolean,
    isLiveRecording: Boolean,
    zoomLevel: ZoomLevel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isCameraBound by remember { mutableStateOf(false) }

    var tapFocusPoint by remember { mutableStateOf<Offset?>(null) }
    var showFocusRing by remember { mutableStateOf(false) }

    LaunchedEffect(tapFocusPoint) {
        if (tapFocusPoint != null) {
            showFocusRing = true
            kotlinx.coroutines.delay(1800)
            showFocusRing = false
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(ViewfinderBackground)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    tapFocusPoint = offset
                }
            }
    ) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val selector = if (cameraFacing == CameraFacing.BACK) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }

                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview
                            )
                            // Apply torch if flash mode is TORCH
                            camera.cameraControl.enableTorch(flashMode == FlashMode.TORCH)
                            isCameraBound = true
                        } catch (e: Exception) {
                            isCameraBound = false
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )
        }

        // Fallback or overlay view if camera hardware is unbindable or simulated in emulator
        if (!hasCameraPermission || !isCameraBound) {
            SimulatedViewfinderCanvas(
                zoomFactor = zoomLevel.factor,
                isLiveMode = isLiveRecording
            )
        }

        // Camera Grid Overlay
        if (isGridVisible) {
            CameraGridOverlay(modifier = Modifier.fillMaxSize())
        }

        // Focus Ring animation when user taps
        tapFocusPoint?.let { point ->
            if (showFocusRing) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(point.x.roundToInt() - 40, point.y.roundToInt() - 40) }
                        .size(80.dp)
                        .border(1.5.dp, FlashYellow, RoundedCornerShape(12.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(FlashYellow, CircleShape)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        // Default Focus Box in center if no tap focus active
        if (tapFocusPoint == null) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(FlashYellow, CircleShape)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun SimulatedViewfinderCanvas(
    zoomFactor: Float,
    isLiveMode: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "viewfinder")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Simulated background environment
        drawRect(
            color = Color(0xFF14171A),
            size = Size(width, height)
        )

        // Draw motion aura circles representing live pre-roll buffer
        if (isLiveMode) {
            drawCircle(
                color = Color(0xFFD1E4FF).copy(alpha = 0.08f),
                radius = (width * 0.35f * pulseScale * zoomFactor).coerceAtMost(width * 0.8f),
                center = Offset(width / 2f, height / 2f)
            )
            drawCircle(
                color = Color(0xFF004883).copy(alpha = 0.12f),
                radius = (width * 0.22f * zoomFactor).coerceAtMost(width * 0.6f),
                center = Offset(width / 2f, height / 2f)
            )
        }

        // Subject silhouette
        drawCircle(
            color = Color(0xFF2C3138),
            radius = 110f * zoomFactor,
            center = Offset(width / 2f, height / 2f - 20f)
        )
        drawCircle(
            color = Color(0xFF3B414A),
            radius = 45f * zoomFactor,
            center = Offset(width / 2f, height / 2f - 70f)
        )
    }
}

@Composable
fun CameraGridOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val gridColor = Color.White.copy(alpha = 0.18f)
        val strokeWidth = 1.dp.toPx()

        // 3x3 Grid lines
        drawLine(
            color = gridColor,
            start = Offset(width / 3f, 0f),
            end = Offset(width / 3f, height),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = gridColor,
            start = Offset(2 * width / 3f, 0f),
            end = Offset(2 * width / 3f, height),
            strokeWidth = strokeWidth
        )

        drawLine(
            color = gridColor,
            start = Offset(0f, height / 3f),
            end = Offset(width, height / 3f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = gridColor,
            start = Offset(0f, 2 * height / 3f),
            end = Offset(width, 2 * height / 3f),
            strokeWidth = strokeWidth
        )
    }
}
