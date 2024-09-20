package com.example.to_me_from_me.Mypage

import android.Manifest
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
import com.example.to_me_from_me.AlarmNotificationService
import com.example.to_me_from_me.AlarmTimeDialogFragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.example.to_me_from_me.SharedViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class AlarmActivity : AppCompatActivity() {
    lateinit var timeSetTextview: TextView
    private lateinit var viewModel: SharedViewModel

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        checkNotificationPermission() // 권한 체크 호출

        val backButton: ImageView = findViewById(R.id.back_iv)
        val alarmTime = findViewById<ImageView>(R.id.time_set_iv)
        val switchAll: Switch = findViewById(R.id.switch_all)
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

        viewModel.selectedData.observe(this, Observer { data ->
            Log.d("selectedTime", "AlarmActivity: $data")
            startAlarmService(convertTimeToString(data))
            Log.d("selectedTime", "timeInMillis: $data")
        })


        switchAll.setOnCheckedChangeListener { _, isChecked ->
            switchAllOnLayout.visibility = if (isChecked) LinearLayout.VISIBLE else LinearLayout.GONE
        }

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


    private fun convertTimeToString(time: String?): String {
        return time ?: "00:00" // 기본값 설정
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
