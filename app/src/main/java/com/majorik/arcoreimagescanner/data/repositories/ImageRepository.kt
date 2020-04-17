package com.majorik.arcoreimagescanner.data.repositories

import com.majorik.arcoreimagescanner.data.dao.ImageDao
import com.majorik.arcoreimagescanner.data.model.Image

class ImageRepository(private val imageDao: ImageDao) {

    suspend fun getImages(): List<Image> {
        return imageDao.getImages()
    }

    suspend fun addImage(title: String, path: String, date: String) {
        imageDao.addImage(Image(title = title, imagePath = path, date = date, _id = null))
    }

    suspend fun deleteById(id: Int) {
        imageDao.deleteById(id)
    }
}