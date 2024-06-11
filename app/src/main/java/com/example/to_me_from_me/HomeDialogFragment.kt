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
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class HomeDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        // 다이얼로그 위치
        dialog?.window?.apply {
            val params = attributes
            params.x = 80
            params.y = -200
            attributes = params
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? { val view = inflater.inflate(R.layout.home_dialog, container, false)

        // 편지쓰기 버튼
        val writeMailIv: RelativeLayout = view.findViewById(R.id.writemail_iv)
        writeMailIv.setOnClickListener {
            val intent = Intent(activity, WriteLetterActivity::class.java)
            startActivity(intent)
        }

        // 편지함 버튼
        val mailBoxIv: RelativeLayout = view.findViewById(R.id.mailbox_iv)
        mailBoxIv.setOnClickListener {
            Toast.makeText(requireContext(), "편지함 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentDialogTheme)
    }

}
