package com.example.to_me_from_me.MainAlarm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.LetterWrite.AdjectiveQ1Adapter
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.R

class MainAlarmFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_alarm, container, false)


        // RecyclerView 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_alarm)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 데이터 준비
        val alarmDataList = listOf(
            AlarmData(R.drawable.ic_t_alram_letter, "편지 쓸 시간이야!", "나에게 따뜻한 편지를 작성해보자", "10:00 AM"),
            AlarmData(R.drawable.ic_f_alram_letter, "편지가 도착했어!", "시험 점수가 나왔는데 점수가 너무", "11:00 AM"),
            AlarmData(R.drawable.ic_t_alram_letter, "알림 3", "내용 3", "12:00 PM")
        )


        // 어댑터 설정 (데이터 예시로 빈 리스트 사용)
        val adapter = MainAlarmAdapter(requireContext(), alarmDataList)
        recyclerView.adapter = adapter


        return  view
    }
}