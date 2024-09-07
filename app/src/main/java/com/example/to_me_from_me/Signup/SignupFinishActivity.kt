package com.example.to_me_from_me.Signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.example.to_me_from_me.R

class SignupFinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_finsh)



        val email = intent.getStringExtra("email")
        val pwd = intent.getStringExtra("pwd")
        val nickname = intent.getStringExtra("nickname")


        val backButton = findViewById<ImageView>(R.id.back_iv)
        backButton.setOnClickListener {
            // 이전 Activity로 이동
            finish()
        }

        Log.d("로그인 정보","email : $email , pwd : $pwd , nickname : $nickname")
    }
}