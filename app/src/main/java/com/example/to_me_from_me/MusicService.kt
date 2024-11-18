package com.example.to_me_from_me

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.example.to_me_from_me.R

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition: Int = 0 // 현재 재생 위치 저장

    override fun onCreate() {
        super.onCreate()
        // MediaPlayer 초기화
        mediaPlayer = MediaPlayer.create(this, R.raw.service_bgm)
        mediaPlayer?.isLooping = true // 반복 재생
        Log.d("MusicService", "onCreate: MediaPlayer 초기화됨")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 이전에 저장된 위치로 이동
        val savedPosition = getSavedPosition()
        mediaPlayer?.seekTo(savedPosition)
        mediaPlayer?.start() // 음악 시작

        // WriteLetterActivity에서는 음악이 실행되지 않도록 조건 추가
        if (intent?.getStringExtra("activity_name") != "WriteLetterActivity") {
            mediaPlayer?.start()
        }

        // 현재 재생 위치와 저장된 위치 출력
        Log.d("MusicService", "onStartCommand: 음악 시작 - 현재 위치: ${mediaPlayer?.currentPosition}ms, 저장된 위치: $savedPosition ms")

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // 종료 시 현재 위치 저장
        mediaPlayer?.let {
            currentPosition = it.currentPosition
            savePosition(currentPosition)
            Log.d("MusicService", "onDestroy: 음악 정지 - 현재 위치: ${currentPosition}ms")
            // 여기서 stop() 호출을 하지 않음
            // it.stop() // 멈추지 않도록 수정
            it.release() // 필요시 자원 해제
        }
        mediaPlayer = null // MediaPlayer 해제
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun savePosition(position: Int) {
        val sharedPreferences = getSharedPreferences("music_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("music_position", position)
            apply()
        }
        Log.d("MusicService", "savePosition: 음악 위치 저장 - 위치: ${position}ms")
    }

    private fun getSavedPosition(): Int {
        val sharedPreferences = getSharedPreferences("music_prefs", MODE_PRIVATE)
        val savedPosition = sharedPreferences.getInt("music_position", 0) // 기본값 0
        Log.d("MusicService", "getSavedPosition: 저장된 위치 가져오기 - 위치: $savedPosition ms")
        return savedPosition
    }
}








