package com.example.to_me_from_me

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModel : ViewModel() {
    private val _situationText = MutableLiveData<String>()
    val situationText: LiveData<String> get() = _situationText

    private val _selectedImageResId = MutableLiveData<Int>()
    val selectedImageResId: LiveData<Int> get() = _selectedImageResId

    fun setSituationText(text: String) {
        _situationText.value = text
    }
    fun setSelectedImageResId(resId: Int) {
        _selectedImageResId.value = resId
    }
}