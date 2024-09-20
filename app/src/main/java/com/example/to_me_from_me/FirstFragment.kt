package com.example.to_me_from_me


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.Mailbox.MailboxActivity
import com.example.to_me_from_me.Signup.SignupEmailActivity

class FirstFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)



        val writeIv = view.findViewById<ImageView>(R.id.write_iv)
        writeIv.setOnClickListener {
            val dialogFragment = HomeDialogFragment()
            dialogFragment.show(parentFragmentManager, "HomeDialogFragment")

        }
        return view
    }


}

