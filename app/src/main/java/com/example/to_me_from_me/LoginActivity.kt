package com.example.to_me_from_me

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.to_me_from_me.Signup.SignupEmailActivity
import com.example.to_me_from_me.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val providerFactory = PlayIntegrityAppCheckProviderFactory.getInstance()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(providerFactory)

        mAuth = FirebaseAuth.getInstance()


        binding.loginBtn.setOnClickListener {
            val email = binding.loginInputIdEt.text.toString()
            val password = binding.loginInputPwdEt.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signIn(email, password)

            performLogin()
        }


        binding.signupTv.setOnClickListener {
            startActivity(Intent(this, SignupEmailActivity::class.java))
            finish() // 현재 화면 종료
        }

    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = mAuth.currentUser
                    Log.d("LoginActivity", "로그인 성공: ${user?.email}")
                } else {
                    Log.e("LoginActivity", "로그인 실패: ${task.exception?.message}")
                }
            }
    }

    private fun performLogin() {
        // 아이디 비번 확인
    }
}