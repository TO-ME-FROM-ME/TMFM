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

        // 1주일 전 날짜 계산
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, -7) // 현재 날짜에서 7일 전
        val oneWeekAgoDate = calendar.time

        firestore.collection("users")
            .document(userId!!)
            .collection("letters")
            .get()
            .addOnSuccessListener { letterDocuments ->
                if (!letterDocuments.isEmpty) {
                    for (document in letterDocuments) {
                        val reservedDate = document.getString("reservedate")
                        val letterContent = document.getString("situation")
                        val time = SimpleDateFormat("hh:mm", Locale.getDefault()).format(currentDate)

                        if (reservedDate != null && letterContent != null) {
                            val reservedDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            val reservedDateParsed = reservedDateFormat.parse(reservedDate)

                            if (reservedDateParsed != null) {
                                // 'reservedate'가 현재 날짜보다 이전이고, 1주일 전 날짜 이후인 경우 추가
                                val isWithinOneWeek = reservedDateParsed.after(oneWeekAgoDate) && reservedDateParsed.before(currentDate)
                                if (isWithinOneWeek) {
                                    alarmDataList.add(
                                        AlarmData(
                                            imgResId = R.drawable.ic_letter_alram,
                                            title = "편지가 도착했어!",
                                            letter = letterContent,
                                            time = time,
                                            reservedate = reservedDate,
                                            clicked = false
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // 시간 기준 내림차순으로 정렬
                alarmDataList.sortByDescending {
                    val reservedDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    reservedDateFormat.parse(it.reservedate) ?: Date(0)
                }

                // 필터링된 결과를 콜백으로 반환
                callback(alarmDataList)
            }
            .addOnFailureListener { e ->
                Log.e("MainAlarmFragment", "예약된 편지 가져오기 실패", e)
            }
    }



}