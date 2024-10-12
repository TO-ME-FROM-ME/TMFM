package com.example.to_me_from_me.LetterWrite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.R
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SaveDialogFragment : DialogFragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var uid: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        val view = inflater.inflate(R.layout.save_dialog, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // 다이얼로그의 닫기 버튼 설정
        val closeIv: ImageView = view.findViewById(R.id.close_iv)
        closeIv.setOnClickListener {
            dismiss()
        }

        val new_btn: Button = view.findViewById(R.id.new_btn)
        val cwrite_btn: Button = view.findViewById(R.id.cwrite_btn)

        new_btn.setOnClickListener {
            val intent = Intent(activity, WriteLetterActivity::class.java)
            startActivity(intent)
            updateStorageField(false)
        }

        cwrite_btn.setOnClickListener {
            loadSavedLetter()
            updateStorageField(false)
        }

        return view
    }

    private fun updateStorageField(isStored: Boolean) {
        uid?.let { userId ->
            firestore.collection("users").document(userId)
                .update("storage", isStored)
        }
    }

    private fun loadSavedLetter() {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid

            // Firestore에서 임시저장된 편지 가져오기
            firestore.collection("users").document(userId).collection("letters")
                .whereEqualTo("storage", true) // storage 필드가 true인 문서만 가져옴
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.d("Firebase", "임시저장된 편지가 없습니다.")
                    } else {
                        for (document in documents) {
                            val date = document.getString("date")
                            val situation = document.getString("situation")
                            val emoji = document.getString("emoji")
                            val ad1 = document.getString("ad1")
                            val ad2 = document.getString("ad2")
                            val q1 = document.getString("q1")
                            val q2 = document.getString("q2")
                            val q3 = document.getString("q3")
                            val lettter = document.getString("lettter")

                            // 로그로 확인
                            Log.d("Firebase", "날짜: $date")
                            Log.d("Firebase", "상황: $situation")
                            Log.d("Firebase", "이모지: $emoji")

                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "임시저장된 편지를 가져오는 중 오류 발생", e)
                }
        }
    }
}

