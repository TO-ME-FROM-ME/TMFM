package com.example.to_me_from_me

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.to_me_from_me.Signup.SignupEmailActivity
import com.example.to_me_from_me.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loginBtn.setOnClickListener {
            performLogin()
        }


        binding.signupTv.setOnClickListener {
            startActivity(Intent(this, SignupEmailActivity::class.java))
            finish() // 현재 화면 종료
        }

    }

    private fun performLogin() {
        // 아이디 비번 확인
    }
}