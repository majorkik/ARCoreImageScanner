package com.majorik.arcoreimagescanner.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.majorik.arcoreimagescanner.data.model.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(image: Image)

    @Query("SELECT * from image_table")
    suspend fun getImages(): List<Image>

    @Query("DELETE FROM image_table WHERE image_path = :path")
    suspend fun deleteById(path: String)

    @Query("DELETE FROM image_table")
    suspend fun deleteAll()
}