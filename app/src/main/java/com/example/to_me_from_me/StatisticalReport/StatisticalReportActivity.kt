package com.example.to_me_from_me.StatisticalReport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.to_me_from_me.R
import com.example.to_me_from_me.TestquitDialogFragment

class StatisticalReportActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var switch: SwitchCompat
    private lateinit var monthTextView: TextView
    private lateinit var annualTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistical_report)

        // View 초기화
        viewPager = findViewById(R.id.report_vp2)
        switch = findViewById(R.id.switchOnOff_sc)
        monthTextView = findViewById(R.id.month_tv)
        annualTextView = findViewById(R.id.annual_tv)

        // ViewPager 어댑터 설정
        viewPager.adapter = StatisticalReportAdapter(this)

        // 초기 페이지 설정
        viewPager.currentItem = 0

        // Switch 초기 상태 설정
        switch.isChecked = false

        // TextView 초기 상태 설정 (기본값: month_tv 선택)
        updateTextViewColors(isMonthSelected = true)

        // Switch 상태 변경 리스너 설정
        switch.setOnCheckedChangeListener { _, isChecked ->
            updateTextViewColors(isMonthSelected = !isChecked)
            viewPager.currentItem = if (isChecked) 1 else 0
        }

        // ViewPager 페이지 변경 리스너 설정
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Switch 상태 동기화
                switch.isChecked = position == 1
                updateTextViewColors(isMonthSelected = position == 0)
            }
        })

    }

    // TextView 색상 업데이트 메서드
    private fun updateTextViewColors(isMonthSelected: Boolean) {
        monthTextView.setTextColor(
            resources.getColor(if (isMonthSelected) R.color.black else R.color.Gray4)
        )
        annualTextView.setTextColor(
            resources.getColor(if (isMonthSelected) R.color.Gray4 else R.color.black)
        )
    }
}
