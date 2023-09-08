package com.reanimator.imagefromnetwork

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {
    private val _imageUrl = SingleLiveEvent<String>()
    val imageUrl: LiveData<String> = _imageUrl

    fun setNewImageUrl(newUrl: String) {
        _imageUrl.value = newUrl
    }
}