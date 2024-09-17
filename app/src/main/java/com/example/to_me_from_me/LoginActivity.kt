package com.example.to_me_from_me

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.to_me_from_me.Signup.SignupEmailActivity
import com.example.to_me_from_me.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val providerFactory = PlayIntegrityAppCheckProviderFactory.getInstance()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(providerFactory)

        auth = Firebase.auth


        binding.loginBtn.setOnClickListener {
            val email = binding.loginInputIdEt.text.toString()
            val password = binding.loginInputPwdEt.text.toString()
            signIn(email, password)
            performLogin()
        }


        binding.signupTv.setOnClickListener {
            startActivity(Intent(this, SignupEmailActivity::class.java))
            finish() // 현재 화면 종료
        }

    }

    private fun signIn(email: String, password: String) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //로그인에 성공한 경우 메인 화면으로 이동
                    Toast.makeText(this, "로그인성공" ,Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    finish()

                } else {
                    //로그인에 실패한 경우 Toast 메시지로 에러를 띄워준다
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    Log.e("error","$task.exception?.message")

                }
            }
    }

    private fun performLogin() {
        // 아이디 비번 확인
    }
}