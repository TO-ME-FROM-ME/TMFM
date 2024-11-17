package com.example.to_me_from_me

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.example.to_me_from_me.R

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        // MediaPlayer 초기화
        mediaPlayer = MediaPlayer.create(this, R.raw.service_bgm) // 음악 파일을 바꿔주세요.
        if (mediaPlayer == null) {
            Log.e("MusicService", "MediaPlayer 생성 실패")
            stopSelf()  // MediaPlayer 초기화 실패 시 서비스 중지
            return
        }
        mediaPlayer?.isLooping = true  // 음악을 반복 재생하도록 설정
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // MediaPlayer가 준비되어 있다면 시작
        mediaPlayer?.start()
        Log.d("MusicService", "BGM 시작됨")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop() // 음악 정지
        mediaPlayer?.release() // MediaPlayer 자원 해제
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
