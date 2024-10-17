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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LogoutDialogFragment : DialogFragment() {
    private lateinit var auth: FirebaseAuth

    private val tag = "LogoutDialogFragment"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.logout_dialog, container, false)
        auth = FirebaseAuth.getInstance()


        val logoutButton : Button = view.findViewById<Button>(R.id.logout_btn)

        logoutButton.setOnClickListener {
            // 로그아웃
            auth.signOut()
            Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_LONG).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

            // 현재 사용자 상태 확인
            if (auth.currentUser == null) {
                Toast.makeText(requireContext(), "사용자가 로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
            }

            // 다이얼로그 닫기
            dismiss()
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
}

