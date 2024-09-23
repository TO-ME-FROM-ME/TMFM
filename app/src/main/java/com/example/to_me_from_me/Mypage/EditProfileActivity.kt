package com.example.to_me_from_me.Mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nicknameET: EditText
    private lateinit var emailET: EditText
    private lateinit var pwdET: EditText
    private lateinit var profileIMG: ImageView
    private lateinit var saveButton: Button

    // 초기값을 저장하기 위한 변수
    private var initialNickname: String = ""
    private var initialEmail: String = ""
    private var initialPassword: String = ""
    private var initialProfileImage: Int = R.drawable.ic_profile_01_s
    private var selectedProfileImage: Int = R.drawable.ic_profile_01_s

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val font = ResourcesCompat.getFont(this, R.font.font_gangwon)


        val backButton: ImageView = findViewById(R.id.back_iv)
        saveButton = findViewById(R.id.save_button)
        saveButton.isEnabled = false // 기본적으로 비활성화


        // 비밀번호 가시성 상태를 저장하는 변수
        var isPasswordVisible = false
        val pwdEye = findViewById<ImageView>(R.id.pwd_eye_iv)
        val pwdCountTextView = findViewById<TextView>(R.id.char_count_tv)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nicknameET = findViewById(R.id.nickname_et)
        emailET = findViewById(R.id.email_et)
        pwdET = findViewById(R.id.pwd_et)
        profileIMG = findViewById(R.id.profile_img)

        loadUserProfile()

        profileIMG.setOnClickListener {
            val profileImgFragment = ProfileImgFragment()
            profileImgFragment.show(supportFragmentManager, profileImgFragment.tag)
        }


        supportFragmentManager.setFragmentResultListener("profileImgKey", this, FragmentResultListener { requestKey, bundle ->
            if (requestKey == "profileImgKey") {
                val selectedImgResId = bundle.getInt("selectedImgResId", R.drawable.ic_profile_01_s)
                Log.d("이미지","profileImgKey : $selectedImgResId")
                profileIMG.setImageResource(selectedImgResId)

                saveProfileImage(selectedImgResId)

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

        nicknameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 내용이 변경되었을 때 확인 버튼 활성화
                saveButton.isEnabled = true
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        emailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 내용이 변경되었을 때 확인 버튼 활성화
                saveButton.isEnabled = true
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        saveButton.setOnClickListener {
            updateUserProfile()
            saveButton.isEnabled = false // 업데이트 후 버튼 비활성화

        }

        backButton.setOnClickListener {
            //MyPageFragment로 이동하기
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToLoad", "MyPageFragment")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }



    private fun updateUserProfile() {
        val user = auth.currentUser

        if (user != null) {
            // Firestore의 사용자 문서 참조
            val userRef = firestore.collection("users").document(user.uid)

            // 변경된 데이터만 업데이트
            val updates = hashMapOf<String, Any>()

            val newNickname = nicknameET.text.toString()
            if (newNickname != initialNickname) {
                updates["nickname"] = newNickname
            }

            val newEmail = emailET.text.toString()
            if (newEmail != initialEmail) {
                updates["email"] = newEmail
                user.updateEmail(newEmail)
                    .addOnSuccessListener {
                        Log.d("EditProfileFB", "이메일 업데이트 성공.")
                    }
                    .addOnFailureListener { e ->
                        Log.w("EditProfileFB", "이메일 업데이트 실패.", e)
                    }
            }

            val newPassword = pwdET.text.toString()
            if (newPassword != initialPassword) {
                updates["password"] = newPassword
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Log.d("EditProfileFB", "비밀번호 업데이트 성공.")
                    }
                    .addOnFailureListener { e ->
                        Log.w("EditProfileFB", "비밀번호 업데이트 실패.", e)
                    }
            }

            // 프로필 이미지가 변경된 경우
            if (selectedProfileImage != initialProfileImage) {
                updates["profileImage"] = selectedProfileImage
            }

            // Firestore 업데이트
            if (updates.isNotEmpty()) {
                userRef.update(updates)
                    .addOnSuccessListener {
                        Log.d("EditProfileFB", "프로필 업데이트 성공.")
                        // 프로필 업데이트 성공 시 MyPageFragment로 이동
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("fragmentToLoad", "MyPageFragment")
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish() // 현재 Activity 종료
                    }
                    .addOnFailureListener { e ->
                        Log.w("EditProfileFB", "프로필 업데이트 실패.", e)
                    }
            } else {
                Log.d("EditProfileFB", "변경된 항목이 없습니다.")
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    private fun saveProfileImage(imageResId: Int) {
        val user = auth.currentUser

        if (user != null) {
            // Firestore의 사용자 문서 참조
            val userRef = firestore.collection("users").document(user.uid)

            // 프로필 이미지 리소스 ID 저장
            val updates = hashMapOf<String, Any>(
                "profileImage" to imageResId
            )

            userRef.update(updates)
                .addOnSuccessListener {
                    Log.d("EditProfile", "프로필 이미지가 성공적으로 저장되었습니다.")
                }
                .addOnFailureListener { e ->
                    Log.w("EditProfile", "프로필 이미지를 저장하지 못했습니다.", e)
                }
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
                        initialNickname  = document.getString("nickname") ?: ""
                        initialEmail  = document.getString("email")?: ""
                        initialPassword = document.getString("password")?: ""
                        initialProfileImage  = document.getLong("profileImage")?.toInt() ?: R.drawable.ic_profile_01_s

                        // EditText에 값 설정
                        nicknameET.setText(initialNickname)
                        emailET.setText(initialEmail)
                        pwdET.setText(initialPassword)
                        profileIMG.setImageResource(initialProfileImage )

                    }
                }
                .addOnFailureListener { e ->
                    Log.w("EditProfile", "사용자 정보를 불러오지 못했습니다.", e)
                }
        }
    }


}