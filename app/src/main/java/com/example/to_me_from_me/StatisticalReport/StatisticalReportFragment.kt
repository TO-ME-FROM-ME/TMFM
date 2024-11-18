package com.example.to_me_from_me.StatisticalReport

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.to_me_from_me.MusicService
import com.example.to_me_from_me.R
import com.example.to_me_from_me.startMusicService
import com.example.to_me_from_me.stopMusicService

class StatisticalReportFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var switch: SwitchCompat
    private lateinit var monthTextView: TextView
    private lateinit var annualTextView: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 inflate
        return inflater.inflate(R.layout.fragment_statistical_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 초기화
        viewPager = view.findViewById(R.id.report_vp2)
        switch = view.findViewById(R.id.switchOnOff_sc)
        monthTextView = view.findViewById(R.id.month_tv)
        annualTextView = view.findViewById(R.id.annual_tv)

        // ViewPager 어댑터 설정
        viewPager.adapter = StatisticalReportAdapter(requireActivity())

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
            resources.getColor(if (isMonthSelected) R.color.black else R.color.Gray4, null)
        )
        annualTextView.setTextColor(
            resources.getColor(if (isMonthSelected) R.color.Gray4 else R.color.black, null)
        )
    }
}
