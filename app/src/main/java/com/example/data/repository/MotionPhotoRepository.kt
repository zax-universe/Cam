package com.example.data.repository

import com.example.data.db.MotionPhotoDao
import com.example.data.model.MotionPhoto
import kotlinx.coroutines.flow.Flow

class MotionPhotoRepository(private val dao: MotionPhotoDao) {
    val allPhotos: Flow<List<MotionPhoto>> = dao.getAllMotionPhotos()
    val latestPhoto: Flow<MotionPhoto?> = dao.getLatestPhoto()

    suspend fun savePhoto(photo: MotionPhoto): Long {
        return dao.insertMotionPhoto(photo)
    }

    suspend fun deletePhoto(id: Long) {
        dao.deleteMotionPhotoById(id)
    }
}
