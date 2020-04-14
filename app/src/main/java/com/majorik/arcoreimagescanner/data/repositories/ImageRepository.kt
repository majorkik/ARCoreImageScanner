package com.majorik.arcoreimagescanner.data.repositories

import androidx.lifecycle.LiveData
import com.majorik.arcoreimagescanner.data.dao.ImageDao
import com.majorik.arcoreimagescanner.data.model.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository(private val imageDao: ImageDao) {

    suspend fun getImages(): List<Image> {
        return imageDao.getImages()
    }
}