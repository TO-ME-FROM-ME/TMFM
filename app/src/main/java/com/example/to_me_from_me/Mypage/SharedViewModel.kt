package com.example.to_me_from_me.Mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel: ViewModel() {
//    val selectedData = MutableLiveData<String>()
private val _selectedTime = MutableLiveData<Long>()
    val selectedTime: LiveData<Long> get() = _selectedTime

    fun setSelectedTime(timeInMillis: Long) {
        _selectedTime.value = timeInMillis
    }
}