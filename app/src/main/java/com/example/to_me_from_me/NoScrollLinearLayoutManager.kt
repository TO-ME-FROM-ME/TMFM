package com.example.to_me_from_me

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NoScrollLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(context, orientation, reverseLayout) {
    override fun canScrollHorizontally(): Boolean {
        return false // 수평 스크롤 비활성화
    }

    override fun canScrollVertically(): Boolean {
        return false // 수직 스크롤 비활성화 (필요할 경우)
    }
}