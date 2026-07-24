package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.camera.CameraScreen
import com.example.ui.camera.CameraViewModel
import com.example.ui.theme.MotionCamTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MotionCamTheme {
        val cameraViewModel: CameraViewModel = viewModel()
        CameraScreen(viewModel = cameraViewModel)
      }
    }
  }
}

