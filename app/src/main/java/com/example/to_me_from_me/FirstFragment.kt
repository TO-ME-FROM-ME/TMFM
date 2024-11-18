package com.example.to_me_from_me


import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.MainAlarm.MainAlarmActivity

class FirstFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)


        val alarmIv = view.findViewById<ImageView>(R.id.alarm_iv)
        alarmIv.setOnClickListener {
            startActivity(Intent(activity, MainAlarmActivity::class.java))
        }

        val writeIv = view.findViewById<ImageView>(R.id.write_iv)
        writeIv.setOnClickListener {
            val dialogFragment = HomeDialogFragment()
            dialogFragment.show(parentFragmentManager, "HomeDialogFragment")

        }

        val mailIv = view.findViewById<ImageView>(R.id.mail_iv)
        mailIv.setOnClickListener {
            val dialogFragment2 = HomeDialogFragment2()
            dialogFragment2.show(parentFragmentManager, "HomeDialogFragment2")

        }
        return view
    }


}

