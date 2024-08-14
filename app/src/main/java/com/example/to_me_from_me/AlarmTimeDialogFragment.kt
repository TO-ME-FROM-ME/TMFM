package com.example.to_me_from_me

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import java.util.Calendar

class AlarmTimeDialogFragment : DialogFragment() {
    private val tag = "AlarmTimeDialogFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.alarm_time_dialog, container, false)

        val closeIv: Button = view.findViewById(R.id.cancel_btn)
        closeIv.setOnClickListener {
            dismiss()
        }

        return view
    }
}

