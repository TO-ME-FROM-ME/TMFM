package com.example.to_me_from_me.Mypage

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.LoginActivity
import com.example.to_me_from_me.MusicService
import com.example.to_me_from_me.R
import com.example.to_me_from_me.startMusicService
import com.example.to_me_from_me.stopMusicService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userNameTV: TextView
    private lateinit var profileIMG: ImageView
    private lateinit var userProfile: ImageView

    override fun onResume() {
        super.onResume()
        loadUserNickname()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        userNameTV = view.findViewById(R.id.user_name)
        profileIMG = view.findViewById(R.id.user_img)

        userProfile = view.findViewById<ImageView>(R.id.user_go)
        val userAlarm = view.findViewById<ImageView>(R.id.alarm_go)
        val userLogout = view.findViewById<ImageView>(R.id.logout_go)
        val userDeleteAcc = view.findViewById<ImageView>(R.id.deleteacc_go)


        userAlarm.setOnClickListener {
            startActivity(Intent(activity, AlarmActivity::class.java))
        }

        userLogout.setOnClickListener {
            val dialogFragment = LogoutDialogFragment()
            dialogFragment.show(parentFragmentManager, "LogoutDialogFragment")
        }

        userDeleteAcc.setOnClickListener {
            startActivity(Intent(activity, DeleteAccActivity::class.java))
        }
    }

    private fun loadUserNickname() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nickname = document.getString("nickname") ?: "닉네임이 없습니다."
                        userNameTV.text = nickname

                        val profileImageKey = document.getString("profileImage") ?: "default"
                        val profileImageDrawable = getEmojiDrawable(profileImageKey)

                        if (profileImageDrawable == 0) {
                            profileIMG.setImageResource(R.drawable.ic_my_01_s) // 기본 이미지
                        } else {
                            profileIMG.setImageResource(profileImageDrawable)
                        }

                        userProfile.setOnClickListener {
                            if (auth.currentUser != null) {
                                // 사용자가 로그인한 상태이면 프로필 편집 화면으로 이동
                                //startActivity(Intent(activity, EditProfileActivity::class.java))
                                val intent = Intent(requireContext(), EditProfileActivity::class.java)
                                intent.putExtra("profileImageKey", profileImageKey) // 데이터 전달
                                startActivity(intent)
                            } else {
                                // 로그인이 되어 있지 않으면 LoginActivity로 이동
                                startActivity(Intent(activity, LoginActivity::class.java))
                            }
                        }


                        Log.d("UserPref", "nickname: $nickname, profileImageKey: $profileImageKey")
                    } else {
                        Toast.makeText(activity, "사용자 데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "데이터 로드 실패: ${e.message}")
                    Toast.makeText(activity, "데이터 로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            userNameTV.text = "로그인을 해주세요."
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

}
