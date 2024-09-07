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
import androidx.core.widget.addTextChangedListener
import com.example.to_me_from_me.R

class SignupNicknameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_nickname)


        val nicknameEditText = findViewById<EditText>(R.id.nickname_et)
        val signupBtn = findViewById<Button>(R.id.signup_btn)

        val email = intent.getStringExtra("email")
        val pwd = intent.getStringExtra("pwd")
        val charCountTextView = findViewById<TextView>(R.id.char_count_tv)
        val backButton = findViewById<ImageView>(R.id.back_iv)


        // 닉네임 최대 6글자
        charCountTextView.filters = arrayOf(InputFilter.LengthFilter(6))


        signupBtn.setOnClickListener {
            val nickname = nicknameEditText.text.toString()

            if(nickname.length <2 ){
                Toast.makeText(this, "2글자 이상 작성해줘!", Toast.LENGTH_SHORT).show()
            } else if(nickname.length > 6){
                Toast.makeText(this, "6글자 이하로 작성해줘!", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this, SignupFinishActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("pwd", pwd)
                intent.putExtra("nickname", nickname)
                startActivity(intent)
            }
        }


        nicknameEditText.addTextChangedListener(object : TextWatcher {
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

        // 이전 Activity로 이동
        backButton.setOnClickListener {
            finish()
        }


    }
}