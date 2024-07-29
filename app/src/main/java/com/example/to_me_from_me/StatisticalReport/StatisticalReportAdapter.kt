package com.example.to_me_from_me.StatisticalReport

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class StatisticalReportAdapter (activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MonthlyReportFragment() // 첫 번째 프래그먼트
            1 -> AnnualReportFragment() // 두 번째 프래그먼트
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}