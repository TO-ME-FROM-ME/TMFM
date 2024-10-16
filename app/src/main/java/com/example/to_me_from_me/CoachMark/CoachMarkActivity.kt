package com.example.to_me_from_me.CoachMark



import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.example.to_me_from_me.SetTest.TestquitDialogFragment

class CoachMarkActivity : AppCompatActivity()  {

    private lateinit var coachImageView: ImageView
    private lateinit var stepTv: TextView
    private lateinit var nextButton: ImageView // 다음 버튼
    private lateinit var backButton: ImageView // 뒤로 버튼
    private var currentStep = 1 // 현재 단계를 저장하는 변수
    private val coachImages = arrayOf( // 이미지를 배열로 저장
        R.drawable.coach1,
        R.drawable.coach2,
        R.drawable.coach3,
        R.drawable.coach4,
        R.drawable.coach5,
        R.drawable.coach6,
        R.drawable.coach7,
        R.drawable.coach8,
        R.drawable.coach9,
        R.drawable.coach10
    )


    private lateinit var coach3Iv: ImageView
    private lateinit var coach3Iv2: ImageView

    private lateinit var coach5Iv: ImageView
    private lateinit var coach5Iv2: ImageView

    private lateinit var coach7Iv: ImageView
    private lateinit var coach7Iv2: ImageView

    private lateinit var coach10Iv: ImageView
    private lateinit var coach10Iv2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coachmark)

        val cancelButton = findViewById<ImageView>(R.id.cancel_iv)
        cancelButton.setOnClickListener {
            val dialog = CoachMarkDialogFragment()
            dialog.show(supportFragmentManager, "CoachMarkDialogFragment")
        }

        coachImageView = findViewById(R.id.coach_iv)
        stepTv = findViewById(R.id.step_tv)
        nextButton = findViewById(R.id.next_iv) // '다음' 버튼의 ID 확인
        backButton = findViewById(R.id.back_iv) // '뒤로' 버튼의 ID 확인

        coach3Iv = findViewById(R.id.coach3_iv)
        coach3Iv2 = findViewById(R.id.coach3_iv2)

        coach5Iv = findViewById(R.id.coach5_iv)
        coach5Iv2 = findViewById(R.id.coach5_iv2)

        coach7Iv = findViewById(R.id.coach7_iv)
        coach7Iv2 = findViewById(R.id.coach7_iv2)

        coach10Iv = findViewById(R.id.coach10_iv)
        coach10Iv2 = findViewById(R.id.coach10_iv2)

        val cancelIv = findViewById<ImageView>(R.id.cancel_iv)
        cancelIv.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // 현재 화면 종료
        }

        nextButton.setOnClickListener {
            changeCoachMark()
        }


        backButton.setOnClickListener {
            goBackStep()
        }

        // 초기 UI 설정
        updateUI()


    }

    private fun goBackStep() {
        if (currentStep > 1) {
            currentStep-- // 단계 감소
            // coach_iv의 이미지 변경
            coachImageView.setImageResource(coachImages[currentStep - 1])

            // TextView의 텍스트 변경
            stepTv.text = currentStep.toString()

            // UI 업데이트
            updateUI()
        } else {
            Log.d("coach","코치마크 첫 화면")
        }
    }

    private fun changeCoachMark() {
        if (currentStep < coachImages.size) {
            currentStep++
            // coach_iv의 이미지 변경
            coachImageView.setImageResource(coachImages[currentStep - 1]) // 현재 단계에 해당하는 이미지로 변경

            // TextView의 텍스트 변경
            stepTv.text = currentStep.toString() // 텍스트를 현재 단계로 변경

            // UI 업데이트
            updateUI()
        } else {
            Log.d("coach","코치마크 마지막 화면")
        }
    }

    private fun updateUI() {
        when (currentStep) {
            1 -> {
                backButton.visibility = View.GONE
                nextButton.visibility = View.VISIBLE
            }
            10 -> {
                backButton.visibility = View.VISIBLE
                nextButton.visibility = View.GONE
                coach10Iv.visibility = View.VISIBLE
                coach10Iv2.visibility = View.VISIBLE

                coach10Iv.setOnClickListener {
                    startActivity(Intent(this, WriteLetterActivity::class.java))
                    finish() // 현재 화면 종료
                }

            }
            3 -> {
                backButton.visibility = View.VISIBLE
                nextButton.visibility = View.VISIBLE
                coach3Iv.visibility = View.VISIBLE
                coach3Iv2.visibility = View.VISIBLE

                coach3Iv.setOnClickListener {
                    changeCoachMark()
                }
            }

            5 -> {
                backButton.visibility = View.VISIBLE
                nextButton.visibility = View.VISIBLE
                coach5Iv.visibility = View.VISIBLE
                coach5Iv2.visibility = View.VISIBLE

                coach5Iv.setOnClickListener {
                    changeCoachMark()
                }
            }
            7 -> {
                backButton.visibility = View.VISIBLE
                nextButton.visibility = View.VISIBLE
                coach7Iv.visibility = View.VISIBLE
                coach7Iv2.visibility = View.VISIBLE

                coach7Iv.setOnClickListener {
                    changeCoachMark()
                }
            }

            else -> {
                backButton.visibility = View.VISIBLE
                nextButton.visibility = View.VISIBLE

                coach3Iv.visibility = View.GONE
                coach3Iv2.visibility = View.GONE

                coach5Iv.visibility = View.GONE
                coach5Iv2.visibility = View.GONE

                coach7Iv.visibility = View.GONE
                coach7Iv2.visibility = View.GONE

                coach10Iv.visibility = View.GONE
                coach10Iv2.visibility = View.GONE

            }
        }

    }


}