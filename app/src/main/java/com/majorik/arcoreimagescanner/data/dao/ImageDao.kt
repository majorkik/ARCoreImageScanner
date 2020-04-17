package com.majorik.arcoreimagescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.majorik.arcoreimagescanner.data.model.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(image: Image)

    @Query("SELECT * from image_table ORDER BY id ASC")
    suspend fun getImages(): List<Image>

    @Query("DELETE FROM image_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM image_table")
    suspend fun deleteAll()
}