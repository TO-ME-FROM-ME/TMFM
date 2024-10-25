package com.example.to_me_from_me.StatisticalReport

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.R
import java.util.Calendar

class MonthPickerDialogFragment2
    (private var selectedYear: Int, private var selectedMonth: Int) : DialogFragment() {

    private lateinit var monthTextViews: List<TextView>
    private var selectedMonthIndex: Int = selectedMonth // 인덱스를 따로 유지
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.month_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yearBackIv = view.findViewById<ImageView>(R.id.year_back_iv)
        val yearNextIv = view.findViewById<ImageView>(R.id.year_next_iv)

        val mainDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.select_solid)
        val gridLayout: GridLayout = view.findViewById(R.id.month_grid)
        val monthTextViews = gridLayout.children.filterIsInstance<TextView>()
        val monthText = view.findViewById<TextView>(R.id.year_month_text)

        // 초기 연도와 월 표시
        monthText.text = "${selectedYear}년 ${selectedMonth+1}월"


        monthTextViews.forEach { textView ->
            textView.setOnClickListener {
                // 모든 TextView의 배경색상을 기본 색상으로 변경
                monthTextViews.forEach { tv -> tv.setBackgroundResource(R.drawable.solid_no_stroke) }

                // 클릭된 TextView의 배경색상을 main 색상으로 변경
                textView.background = mainDrawable

                // 선택된 월 저장
                selectedMonth = monthTextViews.indexOf(textView)
                monthText.text="${selectedYear}년 ${selectedMonth+1}월"
            }
        }

        yearBackIv.setOnClickListener {
            selectedYear -= 1 // 연도 감소
            monthText.text = "${selectedYear}년 ${selectedMonth}월"

        }


        yearNextIv.setOnClickListener {
            selectedYear += 1 // 연도 증가
            monthText.text = "${selectedYear}년 ${selectedMonth}월"
        }


        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            dismiss()
        }

        val okButton: Button = view.findViewById(R.id.ok_btn)
        okButton.setOnClickListener {
            (targetFragment as? MonthSelectionListener2)?.onMonthSelected2(selectedMonth, selectedYear)
            Log.d("MonthPicker2","MonthPickerDialog2 $selectedMonth,  $selectedYear")
            dismiss()
        }

    }


    interface MonthSelectionListener2 {
        fun onMonthSelected2(month: Int,  year: Int)
    }
}
