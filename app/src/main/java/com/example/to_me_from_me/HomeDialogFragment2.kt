package com.example.to_me_from_me

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeDialogFragment2 : DialogFragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var excitedIv: ImageView
    private lateinit var happyIv: ImageView
    private lateinit var normalIv: ImageView
    private lateinit var sadIv: ImageView
    private lateinit var upsetIv: ImageView

    override fun onStart() {
        super.onStart()
        // 다이얼로그 위치 조정
        dialog?.window?.apply {
            val params = attributes
            params.x = 40
            params.y = -450
            attributes = params
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_dialog2, container, false)

        val titleTv = view.findViewById<TextView>(R.id.title_tv)
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        excitedIv = view.findViewById(R.id.excited_iv)
        happyIv = view.findViewById(R.id.happy_iv)
        normalIv = view.findViewById(R.id.normal_iv)
        sadIv = view.findViewById(R.id.sad_iv)
        upsetIv = view.findViewById(R.id.upset_iv)

        if (uid != null) {
            firestore = FirebaseFirestore.getInstance()
            Log.d("UserPref", "HomeDialogFragment2 : 사용자 토큰 : $uid")

            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    Log.d("UserPref", "Firestore 쿼리 성공. 문서 수: ${documents.size()}")
                    if (documents.isEmpty) {
                        Log.d("UserPref", "Firebase : 해당 문서가 없습니다.")
                    } else {
                        for (document in documents) {
                            Log.d("UserPref", "Firebase : ${document.id} => ${document.data}")
                            val emoji = document.getLong("emoji")?.toInt()
                            titleTv.text = "받고 싶은 편지의 감정을 선택해줘!"
                            updateEmojiView(emoji)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("UserPref", "Error getting documents: ", exception)
                }
        } else {
            Log.d("UserPref", "저장된 이메일이 없습니다.")
        }

        return view
    }

    private fun updateEmojiView(emoji: Int?) {
        // emoji 값에 따라 해당 ImageView 보이기
        when (emoji) {
            2131165351 -> {
                excitedIv.setImageResource(R.drawable.ic_main_y_01)
                setEmojiClickListener(excitedIv, R.drawable.ic_main_y_01, R.drawable.ic_main_s_01)
            }
            2131165355 -> {
                Log.d("UserPref", "0")
                happyIv.setImageResource(R.drawable.ic_main_y_02)
                Log.d("UserPref", "1")
                happyIv.setOnClickListener {
                    setEmojiClickListener(happyIv, R.drawable.ic_main_y_02, R.drawable.ic_main_s_02)
                    Log.d("UserPref", "2")
                }
                Log.d("UserPref", "3")

            }
            2131165565 -> {
                normalIv.setImageResource(R.drawable.ic_main_y_03)
                setEmojiClickListener(normalIv, R.drawable.ic_main_y_03, R.drawable.ic_main_s_03)
            }
            2131165612 -> {
                sadIv.setImageResource(R.drawable.ic_main_y_04)
                setEmojiClickListener(sadIv, R.drawable.ic_main_y_04, R.drawable.ic_main_s_04)
            }
            2131165312 -> {
                upsetIv.setImageResource(R.drawable.ic_main_y_05)
                setEmojiClickListener(upsetIv, R.drawable.ic_main_y_05, R.drawable.ic_main_s_05)
            }
            else -> Log.d("UserPref", "Unknown emoji: $emoji")
        }
    }

    private fun setEmojiClickListener(emojiView: ImageView, originalImageResId: Int, newImageResId: Int) {
        // 상태를 저장할 변수
        var isOriginalImage = true // 초기값을 원래 이미지로 설정
        Log.d("UserPref", "현재 상태: $isOriginalImage")

        emojiView.setOnClickListener {
            // 현재 이미지가 원래 이미지인지 확인
            if (isOriginalImage) {
                emojiView.setImageResource(newImageResId)
                isOriginalImage = false // 현재 이미지를 새로운 이미지로 설정
            } else {
                emojiView.setImageResource(originalImageResId)
                isOriginalImage = true // 다시 원래 이미지로 설정
            }

            // 로그 추가하여 현재 상태 확인
            Log.d("UserPref", "현재 상태: ${if (isOriginalImage) "원래 이미지" else "새로운 이미지"}")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentDialogTheme)
    }
}
