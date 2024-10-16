package com.example.to_me_from_me.CoachMark

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R

class CoachMarkDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.coachmark_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeButton = view.findViewById<ImageView>(R.id.close_iv)
        closeButton.setOnClickListener {
            dismiss()
        }

        // '계속 보기' 버튼
        val saveButton = view.findViewById<Button>(R.id.save_btn)
        saveButton.setOnClickListener {
            dismiss()
        }

        // '그래도 나갈래' 버튼
        val cancelButton = view.findViewById<Button>(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all)
    }
}