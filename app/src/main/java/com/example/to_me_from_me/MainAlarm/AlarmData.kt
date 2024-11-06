package com.example.to_me_from_me.MainAlarm

data class AlarmData(
    val imgResId: Int,    // 이미지 리소스 ID
    val title: String,    // 제목 텍스트
    val letter: String,   // 본문 텍스트
    val time: String,      // 시간 텍스트
    val reservedate: String,
    val clicked: Boolean
)
