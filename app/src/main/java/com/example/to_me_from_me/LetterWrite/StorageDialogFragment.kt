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
import androidx.fragment.app.activityViewModels
import com.example.to_me_from_me.HomeDialogFragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StorageDialogFragment : DialogFragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val sharedViewModel: ViewModel by activityViewModels()

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

        val exitBtn: Button = view.findViewById(R.id.exit_btn)
        exitBtn.setOnClickListener {

            val user = auth.currentUser
            val uid = user?.uid
            val currentDate = sharedViewModel.currentDate.value

            if (uid != null && currentDate != null) {
                Log.d("StorageDialogFragment", "UID 확인: $uid, 현재 날짜 확인: $currentDate")

                // Firestore에서 현재 작성 중인 문서 참조
                val letterDocRef = firestore.collection("users").document(uid)
                    .collection("letters").document(currentDate)

                // 문서 존재 여부 확인 후 삭제
                letterDocRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // 문서가 존재할 경우 삭제
                            letterDocRef.delete()
                                .addOnSuccessListener {
                                    navigateToMainActivity()
                                }
                        } else {
                            // 문서가 존재하지 않을 경우
                            Log.d("StorageDialogFragment", "삭제할 문서가 없습니다. 메인 화면으로 이동합니다.")
                            navigateToMainActivity()
                        }
                    }
            } else {
                Log.d("StorageDialogFragment", "UID 또는 현재 날짜가 null입니다.")
                navigateToMainActivity()
            }
        }

        // "저장하고 나가기" 버튼 클릭 이벤트 설정
        val saveBtn: Button = view.findViewById(R.id.save_btn)
        saveBtn.setOnClickListener {

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

                                        saveStorageToFirestore()
                                        navigateToMainActivity()
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

    // 메인 화면으로 이동하는 메서드
    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun saveStorageToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser
        val currentDate = sharedViewModel.currentDate.value

        if (user != null && currentDate != null) {
            val userDocumentRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .collection("letters")
                .document(currentDate)

            val storageData = mapOf(
                "storage" to true
            )

            userDocumentRef.update(storageData)
        }
    }

}




