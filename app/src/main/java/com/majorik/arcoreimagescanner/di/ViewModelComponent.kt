package com.majorik.arcoreimagescanner.di

import com.majorik.arcoreimagescanner.ui.arscene.ScannerViewModel
import com.majorik.arcoreimagescanner.ui.library.LibraryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelComponent = module {
    viewModel { ScannerViewModel(get()) }
    viewModel { LibraryViewModel(get()) }
}