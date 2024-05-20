package com.example.to_me_from_me

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_SELECTED_DATE = "selectedDate"

        fun newInstance(selectedDate: String): TimePickerDialogFragment {
            val fragment = TimePickerDialogFragment()
            val args = Bundle()
            args.putString(ARG_SELECTED_DATE, selectedDate)
            fragment.arguments = args
            return fragment
        }
    }

    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(ARG_SELECTED_DATE)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.time_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timePicker: TimePicker = view.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(false)

        val okButton: Button = view.findViewById(R.id.ok_btn)
        okButton.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val selectedTime = "$hour:$minute"
            Toast.makeText(requireContext(), "선택된 시간: $selectedTime", Toast.LENGTH_SHORT).show()

            // 다음 액티비티에 전달할 Intent 생성
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("selectedTime", selectedTime)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)

            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all) // 배경으로 round_corner.xml 설정
    }
}