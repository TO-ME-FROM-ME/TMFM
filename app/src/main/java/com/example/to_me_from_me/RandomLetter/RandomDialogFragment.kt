package com.example.to_me_from_me.RandomLetter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.Mailbox.DetailMailBoxActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RandomDialogFragment : DialogFragment() {
    private lateinit var emojiString: String

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.random_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        // arguments에서 selectedEmoji 가져오기
        arguments?.let {
            emojiString = it.getString("selectedEmoji", "")
        }

        val closeButton = view.findViewById<ImageView>(R.id.close_iv)
        closeButton.setOnClickListener {
            dismiss()
        }

        // 저장 버튼 클릭 리스너 설정
        val okButton = view.findViewById<Button>(R.id.ok_btn)
        okButton.setOnClickListener {
            // 현재 사용자 ID 가져오기
            val uid = auth.currentUser?.uid

            val intent = Intent(requireContext(), DetailMailBoxActivity::class.java).apply {
                putExtra("selectedEmoji", emojiString)
                putExtra("letter", "random2")
            }
            startActivity(intent) // Activity 시작
            dismiss() // 다이얼로그 닫기
        }
    }


    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all)
    }

}
