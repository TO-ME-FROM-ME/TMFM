package com.example.to_me_from_me.Mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.AlarmTimeDialogFragment
import com.example.to_me_from_me.LetterWrite.CalendarDialogFragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R

class
AlarmActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_TIME_PICKER = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val backButton: ImageView = findViewById(R.id.back_iv)
        val alarmTime = findViewById<ImageView>(R.id.time_set_iv)
        val timeSetTextview = findViewById<TextView>(R.id.time_set_tv)
        val switchAll: Switch = findViewById(R.id.switch_all)
        val switchAllOnLayout: LinearLayout = findViewById(R.id.switch_all_on)

        switchAllOnLayout.visibility = LinearLayout.GONE

        backButton.setOnClickListener {
            //MyPageFragment로 이동하기
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToLoad", "MyPageFragment")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        alarmTime.setOnClickListener {
            val dialogFragment = AlarmTimeDialogFragment()
            //dialogFragment.setTargetFragment(this, REQUEST_TIME_PICKER)
            dialogFragment.show(supportFragmentManager, "AlarmTimeDialogFragment")

        }



        switchAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchAllOnLayout.visibility = LinearLayout.VISIBLE
            } else {
                switchAllOnLayout.visibility = LinearLayout.GONE
            }
        }
    }
}