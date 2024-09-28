package com.example.to_me_from_me.Mailbox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Date

class MailBoxFragment: BottomSheetDialogFragment()  {

    private var selectedDate: Date? = null // Date 타입의 변수 선언

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_mailbox, container, false)

        val sendMail_ll = view.findViewById<LinearLayout>(R.id.send_ll)
        val sendMail = view.findViewById<LinearLayout>(R.id.send_mail)
        val receiveMail_ll = view.findViewById<LinearLayout>(R.id.receiv_ll)
        val receiveMail = view.findViewById<LinearLayout>(R.id.receiv_mail)
        val randomMail_ll = view.findViewById<LinearLayout>(R.id.random_ll)
        val randomMail = view.findViewById<LinearLayout>(R.id.random_mail)


        sendMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter","send")
            startActivity(intent)
        }

        receiveMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter","receive")
            startActivity(intent)
        }

        randomMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter","random")
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