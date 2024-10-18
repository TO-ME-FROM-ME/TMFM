package com.example.to_me_from_me.MainAlarm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.LetterWrite.AdjectiveQ1Adapter
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainAlarmFragment : Fragment() {
    private var situation: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_alarm, container, false)


        // RecyclerView 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_alarm)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // arguments에서 데이터 가져오기
        situation = arguments?.getString("situation")

        // 데이터 준비 (여기서는 예시로 static 데이터를 사용)
        val alarmDataList = mutableListOf<AlarmData>()

        // letterContent가 null이 아닌 경우 알람 데이터 리스트에 추가
        if (situation != null) {
            alarmDataList.add(
                AlarmData(
                    imgResId = R.drawable.ic_letter_alram,
                    title = "편지가 도착했어!",
                    letter = situation!!, // 예약된 편지 내용
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                )
            )
        } else {

        }

        // 어댑터 설정
        val adapter = MainAlarmAdapter(requireContext(), alarmDataList)
        recyclerView.adapter = adapter

        return view
    }

}