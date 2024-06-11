package com.example.to_me_from_me

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        // 형용사 버튼 받아오는 부분
        val selectedButtonTexts = arguments?.getStringArrayList("selectedButtonTexts")
        val buttonDataList = selectedButtonTexts?.map { ButtonData(it) } ?: emptyList()
        Log.d("Buttondata", "Letter buttonDataList : $buttonDataList")

        // RecyclerView 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_buttons)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = AdjectiveQ1Adapter(requireContext(), buttonDataList) { buttonData ->
        }


        sharedViewModel.selectedImageResId.observe(viewLifecycleOwner) { resId ->
            if (resId != null) {
                imageView.setImageResource(resId)
                imageView.visibility = View.VISIBLE
            }
        }

        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)
        val charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)
        val charCount = combinedTextValue?.length ?: 0
        charCountTextView.text = "$charCount"


        reservBtn.setOnClickListener {
            val dialogFragment = CalendarDialogFragment()
            dialogFragment.show(parentFragmentManager, "CalendarDialogFragment")
        }

        sendBtn.setOnClickListener {

            val textLength = letterTV.text.length
            val toastLayout = LayoutInflater.from(requireContext()).inflate(R.layout.toast, layout, false)
            val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

            when {
                textLength < 150 -> {
                    showToast(toastLayout,letterTV,700)
                    toastTv.text = "최소 150자 이상 작성해줘!"
                    letterTV.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_over_txt)
                }

                else -> {

                    val nextFragment = RecorderFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }

            }
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


    private fun showToast(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(requireContext())
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val yOffset = location[1] - 20 - layout.measuredHeight

        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.view = layout

        val handler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()

        handler.post(object : Runnable {
            override fun run() {
                if (System.currentTimeMillis() - startTime < duration) {
                    toast.show()
                    handler.postDelayed(this, 700)
                } else {
                    toast.cancel()
                }
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme)

    }

}