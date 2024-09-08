package com.example.to_me_from_me

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SETestActivity : AppCompatActivity() {

    private val originalQuestions  = listOf(
        "나는 다른 사람만큼 가치 있는 사람이야.",
        "나는 내가 좋은 성품을 가졌다고 생각해.",
        "대체로 나는 내가 실패했다고 느껴.",
        "나는 다른 사람들과 일을 잘 할 수 있어.",
        "나는 내가 자랑할만한 게 별로 없어.",
        "나는 내 자신에 대해 긍정적인 태도를 지녀.",
        "나는 내 자신에 대해 대체로 만족해.",
        "나는 스스로를 조금 더 존중했으면 좋겠어.",
        "가끔 나는 내가 쓸모없다고 느껴.",
        "때로 나는 아무 능력 없는 사람이라 생각해."
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
        backButton = findViewById(R.id.back_iv) // 이 줄을 onCreate 안으로 이동

        // 질문을 설정
        updateQuestion()

        val buttonClickListener = View.OnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                updateQuestion()
            } else {
                currentQuestionIndex++
                showTotalScore()

            }
        }

        sAgreebtn.setOnClickListener {
            totalScore += 4
            buttonClickListener.onClick(it)
            resetButtonBackgrounds()
            sAgreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
        }

        agreebtn.setOnClickListener {
            totalScore += 3
            buttonClickListener.onClick(it)
            resetButtonBackgrounds()
            agreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
        }

        disagreebtn.setOnClickListener {
            totalScore += 2
            buttonClickListener.onClick(it)
            resetButtonBackgrounds()
            disagreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
        }

        sDisagreebtn.setOnClickListener {
            totalScore += 1
            buttonClickListener.onClick(it)
            resetButtonBackgrounds()
            sDisagreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
        }

        // 뒤로가기 버튼 클릭 리스너 설정
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun showTotalScore() {
        val intent = Intent(this, SETestFinActivity::class.java)
        intent.putExtra("totalScore", totalScore)
        startActivity(intent)
    }

    private fun updateQuestion() {
        questiontv.text = questions[currentQuestionIndex]
        qNumTextView.text = "${currentQuestionIndex + 1}"
        resetButtonBackgrounds()    //버튼 색상 초기화
    }


    private fun resetButtonBackgrounds() {
        sAgreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_gray)
        agreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_gray)
        disagreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_gray)
        sDisagreebtn.background = ContextCompat.getDrawable(this, R.drawable.solid_no_gray)
    }
}
