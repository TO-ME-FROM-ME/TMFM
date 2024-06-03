package com.example.to_me_from_me

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class StorageDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? { val view = inflater.inflate(R.layout.storage_dialog, container, false)

        // 다이얼로그의 닫기 버튼 설정
        val closeIv: ImageView = view.findViewById(R.id.close_iv)
        closeIv.setOnClickListener {
            dismiss()
        }

        // "저장하고 나가기" 버튼 클릭 이벤트 설정
        val saveBtn: Button = view.findViewById(R.id.save_btn)
        saveBtn.setOnClickListener {
            Toast.makeText(requireContext(), "임시저장 완료", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
        return view
    }
}
