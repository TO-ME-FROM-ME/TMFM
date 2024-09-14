package com.example.to_me_from_me.LetterWrite

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.R
import java.util.Calendar

class CalendarDialogFragment : DialogFragment() {

    private var selectedDate: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.calendar_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        val okButton: Button = view.findViewById(R.id.ok_btn)

        // 현재 날짜 가져오기
        val calendar = Calendar.getInstance()
        val currentTimeInMillis = calendar.timeInMillis

        // 현재 날짜 이후만 선택 가능하도록 설정
        calendarView.minDate = currentTimeInMillis


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
            //Toast.makeText(requireContext(), "선택된 날짜: $selectedDate", Toast.LENGTH_SHORT).show()
        }


        okButton.setOnClickListener {
            selectedDate?.let {
                //Toast.makeText(requireContext(), "선택된 날짜: $it", Toast.LENGTH_SHORT).show()
                // 시간 설정 다이얼로그 표시
                val timePickerDialogFragment = TimePickerDialogFragment.newInstance(it)
                timePickerDialogFragment.show(parentFragmentManager, "timePicker")

                dismiss()
            } ?: run {
                //Toast.makeText(requireContext(), "날짜를 먼저 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            // 취소 버튼 클릭 시 동작
            dismiss() // 다이얼로그 닫기
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