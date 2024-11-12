package com.example.to_me_from_me.LetterWrite

import android.graphics.Color

data class ButtonData(
    val buttonText: String,
    var isSelected: Boolean = false,
    var backgroundColor: Int = Color.WHITE // 기본 배경색
)
