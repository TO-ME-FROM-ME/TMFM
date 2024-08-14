package com.example.to_me_from_me

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import kotlin.math.log

class
MyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val userProfile = findViewById<ImageView>(R.id.user_go)
        val userAlarm = findViewById<ImageView>(R.id.alarm_go)
        val userLogout = findViewById<ImageView>(R.id.logout_go)
        val userDeleteAcc = findViewById<ImageView>(R.id.deleteacc_go)

        userProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        userAlarm.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        userLogout.setOnClickListener {
            val dialogFragment = LogoutDialogFragment()
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedBottomSheetDialogTheme)
            dialogFragment.show(supportFragmentManager, "LogoutDialogFragment")
        }

        userDeleteAcc.setOnClickListener {
            val intent = Intent(this, DeleteAccActivity::class.java)
            startActivity(intent)
        }
    }
}