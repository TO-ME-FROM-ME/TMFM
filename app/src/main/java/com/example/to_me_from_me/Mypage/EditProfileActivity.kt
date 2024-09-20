package com.example.to_me_from_me.Mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentResultListener
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class
EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nicknameET: EditText
    private lateinit var emailET: EditText
    private lateinit var pwdET: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val font = ResourcesCompat.getFont(this, R.font.font_gangwon)

        val profileimg: ImageView = findViewById(R.id.profile_img)
        val backButton: ImageView = findViewById(R.id.back_iv)

        // 비밀번호 가시성 상태를 저장하는 변수
        var isPasswordVisible = false
        val pwdEye = findViewById<ImageView>(R.id.pwd_eye_iv)
        val pwdCountTextView = findViewById<TextView>(R.id.char_count_tv)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nicknameET = findViewById(R.id.nickname_et)
        emailET = findViewById(R.id.email_et)
        pwdET = findViewById(R.id.pwd_et)

        loadUserProfile()

        profileimg.setOnClickListener {
            val profileImgFragment = ProfileImgFragment()
            profileImgFragment.show(supportFragmentManager, profileImgFragment.tag)
        }

        supportFragmentManager.setFragmentResultListener("profileImgKey", this, FragmentResultListener { requestKey, bundle ->
            if (requestKey == "profileImgKey") {
                val selectedImgResId = bundle.getInt("selectedImgResId", R.drawable.ic_profile_01_s)
                Log.d("이미지","profileImgKey : $selectedImgResId")
                profileimg.setImageResource(selectedImgResId)
            }
        })




        // pwdEye 클릭 시 비밀번호 가시성 토글
        pwdEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible  // 상태 반전

            if (isPasswordVisible) {
                // 비밀번호 보이도록 설정
                pwdET.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                pwdEye.setImageResource(R.drawable.ic_eye_on)
            } else {
                // 비밀번호 숨기도록 설정
                pwdET.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                pwdEye.setImageResource(R.drawable.ic_eye_off)
            }
            // 비밀번호 글씨체 유지
            pwdET.typeface = font

            // 커서 위치 유지
            pwdET.setSelection(pwdET.text.length)
        }
        pwdET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty()){
                    val charCount = s?.length ?: 0
                    pwdCountTextView.text = "$charCount"
                    pwdET.background = ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.solid_over_txt)
                }else{
                    pwdET.background = ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.solid_stroke_q)
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
    private fun loadUserProfile() {
        val user = auth.currentUser

        if (user != null) {
            // Firestore에서 사용자 문서 참조
            val userRef = firestore.collection("users").document(user.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Firestore에서 닉네임, 이메일, 비밀번호 가져오기
                        val nickname = document.getString("nickname")
                        val email = document.getString("email")
                        val password = document.getString("password")

                        // EditText에 값 설정
                        nicknameET.setText(nickname)
                        emailET.setText(email)
                        pwdET.setText(password)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("EditProfile", "사용자 정보를 불러오지 못했습니다.", e)
                }
        }
    }

}