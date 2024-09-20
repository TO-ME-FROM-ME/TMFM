package com.example.to_me_from_me.Mypage

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.to_me_from_me.LetterWrite.CustomTimePicker
import com.example.to_me_from_me.R
import java.util.Calendar

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

            // 현재 날짜에 선택한 시간으로 Calendar 객체 생성
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }


//            val selectedTime = String.format("%02d:%02d", hour, minute)
            val selectedTime = calendar.timeInMillis // 밀리초로 변환

            viewModel.setSelectedTime(selectedTime) // ViewModel에 시간 저장
            Log.d("selectedTime","selectedTime : $selectedTime")

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


