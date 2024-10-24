package com.example.to_me_from_me.MainAlarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainAlarmFragment : Fragment() {
    private var situation: String? = null
    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_alarm, container, false)

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid

        // RecyclerView 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_alarm)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // 알람 데이터 리스트
        val alarmDataList = mutableListOf<AlarmData>()

        // 예약된 편지 가져오기
        fetchReservedLetters(alarmDataList) { filteredAlarmDataList ->
            // 어댑터 설정
            val adapter = MainAlarmAdapter(requireContext(), filteredAlarmDataList)
            recyclerView.adapter = adapter
        }

        return view
    }

    private fun fetchReservedLetters(alarmDataList: MutableList<AlarmData>, callback: (List<AlarmData>) -> Unit) {
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 7) // 현재 날짜 기준으로 일주일 뒤 날짜
        val endDate = calendar.time

        // Firestore에서 예약된 편지 가져오기
        firestore.collection("users")
            .document(userId!!)
            .collection("letters")
            .whereGreaterThanOrEqualTo("reservedate", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(currentDate))
            .whereLessThanOrEqualTo("reservedate", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(endDate))
            .get()
            .addOnSuccessListener { letterDocuments ->
                if (!letterDocuments.isEmpty) {
                    for (document in letterDocuments) {
                        val reservedDate = document.getString("reservedate")
                        val letterContent = document.getString("situation") // 편지 내용 가져오기
                        val time = SimpleDateFormat("hh:mm", Locale.getDefault()).format(currentDate) // 예시로 현재 시간 설정

                        // 알람 데이터 리스트에 추가
                        if (reservedDate != null && letterContent != null) {
                            alarmDataList.add(
                                AlarmData(
                                    imgResId = R.drawable.ic_letter_alram,
                                    title = "편지가 도착했어!",
                                    letter = letterContent,
                                    time = time,
                                    reservedate=reservedDate
                                )
                            )
                        }
                    }
                }
                // 콜백 호출하여 필터링된 알람 데이터 리스트 반환
                callback(alarmDataList)
            }
            .addOnFailureListener { e ->
                Log.e("MainAlarmFragment", "예약된 편지 가져오기 실패", e)
            }
    }
}
