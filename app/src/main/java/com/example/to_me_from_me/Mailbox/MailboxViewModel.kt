package com.example.to_me_from_me.Mailbox

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MailboxViewModel : ViewModel() {
    private val _randomLetterLoaded = MutableLiveData<Boolean>()
    val randomLetterLoaded: LiveData<Boolean> get() = _randomLetterLoaded

    private val _randomLetterData = MutableLiveData<Map<String, Any?>>()
    val randomLetterData: LiveData<Map<String, Any?>> get() = _randomLetterData



    fun setRandomLetterLoaded(loaded: Boolean) {
        _randomLetterLoaded.value = loaded
    }

    fun setRandomLetterData(letterData: Map<String, Any?>) {
        _randomLetterData.value = letterData
        setRandomLetterLoaded(true) // 편지 데이터가 로드되었음을 표시
    }

}
