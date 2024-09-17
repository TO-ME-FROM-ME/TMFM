package com.example.to_me_from_me.LetterWrite

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SendFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_send, container, false)


        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {
            //MainActivity 이동
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        return view

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme2)

    }
}