package com.example.to_me_from_me

import android.content.Context
import android.content.Intent

fun startMusicService(context: Context) {
    val intent = Intent(context, MusicService::class.java)
    context.startService(intent)
}

fun stopMusicService(context: Context) {
    val intent = Intent(context, MusicService::class.java)
    context.stopService(intent)
}