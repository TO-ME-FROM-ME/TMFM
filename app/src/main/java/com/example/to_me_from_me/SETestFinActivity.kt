package com.example.to_me_from_me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView

class SETestFinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setestfin)

        val totalScore = intent.getStringExtra("totalScore")
        Log.d("totalScore" ,"totalScore : $totalScore")
    }
}