package com.example.to_me_from_me.Mailbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.R
import java.util.*

class DayAdapter(
    private val tempMonth: Int,
    private val dayList: MutableList<Date>,
    private val onDayClickListener: (Date) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<DayAdapter.DayView>() {
    val ROW = 5

    class DayView(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mailbox_day, parent, false)

        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        // 초기화
        val dayText = holder.layout.findViewById<TextView>(R.id.item_day_text)
        val todayIv = holder.layout.findViewById<ImageView>(R.id.today_iv)


        // 날짜 표시
        val currentDate = dayList[position]
        dayText.text = currentDate.date.toString()

        // 현재 월과 비교하여 다른 월의 날짜는 반투명 처리
        if (tempMonth != currentDate.month) {
            dayText.alpha = 0.4f
        } else {
            dayText.alpha = 1.0f
        }

        // 시스템 날짜와 비교하여 색상 변경
        val today = Calendar.getInstance()
        if (currentDate.date == today.get(Calendar.DAY_OF_MONTH) &&
            currentDate.month == today.get(Calendar.MONTH) &&
            currentDate.year == today.get(Calendar.YEAR) - 1900 // Date의 year는 1900부터 시작
        ) {
            dayText.setTextColor(ContextCompat.getColor(holder.layout.context, android.R.color.black))
            todayIv.isVisible=true

        } else {
            dayText.setTextColor(ContextCompat.getColor(holder.layout.context, R.color.Gray1)) // 다른 날짜의 기본 색상
            todayIv.isVisible=false
        }

        // 날짜 클릭 이벤트 설정
        holder.layout.setOnClickListener {
            onDayClickListener(dayList[position]) // 클릭한 날짜를 리스너로 전달
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }
}
