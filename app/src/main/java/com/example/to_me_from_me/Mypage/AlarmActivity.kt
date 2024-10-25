package com.example.to_me_from_me.Mypage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmActivity : AppCompatActivity() {
    lateinit var timeSetTextview: TextView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 2001
        private const val REQUEST_TIME_PICKER = 1001 // 시간 선택 요청 코드
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)


        Log.d("알람", "onCreate 호출됨")

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val backButton: ImageView = findViewById(R.id.back_iv)
        val alarmTime = findViewById<ImageView>(R.id.time_set_iv)
        val switchAll: Switch = findViewById(R.id.switch_all)
        val switchService: Switch = findViewById(R.id.switch_service)
        val switchLetter: Switch = findViewById(R.id.switch_letter)
        val switchRemind: Switch = findViewById(R.id.switch_remind)
        val switchAllOnLayout: LinearLayout = findViewById(R.id.switch_all_on)

        switchAllOnLayout.visibility = LinearLayout.GONE

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToLoad", "MyPageFragment")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }


        // SharedPreferences에서 알람 시간 읽기
        val sharedPref = getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        val hour = sharedPref.getInt("selected_hour", -1)
        val minute = sharedPref.getInt("selected_minute", -1)
        timeSetTextview = findViewById<TextView>(R.id.time_set_tv)
        alarmTime.setOnClickListener {
            val dialogFragment = AlarmTimeDialogFragment()
            dialogFragment.show(supportFragmentManager, "AlarmTimeDialogFragment")
        }


        // 시간을 포맷하고 TextView에 설정
        if (hour != -1 && minute != -1) {
            val period = if (hour < 12) "오전" else "오후"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val formattedTime = String.format("%s %02d시 %02d분", period, displayHour, minute)
            timeSetTextview.text = formattedTime // TextView 업데이트
        }

        Log.d("AlarmActivity", "받은 알람 시간: $hour 시 $minute 분")



        // Firestore에서 alarm 값을 가져와 초기 가시성 설정
        getAlarmStatusFromFirestore { isChecked ->
            switchAll.isChecked = isChecked
            switchAllOnLayout.visibility = if (isChecked) LinearLayout.VISIBLE else LinearLayout.GONE
        }


        switchAll.setOnCheckedChangeListener { _, isChecked ->
            switchAllOnLayout.visibility = if (isChecked) {
                LinearLayout.VISIBLE
            } else LinearLayout.GONE
            switchService.isChecked = true
            switchLetter.isChecked = true
            switchRemind.isChecked = true

            updateAlarmInFirestore(isChecked)
        }

    }


    private fun getAlarmStatusFromFirestore(callback: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userDocumentRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)

            userDocumentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val isChecked = documentSnapshot.getBoolean("alarm") ?: false
                        callback(isChecked)
                    } else {
                        callback(false) // 문서가 없으면 기본값 false로 설정
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Alarm 상태 가져오기 실패: ", e)
                    callback(false)
                }
        }
    }

    private fun updateAlarmInFirestore(isChecked: Boolean) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userDocumentRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)

            // Firestore에 alarm 값을 업데이트
            val alarmData = mapOf("alarm" to isChecked)
            val alarmTime = mapOf("alarmTime" to String)

            userDocumentRef.update(alarmData)
                .addOnSuccessListener {
                    Log.d("FirestoreUpdate", "Alarm 값이 성공적으로 업데이트되었습니다: $isChecked")

                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Alarm 값 업데이트 실패: ", e)
                }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되었을 때의 처리
                Log.d("selectedTime", "Notification permission granted")
            } else {
                // 권한이 거부되었을 때의 처리
                Log.d("selectedTime", "Notification permission denied")
            }
        }
    }
}
