package com.example.to_me_from_me.Mypage

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R

class AlarmNotificationService : NotificationListenerService() {

    private val CHANNEL_ID = "alarm_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("selectedTime", "service started")
        super.onStartCommand(intent, flags, startId)
        val time = intent?.getStringExtra("alarm_time") ?: "00:00" // 기본값 설정
        sendNotification(time)
        return START_STICKY
    }



    private fun sendNotification(time: String) {
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        // PendingIntent 수정
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("편지 쓸 시간이야!")
            .setContentText("설정한 시간: $time")
            .setSmallIcon(R.drawable.ic_alarm) // 적절한 아이콘으로 변경
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        Log.d("selectedTime", "sendNotification : $time")

        // 오류 아니라서 무시해도 됩니다~
        startForeground(1, notification)
    }

}
