package com.example.to_me_from_me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

class SaveDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        val view = inflater.inflate(R.layout.save_dialog, container, false)

        // 다이얼로그의 닫기 버튼 설정
        val closeIv: ImageView = view.findViewById(R.id.close_iv)
        closeIv.setOnClickListener {
            dismiss()
        }

        val new_btn: Button = view.findViewById(R.id.new_btn)
        val cwrite_btn: Button = view.findViewById(R.id.cwrite_btn)


        cwrite_btn.setOnClickListener {
            // 저장된 Fragment화면이 있다면 보여주기
        }


        return view
    }
}

