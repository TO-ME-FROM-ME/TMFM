package com.example.to_me_from_me.Mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userNameTV: TextView
    private lateinit var profileIMG: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }
    override fun onResume() {
        super.onResume()
        // 프로필 이미지와 닉네임을 다시 불러옴
        loadUserNickname()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        userNameTV = view.findViewById(R.id.user_name)
        profileIMG = view.findViewById(R.id.user_img)
        loadUserNickname()

        val userProfile = view.findViewById<ImageView>(R.id.user_go)
        val userAlarm = view.findViewById<ImageView>(R.id.alarm_go)
        val userLogout = view.findViewById<ImageView>(R.id.logout_go)
        val userDeleteAcc = view.findViewById<ImageView>(R.id.deleteacc_go)

        userProfile.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }

        userAlarm.setOnClickListener {
            startActivity(Intent(activity, AlarmActivity::class.java))
        }

        userLogout.setOnClickListener {
            val dialogFragment = LogoutDialogFragment()
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialogFragment.show(parentFragmentManager, "LogoutDialogFragment")
        }

        userDeleteAcc.setOnClickListener {
            startActivity(Intent(activity, DeleteAccActivity::class.java))
        }
    }
    private fun loadUserNickname() {
        val user = auth.currentUser

        if (user != null) {
            // Firestore에서 사용자 문서 참조
            val userRef = firestore.collection("users").document(user.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // 닉네임 가져와서 TextView에 설정
                        val nickname = document.getString("nickname")
                        userNameTV.text = nickname
                        val profileImage = document.getLong("profileImage")?.toInt() ?: R.drawable.ic_my_01_s
                        profileIMG.setImageResource(profileImage)
                        Log.d("EditProfileFB","img : $profileImage")

                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "데이터 로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            userNameTV.text = "로그인을 해주세요."
        }
    }
}
