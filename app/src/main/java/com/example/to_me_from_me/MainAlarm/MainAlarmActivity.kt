package com.example.to_me_from_me.MainAlarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainAlarmActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null
    private var clickedAt: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_alarm)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid


        // 예약 편지 알림을 통해 이동했는지 확인
        val fromNotification = intent.getBooleanExtra("fromNotification", false)
        if (fromNotification && userId != null) {
            // 알림을 통해 이동한 정보를 Firebase에 저장
            saveNotificationClickToFirebase()
        }

        val backButton: ImageView = findViewById(R.id.back_iv)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // 알림 클릭 정보를 Firebase에 저장
    private fun saveNotificationClickToFirebase() {
        // clickedAt을 yyyy-MM-dd HH:mm 형식의 문자열로 변환
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        clickedAt = dateFormat.format(System.currentTimeMillis())

        if (clickedAt != null) {
            val notificationData = hashMapOf(
                "clickedAt" to clickedAt,
                "fromNotification" to true
            )

            firestore.collection("users")
                .document(userId!!)
                .collection("notifications")
                .document(clickedAt!!) // clickedAt 값을 문서 이름으로 사용
                .set(notificationData)
                .addOnSuccessListener {
                    //Log.d("main알람", "알림 클릭 정보가 users 컬렉션에 성공적으로 저장되었습니다.")
                    // 예약된 편지 내용 가져오기
                    fetchReservedLetter()
                }
                .addOnFailureListener { e ->
                    Log.e("main알람", "알림 클릭 정보 저장 실패", e)
                }
        } else {
            Log.e("main알람", "clickedAt이 null입니다.")
        }
    }

    // 예약된 편지 내용 가져오기
    private fun fetchReservedLetter() {
        // 현재 날짜를 "yyyy-MM-dd" 형식의 문자열로 변환
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDateString = dateFormat.format(Date())

        // 1. letters 컬렉션에서 예약된 편지 가져오기
        firestore.collection("users")
            .document(userId!!)
            .collection("letters")
            .get()
            .addOnSuccessListener { letterDocuments ->
                if (!letterDocuments.isEmpty) {
                    for (document in letterDocuments) {
                        val reservedDate  = document.getString("reservedate")
                        Log.d("main알람", "reservedDate: $reservedDate \n currentDateString : $currentDateString")
                        // reservedDate가 null이 아니고 clickedAt과 같으면
                        if (reservedDate != null && reservedDate == clickedAt) {
                            Log.d("main알람", "예약된 편지 내용의 reservedDate와 clickedAt이 일치합니다: $reservedDate")

                            val situation = document.getString("situation")

                            loadFragment(MainAlarmFragment(), situation)
                        }
                    }
                } else {
                    Log.d("main알람", "일치하는 예약된 편지가 없습니다.")
                    loadFragment(MainNoAlarmFragment(), null)
                }
            }


    }



    private fun loadFragment(fragment: Fragment, situation:String?) {
        val bundle = Bundle().apply {
            putString("situation", situation) // 값을 Bundle에 추가
        }
        fragment.arguments = bundle // Fragment에 arguments 설정

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
