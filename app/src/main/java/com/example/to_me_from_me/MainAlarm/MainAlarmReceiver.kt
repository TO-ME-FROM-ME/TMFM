package com.example.to_me_from_me.MainAlarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R

//class MainAlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        createNotification(context)
//    }
//
//    private fun createNotification(message: String) {
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // 알림 채널 생성 (API 26 이상에서 필요)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "your_channel_id"
//            val channelName = "Channel Name"
//            val channelDescription = "Channel Description"
//            val channelImportance = NotificationManager.IMPORTANCE_HIGH
//
//            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
//                description = channelDescription
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notificationBuilder = NotificationCompat.Builder(this, "your_channel_id")
//            .setSmallIcon(R.drawable.ic_letter_alram) // 알림 아이콘
//            .setContentTitle("알림")
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//
//        notificationManager.notify(1, notificationBuilder.build())
//    }
//
//
//}