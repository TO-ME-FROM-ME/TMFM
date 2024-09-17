package com.example.to_me_from_me.Mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R

class
DeleteAccActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deleteacc)

        val backButton: ImageView = findViewById(R.id.back_iv)

        backButton.setOnClickListener {
            //MyPageFragment로 이동하기
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToLoad", "MyPageFragment")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}