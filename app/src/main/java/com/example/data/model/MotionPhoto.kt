package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motion_photos")
data class MotionPhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLivePhoto: Boolean = true,
    val durationSeconds: Float = 3.0f,
    val flashMode: String = "OFF",
    val cameraFacing: String = "BACK",
    val zoomLevel: String = "1x",
    val imageUri: String,
    val videoUri: String? = null,
    val frameCount: Int = 15,
    val bestFrameIndex: Int = 7
)
