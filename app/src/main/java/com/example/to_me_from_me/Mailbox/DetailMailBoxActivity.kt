package com.example.to_me_from_me.Mailbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.to_me_from_me.R

class DetailMailBoxActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView // 텍스트뷰 초기화
    private lateinit var detailFragment: DetailMailBoxFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mailbox)

        titleTextView = findViewById(R.id.send_title_tv) // TextView의 ID로 초기화

        // Intent에서 데이터 수신
        val sendValue = intent.getStringExtra("letter")
        detailFragment = DetailMailBoxFragment() // Fragment 인스턴스 생성

        // 수신한 값에 따라 TextView 업데이트 및 Fragment 설정
        when (sendValue) {
            "send" -> {
                titleTextView.text = "보낸 편지"
                Log.d("sendValue", "$sendValue")
            }
            "random" -> {
                titleTextView.text = "랜덤 편지"
                Log.d("sendValue", "$sendValue")
            }
            else -> {
                titleTextView.text = "받은 편지"
                Log.d("sendValue", "$sendValue")
            }
        }

        // 초기 Fragment 설정
        if (::detailFragment.isInitialized) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment) // detailFragment를 사용
                .commit()
        }

        val backButton: ImageView = findViewById(R.id.back_iv)
        backButton.setOnClickListener {
            supportFragmentManager.popBackStack()
            val intent = Intent(this, MailboxActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

