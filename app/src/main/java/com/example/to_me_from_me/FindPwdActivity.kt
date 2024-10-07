package com.example.to_me_from_me

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class FindPwdActivity : AppCompatActivity()  {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findpwd)

        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()

        // UI 요소들 초기화
        emailEditText = findViewById(R.id.email_et) // 이메일 입력 필드
        sendButton = findViewById(R.id.send_btn) // 비밀번호 재설정 링크 전송 버튼


        val backButton = findViewById<ImageView>(R.id.back_iv)
        backButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // 현재 화면 종료
        }


        sendButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                // Firebase에 비밀번호 재설정 이메일 요청
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 성공적으로 이메일을 보냈을 때
                            Toast.makeText(this, "비밀번호 재설정 이메일을 전송했습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            // 실패했을 때 에러 메시지 표시
                            Toast.makeText(this, "이메일 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // 이메일을 입력하지 않았을 때
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


    }

}