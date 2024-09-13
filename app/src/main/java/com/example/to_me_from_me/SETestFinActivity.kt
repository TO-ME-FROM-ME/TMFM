package com.example.to_me_from_me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView

class SETestFinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setestfin)

        val totalScore = intent.getIntExtra("totalScore", 0) // 기본값 0 설정
        Log.d("totalScore" ,"totalScore : $totalScore")


        val backButton = findViewById<ImageView>(R.id.back_iv)
        backButton.setOnClickListener {
            // 이전 Activity로 이동
            finish()
        }
    }
}