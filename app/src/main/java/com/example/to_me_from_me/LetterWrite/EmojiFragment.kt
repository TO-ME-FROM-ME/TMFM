package com.example.to_me_from_me.LetterWrite

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiFragment : BottomSheetDialogFragment() {

    private val sharedViewModel: ViewModel by activityViewModels()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var activeButton: ImageView? = null
    private var isImageSelected = false // 이미지가 선택되었는지 여부를 저장하는 변수
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_emoji, container, false)

        val backButton: ImageView? = requireActivity().findViewById(R.id.back_iv)
        backButton?.visibility = View.VISIBLE

        val textView = view.findViewById<TextView>(R.id.user_situation_tv)
        sharedViewModel.situationText.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }

        val emojiView = view.findViewById<ImageView>(R.id.user_emo_iv)
        val nextButton = view.findViewById<Button>(R.id.next_btn)
        val excitedButton = view.findViewById<ImageView>(R.id.excited_btn)
        val happyButton = view.findViewById<ImageView>(R.id.happy_btn)
        val normalButton = view.findViewById<ImageView>(R.id.normal_btn)
        val upsetButton = view.findViewById<ImageView>(R.id.upset_btn)
        val angryButton = view.findViewById<ImageView>(R.id.angry_btn)

        val buttons = listOf(excitedButton, happyButton, normalButton, upsetButton, angryButton)
        val activeDrawables = listOf(
            R.drawable.excited_s,
            R.drawable.happy_s,
            R.drawable.normal_s,
            R.drawable.upset_s,
            R.drawable.angry_s
        )
        val inactiveDrawables = listOf(
            R.drawable.excited,
            R.drawable.happy,
            R.drawable.normal,
            R.drawable.upset,
            R.drawable.angry
        )

        val isFromCWriteBtn = arguments?.getBoolean("isFromCWriteBtn", false) ?: false
        if (isFromCWriteBtn) {
            val emojiName = arguments?.getString("emoji")
            if (emojiName != null) {
                val emojiResId = getResourceIdByName(emojiName)
                if (emojiResId != 0) { // 유효한 리소스 ID인지 확인
                    val index = when (emojiResId) {
                        R.drawable.excited_s -> 0
                        R.drawable.happy_s -> 1
                        R.drawable.normal_s -> 2
                        R.drawable.upset_s -> 3
                        R.drawable.angry_s -> 4
                        else -> -1 // 유효하지 않은 경우
                    }
                    if (index != -1) {
                        setActiveButton(buttons[index], activeDrawables[index], inactiveDrawables) // buttons[index] 사용
                    }

                    sharedViewModel.setSelectedImageResId(emojiResId)
                    emojiView.setImageResource(emojiResId)
                    emojiView.visibility = View.VISIBLE
                    isImageSelected = true
                    nextButton.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)

                    when (emojiResId) {
                        R.drawable.excited_s -> setActiveButton(
                            excitedButton,
                            activeDrawables[0],
                            inactiveDrawables
                        )

                        R.drawable.happy_s -> setActiveButton(
                            happyButton,
                            activeDrawables[1],
                            inactiveDrawables
                        )

                        R.drawable.normal_s -> setActiveButton(
                            normalButton,
                            activeDrawables[2],
                            inactiveDrawables
                        )

                        R.drawable.upset_s -> setActiveButton(
                            upsetButton,
                            activeDrawables[3],
                            inactiveDrawables
                        )

                        R.drawable.angry_s -> setActiveButton(
                            angryButton,
                            activeDrawables[4],
                            inactiveDrawables
                        )
                    }
                }
            }
        }

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                setActiveButton(button, activeDrawables[index], inactiveDrawables)
                sharedViewModel.setSelectedImageResId(activeDrawables[index])
                emojiView.setImageResource(activeDrawables[index])
                emojiView.visibility = View.VISIBLE
                nextButton.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.solid_no_main
                )
            }
        }

        nextButton.setOnClickListener {
            if (isImageSelected) { // 이미지가 선택된 경우에만 다음 단계로 이동
                saveEmojiToFirestore()

                if (isFromCWriteBtn) {
                    val ad1 = arguments?.getString("ad1")
                    val ad2 = arguments?.getString("ad2")
                    val q1 = arguments?.getString("q1")
                    val q2 = arguments?.getString("q2")
                    val q3 = arguments?.getString("q3")
                    val letter = arguments?.getString("letter")

                    val adjectiveFragment = AdjectiveFragment().apply {
                        arguments = Bundle().apply {
                            putString("ad1", ad1)
                            putString("ad2", ad2)
                            putString("q1", q1)
                            putString("q2", q2)
                            putString("q3", q3)
                            putString("letter", letter)
                            putBoolean("isFromCWriteBtn", true)
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, adjectiveFragment)
                        .addToBackStack(tag)
                        .commit()
                } else {

                    val nextFragment = AdjectiveFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
        return view
    }

    private fun getResourceIdByName(resourceName: String): Int {
        return resources.getIdentifier(resourceName, "drawable", requireContext().packageName)
    }


    private fun saveEmojiToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userDocumentRef = firestore.collection("users").document(user.uid)
            val currentDate = sharedViewModel.currentDate.value
            val selectedEmojiResId = sharedViewModel.selectedImageResId.value

            if (user != null && currentDate != null && selectedEmojiResId != null) {
                val userDocumentRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .collection("letters")
                    .document(currentDate)

                // 리소스 ID를 문자열로 변환 (리소스 이름)
                val selectedEmojiResName = getResourceNameById(selectedEmojiResId)

                // Firestore에 저장할 이모지 데이터 (문자열)
                val emojiData = mapOf<String, Any>(
                    "emoji" to selectedEmojiResName
                )

                // 기존 문서에 emoji 필드 업데이트
                userDocumentRef.update(emojiData)
            }
        }
    }

    // 리소스 ID를 리소스 이름으로 변환하는 함수
    private fun getResourceNameById(resourceId: Int): String {
        return resources.getResourceEntryName(resourceId)
    }


    private fun setActiveButton(button: ImageView, activeDrawable: Int, inactiveDrawables: List<Int>) {
        activeButton?.let {
            val index = listOf(
                R.id.excited_btn,
                R.id.happy_btn,
                R.id.normal_btn,
                R.id.upset_btn,
                R.id.angry_btn
            ).indexOf(it.id)
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