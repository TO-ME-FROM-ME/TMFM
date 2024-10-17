package com.example.to_me_from_me.Mypage

import android.Manifest
import android.app.Activity
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
    private lateinit var viewModel: SharedViewModel
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 2001
        private const val REQUEST_TIME_PICKER = 1001 // 시간 선택 요청 코드
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        checkNotificationPermission() // 권한 체크 호출

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

        timeSetTextview = findViewById<TextView>(R.id.time_set_tv)
        alarmTime.setOnClickListener {
            val dialogFragment = AlarmTimeDialogFragment()
            dialogFragment.show(supportFragmentManager, "AlarmTimeDialogFragment")
        }

        // ViewModel의 selectedTime을 관찰
        viewModel.selectedTime.observe(this, Observer { timeInMillis ->
            val formattedTime = convertMillisToTime(timeInMillis)
            timeSetTextview.text = formattedTime
            Log.d("selectedTime", "formattedTime: $formattedTime")
            startAlarmService(formattedTime)
        })



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

    private fun updateAlarmInFirestore(isChecked: Boolean) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userDocumentRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)

            // Firestore에 alarm 값을 업데이트
            val alarmData = mapOf("alarm" to isChecked)

            userDocumentRef.update(alarmData)
                .addOnSuccessListener {
                    Log.d("FirestoreUpdate", "Alarm 값이 성공적으로 업데이트되었습니다: $isChecked")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Alarm 값 업데이트 실패: ", e)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TIME_PICKER && resultCode == Activity.RESULT_OK) {
            data?.getLongExtra("selected_time", System.currentTimeMillis())?.let { timeInMillis ->
                // ViewModel을 통해 선택한 시간 저장
                viewModel.setSelectedTime(timeInMillis)
            }
        }
    }

    private fun convertMillisToTime(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("a hh시 mm분", Locale.getDefault())
        val date = Date(timeInMillis)
        return dateFormat.format(date)
    }
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
            }
        }
    }

    private fun startAlarmService(time: String) {
        val serviceIntent = Intent(this, AlarmNotificationService::class.java).apply {
            putExtra("alarm_time", time) // 알림 시간 추가
        }
        Log.d("selectedTime", "startAlarmService: $time")
        ContextCompat.startForegroundService(this, serviceIntent) // 포그라운드 서비스 시작
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
