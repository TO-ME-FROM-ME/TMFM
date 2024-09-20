package com.example.to_me_from_me

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.to_me_from_me.LetterWrite.CustomTimePicker
import com.example.to_me_from_me.LetterWrite.RecorderFragment
import com.example.to_me_from_me.LetterWrite.TimePickerDialogFragment

class AlarmTimeDialogFragment : DialogFragment() {
    private val tag = "AlarmTimeDialogFragment"
    private lateinit var viewModel: SharedViewModel

    companion object {
        private const val SELECTED_TIME = "selected_time"
    }

    private var selectedDate: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(SELECTED_TIME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.alarm_time_dialog, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


        val closeIv: Button = view.findViewById(R.id.cancel_btn)
        closeIv.setOnClickListener {
            dismiss()
        }

        val timePicker: CustomTimePicker = view.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(false)

        val okButton: Button = view.findViewById(R.id.ok_btn)
        okButton.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val selectedTime = String.format("%02d:%02d", hour, minute)
            Log.d("selectedTime","selectedTime : $selectedTime")
            viewModel.selectedData.value = "$selectedTime"

            dismiss()

        }

        return view
    }


    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle("몇 시에 받고 싶어?")
        }
    }
}


