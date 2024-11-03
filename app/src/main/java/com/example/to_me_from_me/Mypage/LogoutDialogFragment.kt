package com.example.to_me_from_me.Mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.LoginActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth

class LogoutDialogFragment : DialogFragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.logout_dialog, container, false)
        auth = FirebaseAuth.getInstance()

        // 로그아웃 버튼 설정
        val logoutButton: Button = view.findViewById(R.id.logout_btn)
        logoutButton.setOnClickListener {
            // 로그아웃
            auth.signOut()
            Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_LONG).show()
        }

        // 다이얼로그의 닫기 버튼 설정
        val closeIv: ImageView = view.findViewById(R.id.close_iv)
        closeIv.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all) // 배경으로 round_corner.xml 설정
    }

    override fun onStart() {
        super.onStart()

        // AuthStateListener 정의: 인증 상태가 변경될 때 호출됨
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                // 로그아웃 상태일 때 로그인 화면으로 이동
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                dismiss()  // 다이얼로그 닫기
            }
        }

        // 리스너 추가
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        // 리스너 제거
        auth.removeAuthStateListener(authStateListener)
    }
}

