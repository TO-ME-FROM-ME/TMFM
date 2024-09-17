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
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.LetterWrite.CustomTimePicker
class AlarmTimeDialogFragment : DialogFragment() {
    private val tag = "AlarmTimeDialogFragment"

    companion object {
        const val SELECTED_TIME = "selected_time"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.alarm_time_dialog, container, false)

        val closeIv: Button = view.findViewById(R.id.cancel_btn)
        closeIv.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timePicker: CustomTimePicker = view.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(false)

        val okButton: Button = view.findViewById(R.id.ok_btn)
        okButton.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val selectedTime = String.format("%02d:%02d", hour, minute) // 시간과 분을 두 자리 숫자로 포맷

            Log.d(tag, "Selected Time: $selectedTime") // 로그에 선택한 시간 기록

            // 선택된 시간을 담아 Intent 생성
            val intent = Intent().apply {
                putExtra(SELECTED_TIME, selectedTime)
            }
            // Activity에 결과 반환
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

            dismiss() // 다이얼로그 닫기
        }

        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle("몇 시에 받고 싶어?")
        }
    }
}


