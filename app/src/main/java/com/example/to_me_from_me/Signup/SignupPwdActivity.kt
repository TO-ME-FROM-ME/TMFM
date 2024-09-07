package com.example.to_me_from_me.Signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.to_me_from_me.R

class SignupPwdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_pwd)


        val pwdEditText = findViewById<EditText>(R.id.pwd_write_et)
        val pwdReEditText = findViewById<EditText>(R.id.pwd_re_write_et)
        val nextBtn = findViewById<Button>(R.id.next_btn)

        val charCountTextView = findViewById<TextView>(R.id.char_count_tv)
        val charReCountTextView = findViewById<TextView>(R.id.char_re_count_tv)
        val backButton = findViewById<ImageView>(R.id.back_iv)


        val email = intent.getStringExtra("email")

        charCountTextView.filters = arrayOf(InputFilter.LengthFilter(12))
        charReCountTextView.filters = arrayOf(InputFilter.LengthFilter(12))


        // 다음 버튼 클릭 시 비밀번호 유효성 및 일치 여부 검사
        nextBtn.setOnClickListener {
            val pwd = pwdEditText.text.toString()
            val pwdRe = pwdReEditText.text.toString()

            when {
                pwd.length < 8 -> {
                    Toast.makeText(this, "8글자 이상 작성해줘!", Toast.LENGTH_SHORT).show()
                }

                pwd.length > 12 -> {
                    Toast.makeText(this, "12글자 이하로 작성해줘!", Toast.LENGTH_SHORT).show()
                }

                pwd != pwdRe -> {
                    // 비밀번호와 재입력된 비밀번호가 일치하지 않을 때
                    Toast.makeText(this, "비밀번호가 일치하지 않아!.", Toast.LENGTH_SHORT).show()
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




        // 실시간 글자 수 확인
        pwdEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때마다 글자 수 업데이트
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount"
            }

            override fun afterTextChanged(s: Editable?) {
                // 필요 시 구현 가능
            }
        })
        pwdReEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때마다 글자 수 업데이트
                val charCount = s?.length ?: 0
                charReCountTextView.text = "$charCount"
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
}