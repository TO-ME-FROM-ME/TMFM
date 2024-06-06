package com.example.to_me_from_me

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LetterFragment : BottomSheetDialogFragment() {

    private val sharedViewModel: ViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_letter, container, false)

        val reservBtn = view.findViewById<Button>(R.id.reserve_btn)
        val sendBtn = view.findViewById<Button>(R.id.send_btn)
        val letterTV = view.findViewById<TextView>(R.id.letter_tv)
        val combinedTextValue = arguments?.getString("combinedTextValue")
        letterTV.text = combinedTextValue

        val textView = view.findViewById<TextView>(R.id.user_situation_tv)
        sharedViewModel.situationText.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }

        val imageView = view.findViewById<ImageView>(R.id.user_emo_iv)

        sharedViewModel.selectedImageResId.observe(viewLifecycleOwner) { resId ->
            if (resId != null) {
                imageView.setImageResource(resId)
                imageView.visibility = View.VISIBLE
            }
        }


        reservBtn.setOnClickListener {
            val dialogFragment = CalendarDialogFragment()
            dialogFragment.show(parentFragmentManager, "CalendarDialogFragment")
        }

        sendBtn.setOnClickListener {
//            val intent = Intent(activity, MainActivity::class.java)
//            startActivity(intent)
//            activity?.finish() // Fragment에서 Activity를 종료

            val nextFragment = RecorderFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()
        }


        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme)

    }

}