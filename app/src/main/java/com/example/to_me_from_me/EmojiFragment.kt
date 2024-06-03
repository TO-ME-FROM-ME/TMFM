package com.example.to_me_from_me

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiFragment : BottomSheetDialogFragment() {

    private var activeButton: ImageView? = null
    private var isImageSelected = false // 이미지가 선택되었는지 여부를 저장하는 변수
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_emoji, container, false)

        val textValue = arguments?.getString("textValue")
        val textView = view.findViewById<TextView>(R.id.user_situation_tv)
        textView.text = textValue


        val excitedButton = view.findViewById<ImageView>(R.id.excited_btn)
        val happyButton = view.findViewById<ImageView>(R.id.happy_btn)
        val normalButton = view.findViewById<ImageView>(R.id.normal_btn)
        val upsetButton = view.findViewById<ImageView>(R.id.upset_btn)
        val angryButton = view.findViewById<ImageView>(R.id.angry_btn)

        val buttons = listOf(excitedButton, happyButton, normalButton, upsetButton, angryButton)
        val activeDrawables = listOf(R.drawable.excited_s, R.drawable.happy_s, R.drawable.normal_s, R.drawable.upset_s, R.drawable.angry_s)
        val inactiveDrawables = listOf(R.drawable.excited, R.drawable.happy, R.drawable.normal, R.drawable.upset, R.drawable.angry)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                setActiveButton(button, activeDrawables[index], inactiveDrawables)
            }
        }

        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {
            if (isImageSelected) { // 이미지가 선택된 경우에만 다음 단계로 이동
                nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
                val nextFragment = AdjectiveFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, nextFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                //Toast.makeText(requireContext(), "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun setActiveButton(button: ImageView, activeDrawable: Int, inactiveDrawables: List<Int>) {
        activeButton?.let {
            val index = listOf(R.id.excited_btn, R.id.happy_btn, R.id.normal_btn, R.id.upset_btn, R.id.angry_btn).indexOf(it.id)
            if (index != -1) {
                it.setBackgroundResource(inactiveDrawables[index])
            }
        }
        button.setBackgroundResource(activeDrawable)
        activeButton = button
        isImageSelected = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme).apply {
            setTitle("그때 기분은 어땠어?")
        }
    }
}