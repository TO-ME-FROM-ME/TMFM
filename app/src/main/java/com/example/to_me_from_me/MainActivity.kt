package com.example.to_me_from_me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.add
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val selectedDate = intent.getStringExtra("selectedDate")
        val selectedTime = intent.getStringExtra("selectedTime")

        // 가져온 값 사용 예시
        Log.d("NextActivity", "선택된 날짜: $selectedDate")
        Log.d("NextActivity", "선택된 시간: $selectedTime")


    }

}