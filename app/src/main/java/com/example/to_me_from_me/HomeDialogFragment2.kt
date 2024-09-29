package com.example.to_me_from_me

import android.content.Context
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

        // SharedPreferences에서 이메일 불러오기
        val sharedPref = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
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
                            val emoji = document.getString("emoji")
                            titleTv.text = "받고 싶은 편지의 감정을 선택해줘!"
                            updateEmojiView(emoji)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("UserPref", "Error getting documents: ", exception)
                }
        }else {
            Log.d("UserPref", "저장된 이메일이 없습니다.")
        }
        return view
    }

    private fun updateEmojiView(emoji: Int?) {

        // emoji 값에 따라 해당 ImageView 보이기
        when (emoji) {
           1 -> excitedIv.setImageDrawable(R.drawable.excited)
            "happy" -> happyIv.visibility = View.VISIBLE
            "normal" -> normalIv.visibility = View.VISIBLE
            "sad" -> sadIv.visibility = View.VISIBLE
            "upset" -> upsetIv.visibility = View.VISIBLE
            else -> Log.d("UserPref", "Unknown emoji: $emoji")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 추가 로직 작성 가능
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentDialogTheme)


    }
}
