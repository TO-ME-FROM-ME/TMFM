package com.example.to_me_from_me.Mailbox

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class MailboxViewModel : ViewModel() {
    private val _randomLetterLoaded = MutableLiveData<Boolean>()
    val randomLetterLoaded: LiveData<Boolean> get() = _randomLetterLoaded


    // 랜덤 날짜 편지 목록
    private val _randomDateLetters = MutableLiveData<List<DocumentSnapshot>>()





    private val _randomLetterData = MutableLiveData<Map<String, Any?>>()
    val randomLetterData: LiveData<Map<String, Any?>> get() = _randomLetterData

    private val _hasLetterToday = MutableLiveData<Boolean>()
    val hasLetterToday: LiveData<Boolean> get() = _hasLetterToday

    fun setRandomLetterLoaded(loaded: Boolean) {
        _randomLetterLoaded.value = loaded
    }

    fun setRandomLetterData(letterData: Map<String, Any?>) {
        _randomLetterData.value = letterData
    }

    // 랜덤 날짜 편지 목록 설정
    fun setRandomDateLetters(letters: List<DocumentSnapshot>) {
        _randomDateLetters.value = letters
    }

    fun getRandomDateLetters(): List<DocumentSnapshot> {
        return _randomDateLetters.value ?: emptyList()
    }

    fun checkLetterToday(hasLetter: Boolean) {
        _hasLetterToday.value = hasLetter
    }
}

