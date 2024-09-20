package com.example.to_me_from_me

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel: ViewModel() {
    val selectedData = MutableLiveData<String>()
}