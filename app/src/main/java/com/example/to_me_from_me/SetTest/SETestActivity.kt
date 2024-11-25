package com.example.to_me_from_me.SetTest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.to_me_from_me.R

class SETestActivity : AppCompatActivity() {

    private val originalQuestions  = listOf(
        "나는 다른 사람만큼 가치 있는 사람이야.",
        "나는 내가 좋은 성품을 가졌다고 생각해.",
        "대체로 나는 내가 실패했다고 느끼지 않아.",
        "나는 다른 사람들과 일을 잘 할 수 있어.",
        "나는 자랑할만한 게 많아.",
        "나는 내 자신에 대해 긍정적인 태도를 지녀.",
        "나는 내 자신에 대해 대체로 만족해.",
        "나는 스스로를 존중해.",
        "나는 내가 쓸모없다고 느끼지 않아.",
        "나는 내가 아무 능력 없는 사람이라 생각하지 않아."
    )

    // 랜덤 순서로 섞인 질문 리스트
    private var questions = originalQuestions.shuffled()

    // 현재 질문 인덱스
    private var currentQuestionIndex = 0
    // 사용자가 선택한 값의 합계
    private var totalScore = 0

    private lateinit var questiontv: TextView
    private lateinit var qNumTextView: TextView
    private lateinit var sAgreebtn: Button
    private lateinit var agreebtn: Button
    private lateinit var disagreebtn: Button
    private lateinit var sDisagreebtn: Button
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setest)

        // 초기화
        questiontv = findViewById(R.id.q_text)
        qNumTextView = findViewById(R.id.q_num)
        sAgreebtn = findViewById(R.id.s_agree)
        agreebtn = findViewById(R.id.agree)
        disagreebtn = findViewById(R.id.disagree)
        sDisagreebtn = findViewById(R.id.s_disagree)
        backButton = findViewById(R.id.back_iv)
        val saveButton = findViewById<ImageView>(R.id.save_iv)

        // 질문을 설정
        updateQuestion()

        // 각 버튼 클릭 시 색상 변경 및 다음 질문으로 이동
        sAgreebtn.setOnClickListener {
            totalScore += 4
            sAgreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
            sAgreebtn.setTextColor(ContextCompat.getColor(this, R.color.white))
            Handler(Looper.getMainLooper()).postDelayed({
                moveToNextQuestion()
            }, 300)
        }

        agreebtn.setOnClickListener {
            totalScore += 3
            agreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
            agreebtn.setTextColor(ContextCompat.getColor(this, R.color.white))
            Handler(Looper.getMainLooper()).postDelayed({
                moveToNextQuestion()
            }, 300)
        }

        disagreebtn.setOnClickListener {
            totalScore += 2
            disagreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
            disagreebtn.setTextColor(ContextCompat.getColor(this, R.color.white))
            Handler(Looper.getMainLooper()).postDelayed({
                moveToNextQuestion()
            }, 300)
        }

        sDisagreebtn.setOnClickListener {
            totalScore += 1
            sDisagreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
            sDisagreebtn.setTextColor(ContextCompat.getColor(this, R.color.white))
            Handler(Looper.getMainLooper()).postDelayed({
                moveToNextQuestion()
            }, 300)
        }

        // 뒤로가기 버튼 클릭 리스너 설정
        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            // test_dialog 다이얼프레그먼트화면 보여주기
            val dialog = TestquitDialogFragment()
            dialog.show(supportFragmentManager, "TestDialogFragment")
        }
    }

    private fun moveToNextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            updateQuestion()
        } else {
            showTotalScore()
        }
    }

    private fun updateQuestion() {
        questiontv.text = questions[currentQuestionIndex]
        qNumTextView.text = "${currentQuestionIndex + 1}"
        resetButtonBackgrounds() // 버튼 색상 초기화
        resetButtonText() // 버튼 텍스트 색상 초기화
    }

    private fun resetButtonText() {
        sAgreebtn.setTextColor(ContextCompat.getColor(this, R.color.Gray3))
        agreebtn.setTextColor(ContextCompat.getColor(this, R.color.Gray3))
        disagreebtn.setTextColor(ContextCompat.getColor(this, R.color.Gray3))
        sDisagreebtn.setTextColor(ContextCompat.getColor(this, R.color.Gray3))
    }

    private fun resetButtonBackgrounds() {
        sAgreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_white)
        agreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_white)
        disagreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_white)
        sDisagreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_white)
    }

    private fun showTotalScore() {
        val intent = Intent(this, SETestFinActivity::class.java)
        intent.putExtra("totalScore", totalScore)
        startActivity(intent)
    }
}

