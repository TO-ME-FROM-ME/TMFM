package com.example.to_me_from_me.MainAlarm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.R

class MainNoAlarmFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_noalarm, container, false)


        val okButton = view.findViewById<Button>(R.id.next_btn)
        okButton.setOnClickListener {
            startActivity(Intent(activity, WriteLetterActivity::class.java))
        }
        return view
    }

}