package com.example.to_me_from_me

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Q1Fragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_q1, container, false)

        val writeEditText = view.findViewById<EditText>(R.id.write_et)
        val charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)
        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)

        val mainColor = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        val defaultColor = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)

        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {

            val q1textValue = writeEditText.text.toString()
            val textLength = writeEditText.text.length
            val toastLayout = LayoutInflater.from(requireContext()).inflate(R.layout.toast, layout, false)
            val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

            when {
                textLength < 50 -> {
                    showToast(toastLayout,writeEditText)
                    toastTv.text = "최소 50자 이상 작성해줘!"
                }

                textLength > 150 -> {
                    showToast(toastLayout,writeEditText)
                    toastTv.text = "150자 이하로 작성해줘!"
                }

                else -> {

                    // 다음 Fragment화면으로 이동
                    val nextFragment = Q2Fragment()

                    val bundle = Bundle()
                    bundle.putString("textValue", q1textValue)
                    nextFragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }

            }
        }

        // 실시간 글자 수
        writeEditText.addTextChangedListener(object  : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount"

                // 버튼 배경 변경 로직
                if (charCount >= 50 && charCount <= 150) {
                    nextButton.background = mainColor
                } else {
                    nextButton.background = defaultColor
                }
            }
        })

        return view
    }


    private fun showToast(layout: View, writeEditText: EditText) {
        val toastLayout = LayoutInflater.from(requireContext()).inflate(R.layout.toast, null, false)
        val toast = Toast(requireContext())
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val yOffset = location[1] - 170 - toastLayout.measuredHeight


        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme).apply {
            setTitle("1. 왜 두려운, 불안한 감정을 느꼈어?")
        }
    }

    fun hideKeyboard(activity: Activity){
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.applicationWindowToken, 0)
    }

}
