package com.example.to_me_from_me.Mailbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.R
import java.util.Date

class DayAdapter(
    private val tempMonth: Int,
    private val dayList: MutableList<Date>,
    private val onDayClickListener: (Date) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<DayAdapter.DayView>() {
    val ROW =6
    class DayView(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_mailbox_day,parent,false)
        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {

        //초기화
        var dayText = holder.layout.findViewById<TextView>(R.id.item_day_text)


        //날짜 표시
        dayText.text = dayList[position].date.toString()
        if(tempMonth != dayList[position].month) {
            dayText.alpha=0.4f
        }

        //토요일이면 파란색 || 일요일이면 빨간색으로 색상표시
        if((position + 1) % 7 == 0) {
            dayText.setTextColor(ContextCompat.getColor(holder.layout.context,R.color.black))
        } else if (position == 0 || position % 7 == 0) {
            dayText.setTextColor(ContextCompat.getColor(holder.layout.context,R.color.black))
        }


        // 날짜 클릭 이벤트 설정
        holder.layout.setOnClickListener {
            onDayClickListener(dayList[position]) // 클릭한 날짜를 리스너로 전달
        }
    }


    override fun getItemCount(): Int {
        return ROW*7
    }
}