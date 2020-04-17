package com.majorik.arcoreimagescanner.ui.add_image_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.majorik.arcoreimagescanner.data.repositories.ImageRepository
import com.soywiz.klock.DateTime
import kotlinx.coroutines.launch

class AddImageViewModel(private val repository: ImageRepository) : ViewModel() {

    fun addImage(title: String, path: String) {
        viewModelScope.launch {
            repository.addImage(title, path, DateTime.now().toString())
        }
    }
}