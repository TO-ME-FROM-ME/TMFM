package com.example.to_me_from_me.Signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

class SignupEmailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_email)

        auth = FirebaseAuth.getInstance()

        val emailBtn = findViewById<Button>(R.id.email_code_btn)
        val emailEditText = findViewById<EditText>(R.id.email_et)

        val backButton = findViewById<ImageView>(R.id.back_iv)
        backButton.setOnClickListener {
            // 이전 Activity로 이동
            finish()
        }


        emailBtn.setOnClickListener {
            val email = emailEditText.text.toString()

            // 이메일 유효성 확인
            if(email.isEmpty()){
                Toast.makeText(this, "이메일을 입력해줘.", Toast.LENGTH_SHORT).show()
            }else if(!isValidEmail(email)){
                Toast.makeText(this, "이메일 형식이 잘못됐어!", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, SignupPwdActivity::class.java)
                intent.putExtra("email",email)
                startActivity(intent)
            }
        }


    }

    // 이메일 유효성 검사를 위한 정규식
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(Regex(emailPattern))
    }


}
