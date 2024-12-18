package com.example.to_me_from_me.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.IBinder
import android.app.Service
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.to_me_from_me.MainAlarm.MainAlarmActivity
import com.example.to_me_from_me.R
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Intent가 null인 경우 기본값을 반환
        if (intent == null) {
            return START_NOT_STICKY
        }

        val message = intent.getStringExtra("message") ?: return START_NOT_STICKY
        val reservedDateString = intent.getStringExtra("reservedDate")

        Log.d("main알람", "reservedDateString : $reservedDateString")

        // 현재 시간을 가져옵니다.
        val currentTime = System.currentTimeMillis()

        // 예약된 시간과 비교합니다.
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val reservedDate = dateFormat.parse(reservedDateString)

        if (reservedDate != null && currentTime >= reservedDate.time) {
            // 알림 생성
            createNotification(message)
        }

        return START_NOT_STICKY
    }

    private fun createNotification(message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 생성 (API 26 이상에서 필요)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "your_channel_id"
            val channelName = "Channel Name"
            val channelDescription = "Channel Description"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }


        // MainAlarmActivity로 이동하는 Intent 생성
        val intent = Intent(this, MainAlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // 알림을 통해 이동했음을 알리는 플래그 추가
        intent.putExtra("fromNotification", true)


        // PendingIntent 생성
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Android 12 이상에서 FLAG_IMMUTABLE 필요
        )


        val notificationBuilder = NotificationCompat.Builder(this, "your_channel_id")
            .setSmallIcon(R.drawable.ic_letter_alram) // 알림 아이콘
            .setContentTitle("편지가 도착했어!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(1, notificationBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

