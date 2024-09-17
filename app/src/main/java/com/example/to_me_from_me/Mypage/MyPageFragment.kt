package com.example.to_me_from_me.Mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.LogoutDialogFragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R

class MyPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userProfile = view.findViewById<ImageView>(R.id.user_go)
        val userAlarm = view.findViewById<ImageView>(R.id.alarm_go)
        val userLogout = view.findViewById<ImageView>(R.id.logout_go)
        val userDeleteAcc = view.findViewById<ImageView>(R.id.deleteacc_go)

        userProfile.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }

        userAlarm.setOnClickListener {
            startActivity(Intent(activity, AlarmActivity::class.java))
        }

        userLogout.setOnClickListener {
            val dialogFragment = LogoutDialogFragment()
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialogFragment.show(parentFragmentManager, "LogoutDialogFragment")
        }

        userDeleteAcc.setOnClickListener {
            startActivity(Intent(activity, DeleteAccActivity::class.java))
        }
    }
}
