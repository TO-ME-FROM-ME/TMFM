package com.example.to_me_from_me.Mailbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.R
import java.util.Calendar
import java.util.Date

class MonthAdapter(private val onDayClickListener: (Date) -> Unit) : RecyclerView.Adapter<MonthAdapter.Month>() {
    var calendar: Calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Month {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mailbox_month, parent, false)
        return Month(view)
    }

    override fun onBindViewHolder(holder: Month, position: Int) {
        // 리사이클러뷰 초기화
        val listLayout = holder.view.findViewById<RecyclerView>(R.id.month_recycler)

        calendar.time = Date() // 현재 날짜 초기화
        calendar.set(Calendar.DAY_OF_MONTH, 1) // 스크롤 시 현재 월의 1일로 이동
        calendar.add(Calendar.MONTH, position) // 스크롤 시 포지션만큼 달 이동

        // title 텍스트 초기화
        val titleText: TextView = holder.view.findViewById(R.id.title)
        titleText.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        val tempMonth = calendar.get(Calendar.MONTH)

        // 6주 7일로 날짜를 표시
        val dayList: MutableList<Date> = MutableList(6 * 7) { Date() }
        for (i in 0..5) { // 주
            for (k in 0..6) { // 요일
                calendar.add(Calendar.DAY_OF_MONTH, (1 - calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time // 배열 인덱스만큼 요일 데이터 저장
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        listLayout.layoutManager = GridLayoutManager(holder.view.context, 7)
        // DayAdapter에 클릭 리스너를 전달
        listLayout.adapter = DayAdapter(tempMonth, dayList) { clickedDate ->
            onDayClickListener(clickedDate) // 날짜 클릭 시 리스너 호출
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE / 2
    }

    class Month(val view: View) : RecyclerView.ViewHolder(view)
}
