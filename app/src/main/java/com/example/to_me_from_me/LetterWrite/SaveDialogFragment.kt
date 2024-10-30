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
import androidx.lifecycle.ViewModelProvider
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SaveDialogFragment : DialogFragment() {

    private lateinit var viewModel: ViewModel
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
            uid?.let { userId ->
                firestore.collection("users").document(userId).collection("letters")
                    .whereEqualTo("storage", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Log.d("Firebase", "삭제할 문서가 없습니다.")
                        } else {
                            for (document in documents) {
                                document.reference.delete()
                            }
                        }
                    }
                }
            updateStorageField1(false)
            val intent = Intent(activity, WriteLetterActivity::class.java)
            startActivity(intent)
        }

        cwrite_btn.setOnClickListener {
            loadSavedLetter(object : OnLoadCompleteListener {
                override fun onLoadComplete() {
                    updateStorageField1(false)
                    updateStorageField2(false)

                    // Load the data from ViewModel
                    val situation = viewModel.situation.value
                    val emoji = viewModel.emoji.value
                    val ad1 = viewModel.ad1.value
                    val ad2 = viewModel.ad2.value
                    val q1 = viewModel.q1.value
                    val q2 = viewModel.q2.value
                    val q3 = viewModel.q3.value
                    val letter = viewModel.letter.value

                    // Intent 생성
                    val intent = Intent(activity, WriteLetterActivity::class.java).apply {
                        putExtra("situation", situation)
                        putExtra("emoji", emoji)
                        putExtra("ad1", ad1)
                        putExtra("ad2", ad2)
                        putExtra("q1", q1)
                        putExtra("q2", q2)
                        putExtra("q3", q3)
                        putExtra("letter", letter)
                    }

                    // WriteLetterActivity로 이동
                    startActivity(intent)
                }
            })
        }


        return view
    }

    private fun updateStorageField1(isStored: Boolean) {
        uid?.let { userId ->
            firestore.collection("users").document(userId)
                .update("storage", isStored)
        }
    }

    private fun updateStorageField2(isStored: Boolean) {
        uid?.let { userId ->
            firestore.collection("users").document(userId).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update("storage", isStored)
                    }
                }
        }
    }

    interface OnLoadCompleteListener {
        fun onLoadComplete()
    }

    private fun loadSavedLetter(onLoadComplete: OnLoadCompleteListener) {
        val sharedViewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)

        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid

            firestore.collection("users").document(userId).collection("letters")
                .whereEqualTo("storage", true)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.d("Firebase", "임시저장된 편지가 없습니다.")
                    } else {
                        val document = documents.first()
                            // Firestore에서 값 가져오기
                            val situation = document.getString("situation")
                            val emoji = document.getString("emoji")
                            val ad1 = document.getString("ad1")
                            val ad2 = document.getString("ad2")
                            val q1 = document.getString("q1")
                            val q2 = document.getString("q2")
                            val q3 = document.getString("q3")
                            val letter = document.getString("letter")

                            // ViewModel에 저장하는 함수 호출
                            saveViewModel(sharedViewModel, situation, emoji, ad1, ad2, q1, q2, q3, letter)

                            // 로그로 확인
                            Log.d("FirestoreData", "Loaded and saved letter data.")
                        }
                    onLoadComplete.onLoadComplete()
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "임시저장된 편지를 가져오는 중 오류 발생", e)
                    onLoadComplete.onLoadComplete()
                }
        }
    }

    private fun saveViewModel(
        viewModel: ViewModel,
        situation: String?,
        emoji: String?,
        ad1: String?,
        ad2: String?,
        q1: String?,
        q2: String?,
        q3: String?,
        letter: String?
    ) {
        situation?.let { viewModel.setSituationF(it) }
        emoji?.let { viewModel.setEmojiF(it) }
        ad1?.let { viewModel.setAd1(it) }
        ad2?.let { viewModel.setAd2(it) }
        q1?.let { viewModel.setQ1(it) }
        q2?.let { viewModel.setQ2(it) }
        q3?.let { viewModel.setQ3(it) }
        letter?.let { viewModel.setLetter(it) }

        // 로그로 확인
        Log.d("FirestoreData", "Situation set in ViewModel: ${situation ?: "null"}")
        Log.d("FirestoreData", "Emoji set in ViewModel: ${emoji ?: "null"}")
        Log.d("FirestoreData", "Ad1 set in ViewModel: ${ad1 ?: "null"}")
        Log.d("FirestoreData", "Ad2 set in ViewModel: ${ad2 ?: "null"}")
        Log.d("FirestoreData", "Q1 set in ViewModel: ${q1 ?: "null"}")
        Log.d("FirestoreData", "Q2 set in ViewModel: ${q2 ?: "null"}")
        Log.d("FirestoreData", "Q3 set in ViewModel: ${q3 ?: "null"}")
        Log.d("FirestoreData", "Letter set in ViewModel: ${letter ?: "null"}")
    }
}

