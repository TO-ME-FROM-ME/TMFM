package com.example.to_me_from_me

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.RelativeSizeSpan
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
        val letterTV = view.findViewById<EditText>(R.id.letter_tv)
        val combinedTextValue = arguments?.getString("combinedTextValue")
        val nicknameText = "사랑하는 nickname에게"
        val letterFull = "$nicknameText\n\n$combinedTextValue"

        // SpannableString을 생성합니다.
        val spannableString = SpannableString(letterFull)

// prefixText의 길이를 계산하여 RelativeSizeSpan을 적용합니다.
        val prefixLength = nicknameText.length
        spannableString.setSpan(
            RelativeSizeSpan(1.3f), // 글자 크기를 1.5배로 설정
            0, // 시작 인덱스
            prefixLength, // 끝 인덱스
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// EditText에 결합된 문자열을 설정합니다.
        letterTV.setText(spannableString)

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

        val charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)
        val charCount = combinedTextValue?.length ?: 0
        charCountTextView.text = "$charCount"


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

// 실시간 글자 수
        letterTV.addTextChangedListener(object  : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount"
            }
        })

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme)

    }

}