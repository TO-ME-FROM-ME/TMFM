package com.example.to_me_from_me.LetterWrite

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class LetterFragment : BottomSheetDialogFragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val sharedViewModel: ViewModel by activityViewModels()
    private lateinit var nicknameText : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_letter, container, false)

        firestore = FirebaseFirestore.getInstance()

        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)
        val reservBtn = view.findViewById<Button>(R.id.reserve_btn)
        val sendBtn = view.findViewById<Button>(R.id.send_btn)
        val letterTV = view.findViewById<EditText>(R.id.letter_tv)
        //val nicknameText = "사랑하는 우리동생"
        val combinedTextValue = arguments?.getString("combinedTextValue")

        val nickname = arguments?.getString("nickname") ?: "우리 동생"  // 기본값 설정
        
        val letterFull = "사랑하는 $nickname\n\n$combinedTextValue"

        // SpannableString을 생성합니다.
        val spannableString = SpannableString(letterFull)

        // prefixText의 길이를 계산하여 RelativeSizeSpan을 적용합니다.
        val prefixLength = "사랑하는 $nickname".length
        spannableString.setSpan(
            RelativeSizeSpan(1.3f), // 글자 크기를 1.5배로 설정
            0, // 시작 인덱스
            prefixLength, // 끝 인덱스
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val toastLayout =
            LayoutInflater.from(requireContext()).inflate(R.layout.toast, layout, false)
        val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

        //토스트 메세지 생성
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                toastTv.text = "수정하고 싶으면 클릭해줘!"
                showToast(toastLayout, letterTV, 2000) // 토스트 메시지 표시 (2초 동안)

                // 리스너 제거
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

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
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = AdjectiveQ1Adapter(requireContext(), buttonDataList) { buttonData ->
        }


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
            saveLetterToFirestore(letterTV.text.toString())
        }

        sendBtn.setOnClickListener {

            val textLength = letterTV.text.length

            when {
                textLength < 150 -> {
                    showToast(toastLayout, letterTV, 700)
                    toastTv.text = "최소 150자 이상 작성해줘!"
                    letterTV.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.solid_over_txt
                    )
                }

                textLength > 500 -> {
                    showToast(toastLayout, letterTV, 700)
                    toastTv.text = "500자 이하로 작성해줘!"
                    letterTV.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.solid_over_txt
                    )
                }

                else -> {

                    saveLetterToFirestore(letterTV.text.toString())

                    val nextFragment = RecorderFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }

            }
        }


// 실시간 글자 수
        letterTV.addTextChangedListener(object : TextWatcher {
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
        val yOffset = location[1] - 30 - layout.measuredHeight

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


    private fun saveLetterToFirestore(letterContent: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val currentDate = sharedViewModel.currentDate.value  // SharedViewModel에서 현재 문서 ID를 가져옴

        if (user != null && currentDate != null) {
            val userDocumentRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .collection("letters")
                .document(currentDate)

            val letterData = mapOf<String, Any>(
                "letter" to letterContent
            )
            userDocumentRef.update(letterData)
        }
    }
}