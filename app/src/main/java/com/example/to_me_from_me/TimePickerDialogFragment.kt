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
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            intent.putExtra("selectedTime", selectedTime)
//            intent.putExtra("selectedDate", selectedDate)
//            startActivity(intent)

            val nextFragment = RecorderFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()

            dismiss()
        }

        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            // 취소 버튼 클릭 시 동작
            dismiss() // 다이얼로그 닫기
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all) // 배경으로 round_corner.xml 설정
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle("몇 시에 받고 싶어?")
        }
    }
}