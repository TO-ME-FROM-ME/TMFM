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

    private var currentSelectedEmoji: ImageView? = null

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
                    if (documents.isEmpty) {
                        Log.d("UserPref", "Firebase : 해당 문서가 없습니다.")
                    } else {
                        for (document in documents) {
                            // Firestore에서 가져온 emoji 필드를 문자열로 처리
                            val emojiString = document.getString("emoji") // 문자열로 가져오기
                            titleTv.text = "받고 싶은 편지의 감정을 선택해줘!"
                            updateEmojiView(emojiString) // 문자열로 updateEmojiView에 전달
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

    private fun updateEmojiView(emoji: String?) {
        // emoji 값에 따라 해당 ImageView 보이기
        when (emoji) {
            "excited_s" -> {
                excitedIv.setImageResource(R.drawable.ic_main_y_01)
                setEmojiClickListener(excitedIv, R.drawable.ic_main_y_01, R.drawable.ic_main_s_01)
            }
            "happy_s" -> {
                happyIv.setImageResource(R.drawable.ic_main_y_02)
                setEmojiClickListener(happyIv, R.drawable.ic_main_y_02, R.drawable.ic_main_s_02)
            }
            "normal_s" -> {
                normalIv.setImageResource(R.drawable.ic_main_y_03)
                setEmojiClickListener(normalIv, R.drawable.ic_main_y_03, R.drawable.ic_main_s_03)
            }
            "sad_s" -> {
                sadIv.setImageResource(R.drawable.ic_main_y_04)
                setEmojiClickListener(sadIv, R.drawable.ic_main_y_04, R.drawable.ic_main_s_04)
            }
            "upset_s" -> {
                upsetIv.setImageResource(R.drawable.ic_main_y_05)
                setEmojiClickListener(upsetIv, R.drawable.ic_main_y_05, R.drawable.ic_main_s_05)
            }
            else -> Log.d("UserPref", "Unknown emoji: $emoji")
        }
    }

    private fun setEmojiClickListener(emojiView: ImageView, originalImageResId: Int, newImageResId: Int) {
        emojiView.setOnClickListener {
            // 현재 선택된 이모지가 있으면 원래 이미지로 복원
            currentSelectedEmoji?.setImageResource(getOriginalResId(currentSelectedEmoji!!))

            // 선택한 이모지의 이미지를 새로운 이미지로 변경
            emojiView.setImageResource(newImageResId)

            // 현재 선택된 이모지를 업데이트
            currentSelectedEmoji = emojiView
        }
    }

    private fun getOriginalResId(emojiView: ImageView): Int {
        return when (emojiView) {
            excitedIv -> R.drawable.ic_main_y_01
            happyIv -> R.drawable.ic_main_y_02
            normalIv -> R.drawable.ic_main_y_03
            sadIv -> R.drawable.ic_main_y_04
            upsetIv -> R.drawable.ic_main_y_05
            else -> R.drawable.ic_main_y_01 // 기본값
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentDialogTheme)
    }
}
