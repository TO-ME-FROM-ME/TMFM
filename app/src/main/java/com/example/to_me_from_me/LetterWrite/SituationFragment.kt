package com.example.to_me_from_me.LetterWrite

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.to_me_from_me.Mypage.SharedViewModel
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SituationFragment : BottomSheetDialogFragment() {

    private lateinit var firestore: FirebaseFirestore
    private val sharedViewModel: ViewModel by activityViewModels()
    private val tag = "SituationFragment"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_situation, container, false)

        firestore = FirebaseFirestore.getInstance()

        val writeEditText = view.findViewById<EditText>(R.id.write_et)
        val charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)
        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)

        val mainColor = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        val defaultColor = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)


        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {
            val textValue = writeEditText.text.toString()
            sharedViewModel.setSituationText(textValue)

            val textLength = writeEditText.text.length
            val toastLayout = LayoutInflater.from(requireContext()).inflate(R.layout.toast, layout, false)
            val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

            val toastLayout2 = LayoutInflater.from(requireContext()).inflate(R.layout.toast_over, layout, false)
            val toastTv2 = toastLayout2.findViewById<TextView>(R.id.toast_tv)

            when {
                textLength < 10 -> {
                    showToast(toastLayout,writeEditText,700)
                    toastTv.text = "최소 10자 이상 작성해줘!"
                    writeEditText.background = ContextCompat.getDrawable(requireContext(),
                        R.drawable.solid_over_txt
                    )
                }

                textLength > 30 -> {
                    showToast2(toastLayout2,writeEditText,700)
                    toastTv2.text = "30자 이하로 작성해줘!"

                }

                else -> {

                    saveSituationToFirestore(textValue)

                    // 다음 Fragment화면으로 이동
                    val nextFragment = EmojiFragment()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, nextFragment)
                        .addToBackStack(tag)
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
                if (charCount >= 10 && charCount <= 30) {
                    nextButton.background = mainColor
                    writeEditText.background = ContextCompat.getDrawable(requireContext(),
                        R.drawable.solid_stroke_q
                    )

                } else {
                    nextButton.background = defaultColor
                }
            }
        })

        // 외부 영역 클릭하여 키보드 숨기기
        view.setOnTouchListener { _, event ->
            // 터치 이벤트가 발생한 뷰가 에디트텍스트가 아닌 경우 키보드 숨기기
            if (event.action == MotionEvent.ACTION_DOWN) {
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                // 터치 이벤트가 클릭인 경우 performClick 호출
                if (view.isClickable) {
                    view.performClick()
                }
            }
            false
        }


        return view
    }


    private fun showToast(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(requireContext())
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val yOffset = location[1] - 10  - layout.measuredHeight

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

    //30자 입력 토스트
    private fun showToast2(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(requireContext())
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val yOffset = location[1]- (-50) - layout.measuredHeight

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
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme).apply {
            setTitle("오늘 무슨 일이 있었어?")
        }
    }

    private fun saveSituationToFirestore(letterContent: String) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userDocumentRef = firestore.collection("users").document(user.uid)

            // 날짜 생성 (문서 ID로 사용)
            val currentDate = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(Date())

            // 저장할 데이터
            val situationData = hashMapOf(
                "situation" to letterContent,
                "date" to currentDate
            )

            // Firestore에 데이터 저장
            userDocumentRef.collection("letters")
                .document(currentDate)
                .set(situationData)
                .addOnSuccessListener {
                    // 성공 시 currentDate를 SharedViewModel에 저장
                    sharedViewModel.setCurrentDate(currentDate)
                }
        }
    }

}
