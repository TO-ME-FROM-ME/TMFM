package com.example.to_me_from_me

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class LetterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter)


        val reservBtn : Button = findViewById(R.id.reserve_btn)
        val sendBtn : Button = findViewById(R.id.send_btn)



        reservBtn.setOnClickListener {
            val dialogFragment = CalendarDialogFragment()
            dialogFragment.show(supportFragmentManager, "CalendarDialogFragment")
        }
    }



}