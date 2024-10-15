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
import com.example.to_me_from_me.HomeDialogFragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StorageDialogFragment : DialogFragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.storage_dialog, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 백 스택의 항목 수 가져오기
        val backStackCount = parentFragmentManager.backStackEntryCount

        // 다이얼로그의 닫기 버튼 설정
        val closeIv: ImageView = view.findViewById(R.id.close_iv)
        closeIv.setOnClickListener {
            dismiss()
        }

        // "저장하고 나가기" 버튼 클릭 이벤트 설정
        val saveBtn: Button = view.findViewById(R.id.save_btn)
        saveBtn.setOnClickListener {
            Log.d("StorageDialogFragment", "저장하고 나가기 버튼 클릭됨")

            // Firebase에 저장할 데이터 준비
            val user = auth.currentUser
            val uid = user?.uid
            if (uid != null) {
                Log.d("StorageDialogFragment", "UID 확인: $uid")  // UID 로그 출력
                // Firestore에 storage 필드 업데이트
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val currentStorageValue = document.getBoolean("storage") ?: false
                            Log.d("StorageDialogFragment", "현재 storage 값: $currentStorageValue")

                            if (!currentStorageValue) {
                                // storage 필드가 false일 경우 true로 업데이트
                                firestore.collection("users").document(uid)
                                    .update("storage", true)
                                    .addOnSuccessListener {
                                        Log.d("StorageDialogFragment", "storage 필드가 true로 업데이트됨")

                                        // 메인 화면으로 이동
                                        val intent = Intent(activity, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        activity?.finish()
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("StorageDialogFragment", "Firestore 데이터 가져오기 실패", e)
                    }
            } else {
                Log.d("StorageDialogFragment", "UID가 null입니다.")
            }
        }
        return view
    }
}




