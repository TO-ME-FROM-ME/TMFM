package com.example.to_me_from_me.RandomLetter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.example.to_me_from_me.databinding.ActivityRandomletterBinding

class RandomLetterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRandomletterBinding


    // 이미지를 저장할 리스트
    private val imageList = listOf(
        R.drawable.home_1,
        R.drawable.home_2,
        R.drawable.home_3,
        R.drawable.home_4,
        R.drawable.home_5
    )
    private var currentIndex = 0

    // Handler 및 Runnable 추가
    private val handler = Handler(Looper.getMainLooper())
    private val imageSwitcher = object : Runnable {
        override fun run() {
            // 이미 리스트의 끝에 도달한 경우 전환을 중지
            if (currentIndex < imageList.size - 1) {
                // 다음 이미지로 변경
                currentIndex++
                binding.imgIv.setImageResource(imageList[currentIndex])
                updateSkipButtonVisibility()
                //letterClick()
                // 1초 후에 다시 실행
                handler.postDelayed(this, 1000)
            } else {
                // 끝에 도달하면 작업 중지
                handler.removeCallbacks(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomletterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 이미지 설정
        binding.imgIv.setImageResource(imageList[currentIndex])
        updateSkipButtonVisibility()

        // 자동으로 1초마다 이미지 변경 시작
        handler.postDelayed(imageSwitcher, 1000)

        val selectedEmoji = intent.getStringExtra("selectedEmoji")


        // skipIv 클릭 시 이미지 변경
        binding.skipIv.setOnClickListener {
            // MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("selectedEmoji", selectedEmoji) // 여기에 selectedEmoji 추가
                putExtra("showDialog", true)
            }
            startActivity(intent)
            finish() // 현재 Activity 종료
            Log.d("랜덤", "$selectedEmoji")
        }


        //
        binding.letterIv.setOnClickListener {
            // MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("selectedEmoji", selectedEmoji) // 여기에 selectedEmoji 추가
                putExtra("showDialog", true)
            }
            startActivity(intent)
            finish() // 현재 Activity 종료
        }



    }

//    private fun letterClick() {
//        if (currentIndex == 4) {
//            binding.letterIv.visibility = ImageView.VISIBLE
//            binding.skipIv.visibility = ImageView.GONE
//            binding.letterIv.setOnClickListener {
//                Log.d("letterIv", "letterIv clicked")
//
//            }
//        } else {
//            binding.letterIv.visibility = ImageView.GONE
//        }
//
//    }

    private fun updateSkipButtonVisibility() {
        // currentIndex가 0일 때 skipIv 숨기기
        binding.skipIv.visibility = if (currentIndex == 0) {
            ImageView.GONE
        } else {
            ImageView.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity 종료 시 Handler의 작업을 중지
        handler.removeCallbacks(imageSwitcher)
    }
}
