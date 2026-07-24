package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MotionPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface MotionPhotoDao {
    @Query("SELECT * FROM motion_photos ORDER BY timestamp DESC")
    fun getAllMotionPhotos(): Flow<List<MotionPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotionPhoto(photo: MotionPhoto): Long

    @Query("DELETE FROM motion_photos WHERE id = :id")
    suspend fun deleteMotionPhotoById(id: Long)

    @Query("SELECT * FROM motion_photos ORDER BY timestamp DESC LIMIT 1")
    fun getLatestPhoto(): Flow<MotionPhoto?>
}
