package com.example.to_me_from_me.Mailbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.to_me_from_me.R
import java.util.Date

class DetailMailBoxActivity : AppCompatActivity() {
    private var selectedDate: Date? = null // Date 타입의 변수 선언

    private lateinit var titleTextView: TextView // 텍스트뷰 초기화
    private lateinit var detailFragment: DetailMailBoxFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mailbox)

        titleTextView = findViewById(R.id.send_title_tv) // TextView의 ID로 초기화

        // Intent에서 데이터 수신
        val sendValue = intent.getStringExtra("letter")
        val selectedDateMillis = intent.getLongExtra("selectedDate", -1)
        if (selectedDateMillis != -1L) {
            selectedDate = Date(selectedDateMillis) // Long을 Date로 변환
        }

        // 인텐트에서 데이터 받기
         val reservedate = intent.getStringExtra("reservedate")


        // 수신한 값에 따라 TextView 업데이트 및 Fragment 설정
        when (sendValue) {
            "send" -> {
                titleTextView.text = "흘러간 편지"
                detailFragment = DetailMailBoxFragment()
                val bundle = Bundle()
                // Date를 Long으로 변환하여 저장
                selectedDate?.let {
                    bundle.putLong("selectedDate", it.time)
                    bundle.putString("letter", "send")
                }
                detailFragment.arguments = bundle // Fragment에 인자 설정

            }
            "random" -> {
                titleTextView.text = "우연한 편지"
                Log.d("sendValue", "$sendValue")
                detailFragment = DetailMailBoxFragment()
                val bundle = Bundle()

                Log.d("랜덤확인", "selectedDate: $selectedDate")
                selectedDate?.let {
                    bundle.putLong("selectedDate", it.time)
                }

                bundle.putString("situation", intent.getStringExtra("situation"))
                bundle.putString("emoji", intent.getStringExtra("emoji"))
                bundle.putString("ad1", intent.getStringExtra("ad1"))
                bundle.putString("ad2", intent.getStringExtra("ad2"))
                bundle.putString("letter", "randomMail")
                detailFragment.arguments = bundle

                Log.d("랜덤확인", "detailFragment.arguments : ${detailFragment.arguments}")
            }
            "receive" -> {
                titleTextView.text = "흘러온 편지"
                Log.d("sendValue", "$sendValue")
                detailFragment = DetailMailBoxFragment()
                val bundle = Bundle()
                bundle.putString("letter", "receive")
                bundle.putString("reservedate",reservedate)
                detailFragment.arguments = bundle
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

