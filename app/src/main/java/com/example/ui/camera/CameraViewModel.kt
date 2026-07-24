package com.example.ui.camera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.MotionPhoto
import com.example.data.repository.MotionPhotoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FlashMode {
    OFF, ON, TORCH;

    fun next(): FlashMode = when (this) {
        OFF -> ON
        ON -> TORCH
        TORCH -> OFF
    }
}

enum class CameraMode {
    VIDEO, MOTION, PHOTO, PRO
}

enum class ZoomLevel(val label: String, val factor: Float) {
    ZOOM_6X(".6", 0.6f),
    ZOOM_1X("1x", 1.0f),
    ZOOM_2X("2x", 2.0f),
    ZOOM_5X("5x", 5.0f)
}

enum class CameraFacing {
    BACK, FRONT
}

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MotionPhotoRepository

    init {
        val dao = AppDatabase.getDatabase(application).motionPhotoDao()
        repository = MotionPhotoRepository(dao)

        // Seed initial sample motion photo if empty
        viewModelScope.launch {
            repository.allPhotos.collect { photos ->
                if (photos.isEmpty()) {
                    seedSampleData()
                }
            }
        }
    }

    val allPhotos: StateFlow<List<MotionPhoto>> = repository.allPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestPhoto: StateFlow<MotionPhoto?> = repository.latestPhoto
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _flashMode = MutableStateFlow(FlashMode.ON)
    val flashMode: StateFlow<FlashMode> = _flashMode.asStateFlow()

    private val _isLivePhotoEnabled = MutableStateFlow(true)
    val isLivePhotoEnabled: StateFlow<Boolean> = _isLivePhotoEnabled.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _cameraMode = MutableStateFlow(CameraMode.MOTION)
    val cameraMode: StateFlow<CameraMode> = _cameraMode.asStateFlow()

    private val _zoomLevel = MutableStateFlow(ZoomLevel.ZOOM_1X)
    val zoomLevel: StateFlow<ZoomLevel> = _zoomLevel.asStateFlow()

    private val _cameraFacing = MutableStateFlow(CameraFacing.BACK)
    val cameraFacing: StateFlow<CameraFacing> = _cameraFacing.asStateFlow()

    private val _isGridVisible = MutableStateFlow(true)
    val isGridVisible: StateFlow<Boolean> = _isGridVisible.asStateFlow()

    private val _isCapturing = MutableStateFlow(false)
    val isCapturing: StateFlow<Boolean> = _isCapturing.asStateFlow()

    private val _countdownValue = MutableStateFlow(0)
    val countdownValue: StateFlow<Int> = _countdownValue.asStateFlow()

    private val _selectedPhotoForViewer = MutableStateFlow<MotionPhoto?>(null)
    val selectedPhotoForViewer: StateFlow<MotionPhoto?> = _selectedPhotoForViewer.asStateFlow()

    private val _showAppRecommendationsDialog = MutableStateFlow(false)
    val showAppRecommendationsDialog: StateFlow<Boolean> = _showAppRecommendationsDialog.asStateFlow()

    private val _isPlayingMotion = MutableStateFlow(false)
    val isPlayingMotion: StateFlow<Boolean> = _isPlayingMotion.asStateFlow()

    private val _activeFrameIndex = MutableStateFlow(7)
    val activeFrameIndex: StateFlow<Int> = _activeFrameIndex.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var motionPlaybackJob: Job? = null

    fun toggleFlash() {
        _flashMode.value = _flashMode.value.next()
    }

    fun toggleLivePhoto() {
        _isLivePhotoEnabled.value = !_isLivePhotoEnabled.value
    }

    fun cycleTimer() {
        _timerSeconds.value = when (_timerSeconds.value) {
            0 -> 3
            3 -> 5
            5 -> 10
            else -> 0
        }
    }

    fun setCameraMode(mode: CameraMode) {
        _cameraMode.value = mode
        if (mode == CameraMode.MOTION) {
            _isLivePhotoEnabled.value = true
        }
    }

    fun setZoomLevel(zoom: ZoomLevel) {
        _zoomLevel.value = zoom
    }

    fun toggleCameraFacing() {
        _cameraFacing.value = if (_cameraFacing.value == CameraFacing.BACK) CameraFacing.FRONT else CameraFacing.BACK
    }

    fun toggleGrid() {
        _isGridVisible.value = !_isGridVisible.value
    }

    fun openAppRecommendations() {
        _showAppRecommendationsDialog.value = true
    }

    fun closeAppRecommendations() {
        _showAppRecommendationsDialog.value = false
    }

    fun openViewer(photo: MotionPhoto) {
        _selectedPhotoForViewer.value = photo
        _activeFrameIndex.value = photo.bestFrameIndex
        _isPlayingMotion.value = false
    }

    fun closeViewer() {
        _selectedPhotoForViewer.value = null
        stopMotionPlayback()
    }

    fun startMotionPlayback() {
        stopMotionPlayback()
        _isPlayingMotion.value = true
        val totalFrames = _selectedPhotoForViewer.value?.frameCount ?: 15
        motionPlaybackJob = viewModelScope.launch {
            while (_isPlayingMotion.value) {
                for (i in 0 until totalFrames) {
                    _activeFrameIndex.value = i
                    delay(100) // 10 fps playback simulation
                }
            }
        }
    }

    fun stopMotionPlayback() {
        motionPlaybackJob?.cancel()
        motionPlaybackJob = null
        _isPlayingMotion.value = false
    }

    fun setFrameIndex(index: Int) {
        stopMotionPlayback()
        _activeFrameIndex.value = index
    }

    fun capturePhoto() {
        if (_isCapturing.value) return

        viewModelScope.launch {
            _isCapturing.value = true

            // Countdown handling if timer set
            val timerSec = _timerSeconds.value
            if (timerSec > 0) {
                for (t in timerSec downTo 1) {
                    _countdownValue.value = t
                    delay(1000)
                }
                _countdownValue.value = 0
            }

            // Simulate pre-roll shutter flash & capture
            delay(200)

            val mode = _cameraMode.value
            val isLive = _isLivePhotoEnabled.value && (mode == CameraMode.MOTION || mode == CameraMode.PHOTO)
            val title = when (mode) {
                CameraMode.MOTION -> "Live Motion Photo"
                CameraMode.PHOTO -> if (isLive) "Live Photo" else "Static Photo"
                CameraMode.VIDEO -> "Video Recording"
                CameraMode.PRO -> "RAW Motion Frame"
            }

            // Sample generated URIs for gallery preview
            val sampleImageUri = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop"
            
            val newPhoto = MotionPhoto(
                title = "$title #${(100..999).random()}",
                timestamp = System.currentTimeMillis(),
                isLivePhoto = isLive,
                durationSeconds = if (isLive) 3.0f else 0.0f,
                flashMode = _flashMode.value.name,
                cameraFacing = _cameraFacing.value.name,
                zoomLevel = _zoomLevel.value.label,
                imageUri = sampleImageUri,
                frameCount = if (isLive) 15 else 1,
                bestFrameIndex = 7
            )

            repository.savePhoto(newPhoto)
            _isCapturing.value = false
            
            showSnackbar("Tersimpan di Galeri sebagai ${if (isLive) "Live Photo (.XMP)" else "Foto Statis"}")
        }
    }

    fun exportPhotoFormat(formatName: String) {
        val current = _selectedPhotoForViewer.value ?: return
        showSnackbar("Berhasil diekspor sebagai $formatName!")
    }

    fun deleteCurrentPhoto() {
        val current = _selectedPhotoForViewer.value ?: return
        viewModelScope.launch {
            repository.deletePhoto(current.id)
            closeViewer()
            showSnackbar("Foto berhasil dihapus")
        }
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    private suspend fun seedSampleData() {
        val sample1 = MotionPhoto(
            title = "Sunset Wave Motion",
            timestamp = System.currentTimeMillis() - 3600000,
            isLivePhoto = true,
            durationSeconds = 3.0f,
            flashMode = "OFF",
            cameraFacing = "BACK",
            zoomLevel = "1x",
            imageUri = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop",
            frameCount = 15,
            bestFrameIndex = 7
        )
        val sample2 = MotionPhoto(
            title = "City Lights Live",
            timestamp = System.currentTimeMillis() - 86400000,
            isLivePhoto = true,
            durationSeconds = 3.0f,
            flashMode = "ON",
            cameraFacing = "BACK",
            zoomLevel = "2x",
            imageUri = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=800&auto=format&fit=crop",
            frameCount = 15,
            bestFrameIndex = 6
        )
        repository.savePhoto(sample1)
        repository.savePhoto(sample2)
    }
}
