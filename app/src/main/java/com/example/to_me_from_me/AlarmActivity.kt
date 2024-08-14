package com.example.to_me_from_me

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import androidx.fragment.app.DialogFragment

class
AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val backButton: ImageView = findViewById(R.id.back_iv)
        val alarmTime = findViewById<ImageView>(R.id.alarmtime_go)
        val switchAll: Switch = findViewById(R.id.switch_all)
        val switchAllOnLayout: LinearLayout = findViewById(R.id.switch_all_on)

        switchAllOnLayout.visibility = LinearLayout.GONE

        backButton.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        alarmTime.setOnClickListener {
            val dialogFragment = AlarmTimeDialogFragment()
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedBottomSheetDialogTheme)
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