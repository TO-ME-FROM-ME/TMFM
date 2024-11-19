package com.example.to_me_from_me.SetTest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.CoachMark.CoachMarkActivity
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TestquitDialogFragment : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.testquit_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeButton = view.findViewById<ImageView>(R.id.close_iv)
        closeButton.setOnClickListener {
            dismiss()
        }

        // 저장 버튼 클릭 리스너 설정
        val quitButton = view.findViewById<Button>(R.id.quit_btn)
        quitButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userRef = FirebaseFirestore.getInstance().collection("users").document(user.uid)
                userRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.contains("totalScore")) {
                            // totalScore 필드가 존재하는 경우
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // totalScore 필드가 존재하지 않는 경우
                            val intent = Intent(activity, CoachMarkActivity::class.java)
                            startActivity(intent)
                        }
                        dismiss() // 다이얼로그 닫기
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all)
    }
}