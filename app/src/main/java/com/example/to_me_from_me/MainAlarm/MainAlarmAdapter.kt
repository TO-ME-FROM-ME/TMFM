package com.example.to_me_from_me.MainAlarm

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.LetterWrite.ButtonData
import com.example.to_me_from_me.R

class MainAlarmAdapter(
    private val context: Context,
    private val alarmDataList: List<AlarmData>,
) : RecyclerView.Adapter<MainAlarmAdapter.AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_alram_layout, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarmData = alarmDataList[position]
        holder.bind(alarmData)
//        holder.itemView.setOnClickListener {
//            onButtonClickListener(buttonData)
//        }
    }

    override fun getItemCount(): Int = alarmDataList.size

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //private val layout : ConstraintLayout = itemView.findViewById(R.id.linear_ll)
        private val img : ImageView = itemView.findViewById(R.id.icon_iv)
        private val title : TextView = itemView.findViewById(R.id.title_tv)
        private val letter : TextView = itemView.findViewById(R.id.letter_tv)
        private val time : TextView = itemView.findViewById(R.id.time_tv)

        val buttonBg = ContextCompat.getDrawable(itemView.context, R.drawable.solid_no_main2)


        fun bind(alarmData: AlarmData) {
            //img.setImageResource(buttonData.imgResId)  // 이미지 리소스 설정
            img.setImageResource(alarmData.imgResId)
            title.text = alarmData.title              // 제목 설정
            letter.text = alarmData.letter            // 본문 설정
            time.text = alarmData.time                // 시간 설정
        }
    }
}
