package com.example.to_me_from_me.Mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R

class
EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val font = ResourcesCompat.getFont(this, R.font.font_gangwon)

        val profileimg: ImageView = findViewById(R.id.profile_img)
        val backButton: ImageView = findViewById(R.id.back_iv)

        // 비밀번호 가시성 상태를 저장하는 변수
        var isPasswordVisible = false
        val pwdEditText = findViewById<EditText>(R.id.pwd_et)
        val pwdEye = findViewById<ImageView>(R.id.pwd_eye_iv)
        val pwdCountTextView = findViewById<TextView>(R.id.char_count_tv)

        profileimg.setOnClickListener {
            val profileImgFragment = ProfileImgFragment()
            profileImgFragment.show(supportFragmentManager, profileImgFragment.tag)
        }





        // pwdEye 클릭 시 비밀번호 가시성 토글
        pwdEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible  // 상태 반전

            if (isPasswordVisible) {
                // 비밀번호 보이도록 설정
                pwdEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                pwdEye.setImageResource(R.drawable.ic_eye_on)
            } else {
                // 비밀번호 숨기도록 설정
                pwdEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                pwdEye.setImageResource(R.drawable.ic_eye_off)
            }
            // 비밀번호 글씨체 유지
            pwdEditText.typeface = font

            // 커서 위치 유지
            pwdEditText.setSelection(pwdEditText.text.length)
        }
        pwdEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty()){
                    val charCount = s?.length ?: 0
                    pwdCountTextView.text = "$charCount"
                    pwdEditText.background = ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.solid_over_txt)
                }else{
                    pwdEditText.background = ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.solid_stroke_q)
                }
            }

            override fun afterTextChanged(s: Editable?) {1
            }
        })





        backButton.setOnClickListener {
            //MyPageFragment로 이동하기
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToLoad", "MyPageFragment")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}