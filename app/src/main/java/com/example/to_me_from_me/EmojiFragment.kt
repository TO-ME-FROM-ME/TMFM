package com.example.to_me_from_me

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiFragment : BottomSheetDialogFragment() {

    private var activeButton: ImageView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_emoji, container, false)


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
            val nextFragment = AdjectiveFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()
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
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme).apply {
            setTitle("그때 기분은 어땠어?")
        }
    }
}