package com.example.to_me_from_me.LetterWrite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.to_me_from_me.LetterWrite.ButtonData

class ViewModel : ViewModel() {
    private val _situationText = MutableLiveData<String>()
    val situationText: LiveData<String> get() = _situationText

    private val _selectedImageResId = MutableLiveData<Int>()
    val selectedImageResId: LiveData<Int> get() = _selectedImageResId

    private val _selectedButtonData = MutableLiveData<List<ButtonData>>()


    fun setSituationText(text: String) {
        _situationText.value = text
    }

    fun setSelectedImageResId(resId: Int) {
        _selectedImageResId.value = resId
    }

    fun setSelectedButtonData(data: List<ButtonData>) {
        _selectedButtonData.value = data
    }
}
