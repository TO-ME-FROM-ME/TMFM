package com.example.to_me_from_me.StatisticalReport


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.R
import java.util.Calendar

class AnnualPickerDialogFragment(private var selectedYear: Int) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.annual_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yearText = view.findViewById<TextView>(R.id.year_text)

        // 초기 연도와 월 표시
        yearText.text = "${selectedYear}년"

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val yearPicker :CustomYearPicker = view.findViewById(R.id.yearPicker)

        // 여기서 minValue와 maxValue를 설정합니다.
        yearPicker.minValue = 2017
        yearPicker.maxValue = 2100
        yearPicker.value = currentYear



        yearPicker.setOnValueChangedListener { _, _, newVal ->
            // newVal 값이 선택된 연도입니다.
            selectedYear = newVal  // selectedYear 값을 업데이트
            Log.d("annual", "newVal 값이 선택된 연도 : $newVal")
        }



        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            dismiss()
        }

        val okButton: Button = view.findViewById(R.id.ok_btn)
        okButton.setOnClickListener {
            (targetFragment as? YearSelectionListener)?.onYearSelected(selectedYear)
            Log.d("annual","annual :  $selectedYear")
            dismiss()
        }


    }


    interface YearSelectionListener {
        fun onYearSelected(year: Int)
    }
}
