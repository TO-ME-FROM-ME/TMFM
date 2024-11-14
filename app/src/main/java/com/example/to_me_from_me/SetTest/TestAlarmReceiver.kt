package com.example.to_me_from_me.SetTest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TestAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("알람", "AlarmReceiver 실행")

        // Intent에서 선택한 시간 읽기
        val hour = intent.getIntExtra("selected_hour", -1)
        val minute = intent.getIntExtra("selected_minute", -1)

        // 시간 값을 포맷팅
        val period = if (hour < 12) "오전" else "오후"
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        val formattedTime = String.format("%s %02d시 %02d분", period, displayHour, minute)

        showNotification(context, formattedTime)

    }

    private fun showNotification(context: Context, formattedTime: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Android 8.0 이상에서는 NotificationChannel 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "ALARM_CHANNEL"
            val channelName = "알람 채널"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.description = "알람을 위한 채널"
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        Log.d("알람", "pendingIntent : $pendingIntent")

        // Notification 생성
        val notificationBuilder = NotificationCompat.Builder(context, "ALARM_CHANNEL")
            .setSmallIcon(R.drawable.ic_letter_alram)
            .setContentTitle("TO ME FROM ME")
            .setContentText("테스트 알람")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Notification 표시
        notificationManager.notify(1, notificationBuilder.build())
    }
}

