package com.example.to_me_from_me.Mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.ProfileImgFragment
import com.example.to_me_from_me.R

class
EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val profileimg: ImageView = findViewById(R.id.profile_img)
        val backButton: ImageView = findViewById(R.id.back_iv)

        profileimg.setOnClickListener {
            val profileImgFragment = ProfileImgFragment()
        }

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