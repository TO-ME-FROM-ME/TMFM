package com.example.to_me_from_me.LetterWrite


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.MusicService
import com.example.to_me_from_me.R

class WriteLetterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_letter)


        val backButton: ImageView = findViewById(R.id.back_iv)
        backButton.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

        val saveButton: ImageView = findViewById(R.id.save_iv)
        saveButton.setOnClickListener {
            val dialogFragment = StorageDialogFragment()
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialogFragment.show(supportFragmentManager, "StorageDialogFragment")
        }

        val situation = intent.getStringExtra("situation")
        val emoji = intent.getStringExtra("emoji")
        val ad1 = intent.getStringExtra("ad1")
        val ad2 = intent.getStringExtra("ad2")
        val q1 = intent.getStringExtra("q1")
        val q2 = intent.getStringExtra("q2")
        val q3 = intent.getStringExtra("q3")
        val letter = intent.getStringExtra("letter")

        val situationFragment = SituationFragment().apply {
            arguments = Bundle().apply {
                putString("situation", situation)
                putString("emoji", emoji)
                putString("ad1", ad1)
                putString("ad2", ad2)
                putString("q1", q1)
                putString("q2", q2)
                putString("q3", q3)
                putString("letter", letter)
                putBoolean("isFromCWriteBtn", true)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, situationFragment)
            .addToBackStack(null)
            .commit()

        /*// 초기 프래그먼트 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SituationFragment()).commit()*/

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

}