package com.example.to_me_from_me.Signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.to_me_from_me.R

class SignupPwdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_pwd)


        val pwdEditText = findViewById<EditText>(R.id.pwd_write_et)
        val pwdReEditText = findViewById<EditText>(R.id.pwd_re_write_et)
        val nextBtn = findViewById<Button>(R.id.next_btn)

        val pwdEye = findViewById<ImageView>(R.id.pwd_eye_iv)
        val pwdReEye = findViewById<ImageView>(R.id.pwd_re_eye_iv)
        val font = ResourcesCompat.getFont(this, R.font.font_gangwon)
        var isPasswordVisible = false

        val charCountTextView = findViewById<TextView>(R.id.char_count_tv)
        val charReCountTextView = findViewById<TextView>(R.id.char_re_count_tv)
        val backButton = findViewById<ImageView>(R.id.back_iv)

        val toastLayout = LayoutInflater.from(this).inflate(R.layout.toast_pwd, null, false)
        val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

        val toastLayout2 = LayoutInflater.from(this).inflate(R.layout.toast_pwd12, null, false)
        val toastTv2 = toastLayout2.findViewById<TextView>(R.id.toast_tv)

        val toastLayout3 = LayoutInflater.from(this).inflate(R.layout.toast_pwd_no, null, false)
        val toastTv3 = toastLayout3.findViewById<TextView>(R.id.toast_tv)

        val mainColor = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
        val defaultColor = ContextCompat.getDrawable(this, R.drawable.solid_no_gray)

        val email = intent.getStringExtra("email")

        charCountTextView.filters = arrayOf(InputFilter.LengthFilter(12))
        charReCountTextView.filters = arrayOf(InputFilter.LengthFilter(12))

        // 비밀번호 유효성 검사 메서드
        fun validatePasswords() {
            val pwd = pwdEditText.text.toString()
            val pwdRe = pwdReEditText.text.toString()

            if (pwd.length in 8..12 && pwd == pwdRe) {
                // 비밀번호가 유효하고 일치할 때 버튼 색상 변경 및 활성화
                nextBtn.background = mainColor
            } else {
                // 비밀번호가 유효하지 않거나 일치하지 않을 때 버튼 비활성화
                nextBtn.background = defaultColor
            }
        }

        nextBtn.setOnClickListener {
            val pwd = pwdEditText.text.toString()
            val pwdRe = pwdReEditText.text.toString()

            when {
                pwd.length < 8 -> {
                    toastTv.text = "8글자 이상 작성해줘!"
                    showToast(toastLayout, pwdEditText, 700)
                }
                pwd.length > 12 -> {
                    toastTv2.text = "12글자 이하로 작성해줘!"
                    showToast(toastLayout2, pwdEditText, 700)
                }
                pwd != pwdRe -> {
                    toastTv3.text = "비밀번호가 일치하지 않아!"
                    showToast(toastLayout3, pwdReEditText, 700)
                }
                else -> {
                    // 비밀번호가 유효하고 일치할 때 다음 Activity로 이동
                    val intent = Intent(this, SignupNicknameActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("pwd", pwd)
                    startActivity(intent)
                }
            }


        }


        // pwdEye 클릭 시 비밀번호 가시성 토글
        pwdEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // 비밀번호 보이도록 설정
                pwdEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                pwdEye.setImageResource(R.drawable.ic_eye_on)
            } else {
                // 비밀번호 숨기도록 설정
                pwdEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                pwdEye.setImageResource(R.drawable.ic_eye_off)
            }
            pwdEditText.typeface = font
            // 커서 위치 유지
            pwdEditText.setSelection(pwdEditText.text.length)
        }


        // pwdEye 클릭 시 비밀번호 가시성 토글
        pwdReEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // 비밀번호 보이도록 설정
                pwdReEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                pwdReEye.setImageResource(R.drawable.ic_eye_on)
            } else {
                // 비밀번호 숨기도록 설정
                pwdReEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                pwdReEye.setImageResource(R.drawable.ic_eye_off)
            }
            pwdReEditText.typeface = font
            // 커서 위치 유지
            pwdReEditText.setSelection(pwdReEditText.text.length)
        }



        // 실시간 글자 수 확인
        pwdEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때마다 글자 수 업데이트
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount"
                validatePasswords()
            }

            override fun afterTextChanged(s: Editable?) {1
            }
        })
        pwdReEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때마다 글자 수 업데이트
                val charCount = s?.length ?: 0
                charReCountTextView.text = "$charCount"
                validatePasswords()
            }

            override fun afterTextChanged(s: Editable?) {
                // 필요 시 구현 가능
            }
        })

        // 이전 Activity로 이동
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun showToast(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(this)
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)

        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val yOffset = location[1] -(-10) -layout.measuredHeight
        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.view = layout

        toast.show()
    }

}