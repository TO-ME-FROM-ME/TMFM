package com.example.to_me_from_me

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

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
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }
}