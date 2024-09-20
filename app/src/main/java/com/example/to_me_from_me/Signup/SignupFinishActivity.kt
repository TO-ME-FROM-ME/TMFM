package com.example.to_me_from_me.Signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.example.to_me_from_me.R
import com.example.to_me_from_me.SetTest.SETestActivity

class SignupFinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_finsh)



        val email = intent.getStringExtra("email")
        val pwd = intent.getStringExtra("pwd")
        val nickname = intent.getStringExtra("nickname")


        val testButton = findViewById<Button>(R.id.test_btn)

        val backButton = findViewById<ImageView>(R.id.back_iv)
        backButton.setOnClickListener {
            // 이전 Activity로 이동
            finish()
        }

        testButton.setOnClickListener {
            val intent = Intent(this, SETestActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("pwd", pwd)
            intent.putExtra("nickname",nickname)
            startActivity(intent)
        }

        Log.d("로그인 정보","email : $email , pwd : $pwd , nickname : $nickname")
    }
}