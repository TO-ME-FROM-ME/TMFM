package com.example.to_me_from_me.Signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
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
import androidx.core.content.ContextCompat
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var signupFinishFragment: SignupFinishFragment
class SignupNicknameActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_nickname)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nicknameEditText = findViewById<EditText>(R.id.nickname_et)
        val signupBtn = findViewById<Button>(R.id.signup_btn)

        val email = intent.getStringExtra("email")
        val pwd = intent.getStringExtra("pwd")
        val charCountTextView = findViewById<TextView>(R.id.char_count_tv)
        val backButton = findViewById<ImageView>(R.id.back_iv)

        val toastLayout = LayoutInflater.from(this).inflate(R.layout.toast_pwd, null, false)
        val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

        val toastLayout2 = LayoutInflater.from(this).inflate(R.layout.toast_nick6, null, false)
        val toastTv2 = toastLayout2.findViewById<TextView>(R.id.toast_tv)

        val mainColor = ContextCompat.getDrawable(this, R.drawable.solid_no_main)
        val defaultColor = ContextCompat.getDrawable(this, R.drawable.solid_no_gray)

        // 닉네임 최대 6글자
        charCountTextView.filters = arrayOf(InputFilter.LengthFilter(6))


        signupFinishFragment = SignupFinishFragment()


        // 닉네임 유효성 검사 메서드
        fun validateNickname() {
            val nickname = nicknameEditText.text.toString()

            if (nickname.length in 2..6 ) {
                // 비밀번호가 유효하고 일치할 때 버튼 색상 변경 및 활성화
                signupBtn.background = mainColor
            } else {
                // 비밀번호가 유효하지 않거나 일치하지 않을 때 버튼 비활성화
                signupBtn.background = defaultColor
            }
        }

        signupBtn.setOnClickListener {
            val nickname = nicknameEditText.text.toString()

            if(nickname.length <2 ){
                toastTv.text = "2글자 이상 작성해줘!"
                showToast(toastLayout, nicknameEditText, 700)
            } else if(nickname.length > 6){
                toastTv2.text = "6글자 이하로 작성해줘!"
                showToast(toastLayout2, nicknameEditText, 700)
            }
            else {
                // Firebase Authentication을 통해 사용자 등록
                if (email != null) {
                    if (pwd != null) {
                        auth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Firebase Authentication에 사용자 추가 성공
                                    val user = auth.currentUser
                                    if (user != null) {
                                        // Firestore에 사용자 정보 저장
                                        saveUserToFirestore(user.uid, email, nickname)
                                    }
                                } else {
                                    // 회원가입 실패
                                    Log.w("Signup", "createUserWithEmail:failure", task.exception)
                                }
                            }
                    }
                }
            }
        }


        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때마다 글자 수 업데이트
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount"
                validateNickname()
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

    private fun saveUserToFirestore(userId: String, email: String, nickname: String) {
        val user = hashMapOf(
            "email" to email,
            "nickname" to nickname
        )

        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "User data successfully written!")
                // 회원가입 성공 후 다음 화면으로 이동
                signupFinishFragment.show(supportFragmentManager, "SignupFinishFragment")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing user data", e)
                Toast.makeText(this, "Firestore 저장 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showToast(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(this)
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)

        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val yOffset = location[1] -(-30) -layout.measuredHeight
        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.view = layout

        toast.show()
    }
}