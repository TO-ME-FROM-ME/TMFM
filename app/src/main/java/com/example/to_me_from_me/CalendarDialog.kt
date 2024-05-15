package com.example.to_me_from_me

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.CalendarView

class CalendarDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.calendar_dialog)

        // CalendarView 설정
        val calendarView = findViewById<CalendarView>(R.id.calendarView)

    }
}