package com.example.to_me_from_me

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.whenCreated
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SituationFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_situation, container, false)
        val writeEditText = view.findViewById<EditText>(R.id.write_et)
        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)


        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {

            val textLength = writeEditText.text.length
            val toastLayout = LayoutInflater.from(requireContext()).inflate(R.layout.toast, layout, false)
            val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

            when {
                textLength < 10 -> {
                    showToast(toastLayout,writeEditText)
                    toastTv.text = "최소 10자 이상 작성해줘!"
                }

                textLength > 30 -> {
                    showToast(toastLayout,writeEditText)
                    toastTv.text = "30자 이하로 작성해줘!"
                }

                else -> {

                    // 다음 Fragment화면으로 이동
                    val nextFragment = EmojiFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }

            }
        }



        return view
    }

    private fun showToast(layout: View, writeEditText: EditText) {
        val toastLayout = LayoutInflater.from(requireContext()).inflate(R.layout.toast, null, false)
        val toast = Toast(requireContext())
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val yOffset = location[1] - writeEditText.height - toastLayout.measuredHeight


        toast.setGravity(Gravity.TOP or Gravity.END, location[0], yOffset)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme).apply {
            setTitle("오늘 무슨 일이 있었어?")
        }
    }

}