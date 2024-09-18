package com.example.to_me_from_me.Signup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
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

        val emailCodeButton = findViewById<Button>(R.id.email_code_btn)

        val mainColor = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
        val defaultColor = ContextCompat.getDrawable(this, R.drawable.solid_no_gray)

        val toastLayout = LayoutInflater.from(this).inflate(R.layout.toast, null, false)
        val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)


        val backButton = findViewById<ImageView>(R.id.back_iv)
        backButton.setOnClickListener {
            // 이전 Activity로 이동
            finish()
        }


        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전에 호출
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 동안 호출
                if(!s.isNullOrEmpty()){
                    emailCodeButton.background = mainColor
                    emailEditText.background = ContextCompat.getDrawable(this@SignupEmailActivity, R.drawable.solid_stroke_q)
                }else{
                    emailCodeButton.background = defaultColor
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후 호출
            }
        })

        emailBtn.setOnClickListener {
            val email = emailEditText.text.toString()

            // 이메일 유효성 확인
            if(email.isEmpty()){
                emailCodeButton.background = defaultColor
                Toast.makeText(this, "이메일을 입력해줘.", Toast.LENGTH_SHORT).show()
            }else if(!isValidEmail(email)){
                showToast(toastLayout,emailEditText,700)
                toastTv.text = " 이메일 형식이 잘못됐어 !"
                toastTv.textAlignment = View.TEXT_ALIGNMENT_CENTER
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

    private fun showToast(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(this)
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)

        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val yOffset = location[1] -(30) -layout.measuredHeight
        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.view = layout

        toast.show()
    }



}
