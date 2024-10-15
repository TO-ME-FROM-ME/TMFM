package com.example.to_me_from_me

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var mainColor: Drawable
    private lateinit var defaultColor: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 이전 사용자 정보 초기화
        val emailEditText = findViewById<EditText>(R.id.login_input_id_et)
        val pwdEye = findViewById<ImageView>(R.id.eye_off)
        var isPasswordVisible = false
        val pwdEditText =findViewById<EditText>(R.id.login_input_pwd_et)
        val font = ResourcesCompat.getFont(this, R.font.font_gangwon)

        FirebaseApp.initializeApp(this)
        val providerFactory = PlayIntegrityAppCheckProviderFactory.getInstance()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(providerFactory)

        emailEditText.text.clear()
        pwdEditText.text.clear()

        auth = Firebase.auth

        // Drawable 초기화
        mainColor = ContextCompat.getDrawable(this, R.drawable.solid_no_main)!!
        defaultColor = ContextCompat.getDrawable(this, R.drawable.solid_no_gray)!!

        // 이메일과 비밀번호 입력 필드에 TextWatcher 추가
        binding.loginInputIdEt.addTextChangedListener(loginTextWatcher)
        binding.loginInputPwdEt.addTextChangedListener(loginTextWatcher)

        binding.loginBtn.setOnClickListener {
            val email = binding.loginInputIdEt.text.toString().trim()
            val password = binding.loginInputPwdEt.text.toString().trim()

            signIn(email, password)
            performLogin()
        }

        binding.signupTv.setOnClickListener {
            startActivity(Intent(this, SignupEmailActivity::class.java))
            finish() // 현재 화면 종료
        }


        binding.searchPwdTv.setOnClickListener {
            startActivity(Intent(this, FindPwdActivity::class.java))
            finish() // 현재 화면 종료
        }

        pwdEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // 비밀번호 보이도록 설정
                pwdEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                pwdEye.setImageResource(R.drawable.ic_eye_on)
            } else {
                // 비밀번호 숨기도록 설정
                pwdEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                pwdEye.setImageResource(R.drawable.ic_eye_off)
            }
            pwdEditText.typeface = font
            // 커서 위치 유지
            pwdEditText.setSelection(pwdEditText.text.length)
        }
    }

    private fun signIn(email: String, password: String) {
        val toastLayout = LayoutInflater.from(this).inflate(R.layout.toast_pwd, null, false)
        val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)
        val inputId = findViewById<EditText>(R.id.login_input_id_et)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 로그인에 성공한 경우 버튼 색상을 변경
                    binding.loginBtn.background = mainColor

                    // 로그인 성공 시 현재 사용자 정보에서 UID를 가져옴
                    val user = auth.currentUser
                    val uid = user?.uid

                    // UID가 null이 아닌 경우 SharedPreferences에 UID 저장
                    if (uid != null) {
                        saveUserUid(uid)
                    }

                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 로그인에 실패한 경우 Toast 메시지로 에러를 띄워준다
                    toastTv.text = "등록된 정보가 없어😥"
                    showToast(toastLayout, inputId, 700)
                    Log.e("로그인오류", "${task.exception?.message}")
                }
            }
    }

    private fun saveUserUid(uid: String) {
        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("userUid", uid)
        editor.apply()

    }

    private fun showToast(layout: View, editText: EditText, duration: Int) {
        val toast = Toast(this)
        val location = IntArray(2)
        editText.getLocationOnScreen(location)

        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val yOffset = location[1] - (-30) - layout.measuredHeight
        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT

        toast.show()
    }

    private fun performLogin() {
        // 아이디 비번 확인
    }

    // TextWatcher를 활용한 이메일, 비밀번호 입력 실시간 확인
    private val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val email = binding.loginInputIdEt.text.toString().trim()
            val password = binding.loginInputPwdEt.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 등록된 정보가 있는 경우 버튼 색상 변경
                            binding.loginBtn.background = mainColor
                        } else {
                            // 등록된 정보가 없는 경우 기본 색상으로
                            binding.loginBtn.background = defaultColor
                        }
                    }
            } else {
                // 이메일이나 비밀번호가 비어 있는 경우 기본 색상으로
                binding.loginBtn.background = defaultColor
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }
}

