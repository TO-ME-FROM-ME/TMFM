package com.example.to_me_from_me.Mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.example.to_me_from_me.Signup.SignupNicknameActivity
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


    private var selectedProfileImageResId: String? = null
    private var initialProfileImageResId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val font = ResourcesCompat.getFont(this, R.font.font_gangwon)

        val backButton: ImageView = findViewById(R.id.back_iv)
        saveButton = findViewById(R.id.save_button)


        // 비밀번호 가시성 상태를 저장하는 변수
        var isPasswordVisible = false
        val pwdEye = findViewById<ImageView>(R.id.pwd_eye_iv)
        val pwdCountTextView = findViewById<TextView>(R.id.char_count_tv)

        val nicknameCountTextView = findViewById<TextView>(R.id.char_count_tv_e)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nicknameET = findViewById(R.id.nickname_et)
        emailET = findViewById(R.id.email_et)
        pwdET = findViewById(R.id.pwd_et)
        profileIMG = findViewById(R.id.profile_img)


        // 전달받은 데이터 가져오기
        val profileImageKey = intent.getStringExtra("profileImageKey") ?: "default"

        // 프로필 이미지 키를 기반으로 UI 업데이트
        val profileImageDrawable = getEmojiDrawable(profileImageKey)
        profileIMG.setImageResource(profileImageDrawable)

        Log.d("EditProfileActivity", "Received profileImageKey: $profileImageKey")


        loadUserProfile()

        val user = auth.currentUser
        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)

            // Firestore에서 사용자 데이터 가져오기
            userRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    // Firestore에서 프로필 이미지 ID 가져오기
                    //initialProfileImageResId = document.getLong("profileImage")?.toInt() ?: R.drawable.excited_s
                    // 가져온 이미지로 ImageView 설정
                    //profileIMG.setImageResource(initialProfileImageResId)
                }
            }.addOnFailureListener { e ->
                Log.w("EditProfileFB", "사용자 데이터를 가져오는 데 실패했습니다.", e)
            }
        }


        profileIMG.setOnClickListener {
            val profileImgFragment = ProfileImgFragment()
            profileImgFragment.show(supportFragmentManager, profileImgFragment.tag)
        }


        supportFragmentManager.setFragmentResultListener("profileImgKey", this, FragmentResultListener { requestKey, bundle ->
            if (requestKey == "profileImgKey") {
                //val selectedImgResId = bundle.getString("selectedImgResId", "happy")
                //Log.d("이미지","profileImgKey : $selectedImgResId")
                //profileIMG.setImageResource(selectedImgResId)
                val selectedImgResId = bundle.getString("selectedImgResId", "default")
                Log.d("이미지", "선택된 이미지: $selectedImgResId")

                selectedProfileImageResId = selectedImgResId

                // ImageView에 표시할 리소스를 결정
                val drawableResId = when (selectedImgResId) {
                    "excited" -> R.drawable.excited_s2
                    "happy" -> R.drawable.happy_s2
                    "normal" -> R.drawable.normal_s2
                    "upset" -> R.drawable.upset_s2
                    "angry" -> R.drawable.angry_s2
                    else -> R.drawable.ic_my_01_s // 기본 이미지
                }

                profileIMG.setImageResource(drawableResId)
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
                    saveButton.isEnabled = true
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
                if(!s.isNullOrEmpty()){
                    val charCount = s?.length ?: 0
                    nicknameCountTextView.text = "$charCount"
                    nicknameET.background = ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.solid_over_txt)
                    saveButton.isEnabled = true
                }else{
                    nicknameET.background = ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.solid_stroke_q)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        emailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty()){
                    emailET.background = ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.solid_over_txt)
                    saveButton.isEnabled = true
                }else{
                    emailET.background = ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.solid_stroke_q)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                saveButton.isEnabled = true
            }
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

    private fun getEmojiDrawable(profileImage: String): Int {
        return when (profileImage.trim().lowercase()) {
            "excited" -> R.drawable.excited_s2
            "happy" -> R.drawable.happy_s2
            "normal" -> R.drawable.normal_s2
            "upset" -> R.drawable.upset_s2
            "angry" -> R.drawable.angry_s2
            else -> R.drawable.ic_my_01_s
        }
    }


    private fun updateUserProfile() {
        val user = auth.currentUser

        val toastLayout = LayoutInflater.from(this).inflate(R.layout.toast_pwd, null, false)
        val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

        val toastLayout2 = LayoutInflater.from(this).inflate(R.layout.toast_pwd12, null, false)
        val toastTv2 = toastLayout2.findViewById<TextView>(R.id.toast_tv)

        val toastLayout3 = LayoutInflater.from(this).inflate(R.layout.toast, null, false)
        val toastTv3 = toastLayout3.findViewById<TextView>(R.id.toast_tv)

        val toastLayout4 = LayoutInflater.from(this).inflate(R.layout.toast_nick6, null, false)
        val toastTv4 = toastLayout4.findViewById<TextView>(R.id.toast_tv)

        if (user != null) {
            // Firestore의 사용자 문서 참조
            val userRef = firestore.collection("users").document(user.uid)

            // 변경된 데이터만 업데이트
            val updates = hashMapOf<String, Any>()

            val newNickname = nicknameET.text.toString()
            if (newNickname.isNotEmpty() && newNickname != initialNickname) {
                if (newNickname.length < 2) {
                    toastTv.text = "2글자 이상 작성해줘!"
                    showToast(toastLayout, nicknameET, 700)
                } else if (newNickname.length > 6) {
                    toastTv4.text = "6글자 이하로 작성해줘!"
                    showToast(toastLayout4, nicknameET, 700)
                } else {
                    updates["nickname"] = newNickname
                }
            }

            val newEmail = emailET.text.toString()
            if (newEmail.isNotEmpty() && newEmail != initialEmail) {
                if (!isValidEmail(newEmail)) {
                    showToast2(toastLayout3, emailET, 700)
                    toastTv3.text = "이메일 형식으로 작성해줘!"
                    toastTv3.textAlignment = View.TEXT_ALIGNMENT_CENTER
                } else {
                    updates["email"] = newEmail
                }
            }

            val newPassword = pwdET.text.toString()
            if (newPassword.isNotEmpty() && newPassword != initialPassword) {
                if (newPassword.length < 8) {
                    toastTv.text = "8글자 이상 작성해줘!"
                    showToast(toastLayout, pwdET, 700)
                } else if (newPassword.length > 12) {
                    toastTv2.text = "12글자 이하로 작성해줘!"
                    showToast(toastLayout2, pwdET, 700)
                } else {
                    updates["password"] = newPassword
                }
            }

            // 프로필 이미지가 변경된 경우
            if (selectedProfileImageResId != null && selectedProfileImageResId != initialProfileImageResId) {
                updates["profileImage"] = selectedProfileImageResId!!
            }

            // Firestore 업데이트
            if (updates.isNotEmpty()) {
                userRef.update(updates)
                    .addOnSuccessListener {
                        Log.d("EditProfileFB", "프로필 업데이트 성공.")
                        // 프로필 업데이트 성공 시 MyPageFragment로 이동
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("fragmentToLoad", "MyPageFragment")
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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

    private fun isValidEmail(newEmail: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return newEmail.matches(Regex(emailPattern))
    }


    /*private fun saveProfileImage(imageResId: Int) {
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
    }*/


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
                        //initialProfileImage  = document.getLong("profileImage")?.toInt() ?: R.drawable.ic_profile_01_s

                        val profileImageKey = document.getString("profileImage") ?: "default"
                        val profileImageDrawable = getEmojiDrawable(profileImageKey)

                        // ImageView에 이미지 설정
                        profileIMG.setImageResource(profileImageDrawable)

                        Log.d("이미지", "profileImageKey 이미지 : $profileImageKey")


                        // EditText에 값 설정
                        nicknameET.setText(initialNickname)
                        emailET.setText(initialEmail)
                        pwdET.setText(initialPassword)
                        //profileIMG.setImageResource(initialProfileImage )

                    }
                }
                .addOnFailureListener { e ->
                    Log.w("EditProfile", "사용자 정보를 불러오지 못했습니다.", e)
                }
        }


    }
    private fun showToast(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(this)
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)

        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val yOffset = location[1] -(-10) -layout.measuredHeight
        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.view = layout

        toast.show()
    }

    private fun showToast2(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(this)
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)

        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val yOffset = location[1] -(-10) -layout.measuredHeight
        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.view = layout

        toast.show()
    }

}