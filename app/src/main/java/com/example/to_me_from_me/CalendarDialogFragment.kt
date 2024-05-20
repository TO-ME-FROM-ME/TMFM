package com.example.to_me_from_me

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class CalendarDialogFragment : DialogFragment() {

    private var selectedDate: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.calendar_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        val okButton: Button = view.findViewById(R.id.ok_btn)


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
            Toast.makeText(requireContext(), "선택된 날짜: $selectedDate", Toast.LENGTH_SHORT).show()
        }


        okButton.setOnClickListener {
            selectedDate?.let {
                Toast.makeText(requireContext(), "선택된 날짜: $it", Toast.LENGTH_SHORT).show()
                // 시간 설정 다이얼로그 표시
                //TimePickerDialogFragment().show(parentFragmentManager, "timePicker")

                val timePickerDialogFragment = TimePickerDialogFragment.newInstance(it)
                timePickerDialogFragment.show(parentFragmentManager, "timePicker")

                dismiss()
            } ?: run {
                Toast.makeText(requireContext(), "날짜를 먼저 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all) // 배경으로 round_corner.xml 설정
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            //setTitle("날짜 선택")
        }
    }
}