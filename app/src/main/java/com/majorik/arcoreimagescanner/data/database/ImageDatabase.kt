package com.majorik.arcoreimagescanner.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.majorik.arcoreimagescanner.data.dao.ImageDao
import com.majorik.arcoreimagescanner.data.model.Image

@Database(entities = [Image::class], version = 3, exportSchema = false)
abstract class ImageDatabase : RoomDatabase() {

    abstract fun messageDao(): ImageDao

    companion object {

        @Volatile
        private var INSTANCE: ImageDatabase? = null

        fun getDatabase(context: Context): ImageDatabase? {
            if (INSTANCE == null) {
                synchronized(ImageDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            ImageDatabase::class.java, "image_table"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}