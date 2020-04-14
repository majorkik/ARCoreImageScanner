package com.majorik.arcoreimagescanner.di

import android.app.Application
import androidx.room.Room
import com.majorik.arcoreimagescanner.data.dao.ImageDao
import com.majorik.arcoreimagescanner.data.database.ImageDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseComponent = module {

    fun provideDatabase(application: Application): ImageDatabase {
        return Room.databaseBuilder(application, ImageDatabase::class.java, "imgs.database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


    fun provideDao(database: ImageDatabase): ImageDao {
        return database.messageDao()
    }

    single { provideDatabase(androidApplication()) }
    single { provideDao(get()) }
}