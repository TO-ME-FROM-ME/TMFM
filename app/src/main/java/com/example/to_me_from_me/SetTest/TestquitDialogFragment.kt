package com.example.to_me_from_me.SetTest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.R

class TestquitDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.testquit_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeButton = view.findViewById<ImageView>(R.id.close_iv)
        closeButton.setOnClickListener {
            dismiss()
        }

        // 저장 버튼 클릭 리스너 설정
        val saveButton = view.findViewById<Button>(R.id.save_btn)
        saveButton.setOnClickListener {
            dismiss()
        }
    }


}