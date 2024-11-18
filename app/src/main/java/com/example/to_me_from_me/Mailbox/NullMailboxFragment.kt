package com.example.to_me_from_me.Mailbox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.to_me_from_me.LetterWrite.LetterFragment
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NullMailboxFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_null_mailbox, container, false)



        val letterBtn = view.findViewById<Button>(R.id.next_btn)
        letterBtn.setOnClickListener {
            val intent = Intent(activity, WriteLetterActivity::class.java)
            startActivity(intent)
        }
        return view
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme3)
        dialog.setCanceledOnTouchOutside(true)  // 배경 클릭 시 닫힘
        return dialog
    }
}