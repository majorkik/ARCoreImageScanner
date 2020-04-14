package com.majorik.arcoreimagescanner.ui.library

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.majorik.arcoreimagescanner.data.model.Image
import com.majorik.arcoreimagescanner.data.repositories.ImageRepository
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: ImageRepository) : ViewModel() {

    val imagesLiveData = MutableLiveData<List<Image>>()

    fun fetchImages() {
        viewModelScope.launch {
            val result = repository.getImages()

            imagesLiveData.postValue(result)
        }
    }
}