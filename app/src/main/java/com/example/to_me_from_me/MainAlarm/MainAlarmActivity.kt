package com.example.to_me_from_me.MainAlarm

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.Mailbox.DetailMailBoxActivity
import com.example.to_me_from_me.Mailbox.DetailMailBoxFragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.MusicService
import com.example.to_me_from_me.R
import com.example.to_me_from_me.startMusicService
import com.example.to_me_from_me.stopMusicService
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


        // SharedPreferences에서 저장된 값 확인
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val situation = sharedPreferences.getString("situation", null)
        val reservedate = sharedPreferences.getString("reservedate", null)
        val emoji = sharedPreferences.getString("emoji", null)
        val ad1 = sharedPreferences.getString("ad1", null)
        val ad2 = sharedPreferences.getString("ad2", null)
        val readStatus = sharedPreferences.getBoolean("readStatus", false)
        Log.d("SharedPreferences", "situation: $situation, reservedate: $reservedate, emoji: $emoji, ad1: $ad1, ad2: $ad2, readStatus: $readStatus")


        if (situation != null && reservedate != null) {
            // 저장된 값이 있는 경우 MainAlarmFragment 로드
            loadFragment(MainAlarmFragment(), situation, reservedate)
        } else {
            // 저장된 값이 없는 경우 MainNoAlarmFragment 로드
            loadFragment(MainNoAlarmFragment(), null, null)
        }


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
        // 현재 날짜를 "yyyy-MM-dd HH:mm" 형식의 문자열로 변환
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDateString = dateFormat.format(Date())

        // 1. letters 컬렉션에서 예약된 편지 가져오기
        firestore.collection("users")
            .document(userId!!)
            .collection("letters")
            .get()
            .addOnSuccessListener { letterDocuments ->
                if (!letterDocuments.isEmpty) {
                    // 모든 문서에서 reservedate가 있는 경우만 저장
                    for (document in letterDocuments) {
                        val reservedDate = document.getString("reservedate")

                        if (reservedDate != null) {
                            Log.d("main알람", "reservedDate: $reservedDate \n currentDateString : $currentDateString")

                            val situation = document.getString("situation")
                            val emoji = document.getString("emoji") // 필요에 따라 추가
                            val ad1 = document.getString("ad1") // 필요에 따라 추가
                            val ad2 = document.getString("ad2") // 필요에 따라 추가
                            val readStatus = document.getBoolean("readStatus")

                            // SharedPreferences에 저장
                            saveDataToSharedPreferences(situation, reservedDate, emoji, ad1, ad2, readStatus)
                        }
                    }
                } else {
                    Log.d("main알람", "일치하는 예약된 편지가 없습니다.")
                    loadFragment(MainNoAlarmFragment(), null, null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("main알람", "예약된 편지 가져오기 실패", e)
            }
    }


    private fun saveDataToSharedPreferences(situation: String?, reservedate: String, emoji: String?, ad1: String?, ad2: String?, readStatus: Boolean?)
    {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("situation", situation)
        editor.putString("reservedate", reservedate)
        editor.putString("emoji", emoji)
        editor.putString("ad1", ad1)
        editor.putString("ad2", ad2)
        editor.putBoolean("readStatus", readStatus ?: false)
        editor.putString("letter", "receive")
        editor.apply() // 비동기적으로 저장

        // 한 번에 확인할 수 있는 로그 출력
        Log.d("흘러온 편지", "Saving data: " +
                "situation: $situation, " +
                "reservedate: $reservedate, " +
                "emoji: $emoji, " +
                "ad1: $ad1, " +
                "ad2: $ad2, " +
                "readStatus: ${readStatus ?: false}"
        )

    }


    private fun loadFragment(fragment: Fragment, situation:String?, reservedate:String?) {
        val bundle = Bundle().apply {
            putString("situation", situation) // 값을 Bundle에 추가
            putString("reservedate", reservedate)
        }
        fragment.arguments = bundle // Fragment에 arguments 설정

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
