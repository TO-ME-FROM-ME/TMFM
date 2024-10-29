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

    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> get() = _currentDate

    fun setSituationText(text: String) {
        _situationText.value = text
    }

    fun setSelectedImageResId(resId: Int) {
        _selectedImageResId.value = resId
    }

    fun setSelectedButtonData(data: List<ButtonData>) {
        _selectedButtonData.value = data
    }

    fun setCurrentDate(date: String) {
        _currentDate.value = date
    }

    private val _situation = MutableLiveData<String?>(null) // 초기 값을 null로 설정
    val situation: LiveData<String?> get() = _situation

    private val _emoji = MutableLiveData<String?>()
    val emoji: LiveData<String?> get() = _emoji

    private val _ad1 = MutableLiveData<String>()
    val ad1: LiveData<String> get() = _ad1

    private val _ad2 = MutableLiveData<String>()
    val ad2: LiveData<String> get() = _ad2

    private val _q1 = MutableLiveData<String>()
    val q1: LiveData<String> get() = _q1

    private val _q2 = MutableLiveData<String>()
    val q2: LiveData<String> get() = _q2

    private val _q3 = MutableLiveData<String>()
    val q3: LiveData<String> get() = _q3

    private val _letter = MutableLiveData<String>()
    val letter: LiveData<String> get() = _letter


    fun setSituationF(situation: String) {
        _situation.value = situation
    }

    fun setEmojiF(emoji: String) {
        _emoji.value = emoji
    }

    fun setAd1(ad1: String) {
        _ad1.value = ad1
    }

    fun setAd2(ad2: String) {
        _ad2.value = ad2
    }

    fun setQ1(q1: String) {
        _q1.value = q1
    }

    fun setQ2(q2: String) {
        _q2.value = q2
    }

    fun setQ3(q3: String) {
        _q3.value = q3
    }

    fun setLetter(letter: String) {
        _letter.value = letter
    }


    fun clearSituationText() {
        _situation.value = null
    }
}
