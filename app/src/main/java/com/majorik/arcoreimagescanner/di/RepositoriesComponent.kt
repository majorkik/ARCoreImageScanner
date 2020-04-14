package com.majorik.arcoreimagescanner.di

import com.majorik.arcoreimagescanner.data.repositories.ImageRepository
import org.koin.dsl.module

val repositoriesComponent = module {
    factory { ImageRepository(get()) }
}