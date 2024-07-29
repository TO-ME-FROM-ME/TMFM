package com.example.to_me_from_me.StatisticalReport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.to_me_from_me.R

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

        // TextView 초기 상태 설정
        monthTextView.setTextColor(resources.getColor(R.color.white))
        annualTextView.setTextColor(resources.getColor(R.color.black))

        // Switch 상태 변경 리스너 설정
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewPager.currentItem = 1
                monthTextView.setTextColor(resources.getColor(R.color.black))
                annualTextView.setTextColor(resources.getColor(R.color.white))
            } else {
                viewPager.currentItem = 0
                monthTextView.setTextColor(resources.getColor(R.color.white))
                annualTextView.setTextColor(resources.getColor(R.color.black))
            }
        }


    }
}