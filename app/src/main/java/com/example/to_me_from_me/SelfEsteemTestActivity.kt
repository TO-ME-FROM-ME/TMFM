package com.example.to_me_from_me


import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels

class SelfEsteemTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfesteem_test)

        // 초기 프래그먼트 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SeTestFragment()).commit()

    }

}