package com.example.to_me_from_me.Mailbox

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
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.R
import java.util.Calendar

class MonthPickerDialogFragment(private var selectedYear: Int, private var selectedMonth: Int) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.month_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yearBackIv = view.findViewById<ImageView>(R.id.year_back_iv)

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
                selectedMonth = monthTextViews.indexOf(textView)+1
                monthText.text="2024년 ${selectedMonth}월"

                Log.d("MonthPicker","$selectedMonth")
            }
        }

        yearBackIv.setOnClickListener {
            selectedYear -= 1 // 연도 감소
            monthText.text = "${selectedYear}년 ${selectedMonth}월"
        }


        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            dismiss()
        }

        val okButton: Button = view.findViewById(R.id.ok_btn)
        okButton.setOnClickListener {
            // 선택 된 월 MonthAdapter에 넘긴 후 해당 월 보여주기
            (activity  as? MonthSelectionListener)?.onMonthSelected(selectedMonth)
            Log.d("MonthPicker","MonthPickerDialog $selectedMonth")
            dismiss()
        }

    }

    interface MonthSelectionListener {
        fun onMonthSelected(month: Int)
    }
}
