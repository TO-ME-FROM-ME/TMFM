package com.example.to_me_from_me.Signup

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SignupFinishFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signup_finish, container, false)

        val nextButton = view.findViewById<Button>(R.id.next_btn)


        val email = arguments?.getString("email")
        val pwd = arguments?.getString("pwd")
        val nickname = arguments?.getString("nickname")

        nextButton.setOnClickListener {
            val intent = Intent(requireContext(), SignupFinishActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("pwd", pwd)
            intent.putExtra("nickname", nickname)
            startActivity(intent)
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme2)
    }

    companion object {
        // Static method to create a new instance of SignupFinishFragment with arguments
        fun newInstance(email: String, pwd: String, nickname: String): SignupFinishFragment {
            val fragment = SignupFinishFragment()
            val args = Bundle()
            args.putString("email", email)
            args.putString("pwd", pwd)
            args.putString("nickname", nickname)
            fragment.arguments = args
            return fragment
        }
    }
}
