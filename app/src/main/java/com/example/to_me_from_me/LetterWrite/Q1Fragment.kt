package com.example.to_me_from_me.LetterWrite

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
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
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Q1Fragment : BottomSheetDialogFragment() {

    private val sharedViewModel: ViewModel by activityViewModels()
    // 선택된 버튼의 정보를 저장하기 위한 변수
    private var selectedButtonText: String? = null
    private val tag = "Q1Fragment"

    private lateinit var dateTv1: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_q1, container, false)

        val textView = view.findViewById<TextView>(R.id.user_situation_tv)
        val adjective1 = view.findViewById<TextView>(R.id.adjective1)
        val adjective2 = view.findViewById<TextView>(R.id.adjective2)

        dateTv1 = view.findViewById(R.id.date1_tv)

        // 현재 날짜를 가져와 형식 지정
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val formattedDate = currentDate.format(formatter)

        dateTv1.text = formattedDate

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


        val writeEditText = view.findViewById<EditText>(R.id.write_et)
        val charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)
        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)

        val mainColor = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        val defaultColor = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)

        // 전달된 형용사 받아오기
        val selectedButtonTexts = arguments?.getStringArrayList("selectedButtonTexts")
        if (!selectedButtonTexts.isNullOrEmpty()) {
            // 선택된 텍스트를 처리
            adjective1.text = selectedButtonTexts.joinToString(", ")
            adjective2.text = selectedButtonTexts.joinToString(", ")

            // 버튼 데이터 리스트 생성
            val buttonDataList = selectedButtonTexts.map { ButtonData(it) }
            Log.d("Q1Fragment", "buttonDataList : $buttonDataList")

            // RecyclerView 초기화
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_buttons)
            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = AdjectiveQ1Adapter(requireContext(), buttonDataList) { buttonData ->
                Log.d("Q1Fragment", "Clicked Button 1 : ${buttonData.buttonText}")
                selectedButtonText = buttonData.buttonText
            }
        }

        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {

            val q1textValue = writeEditText.text.toString()
            val textLength = writeEditText.text.length
            val toastLayout = LayoutInflater.from(requireContext()).inflate(R.layout.toast, layout, false)
            val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)


            val toastLayout2 = LayoutInflater.from(requireContext()).inflate(R.layout.toast_over, layout, false)
            val toastTv2 = toastLayout2.findViewById<TextView>(R.id.toast_tv)

            when {
                textLength < 0 -> {
                    showToast(toastLayout,writeEditText,700)
                    toastTv.text = "최소 50자 이상 작성해줘!"
                    writeEditText.background = ContextCompat.getDrawable(requireContext(),
                        R.drawable.solid_over_txt
                    )
                }

                textLength > 150 -> {
                    showToast(toastLayout,writeEditText,700)
                    toastTv.text = " 150자 이하로 작성해줘!"

                }

                else -> {

                    saveLetterToFirestore(writeEditText.text.toString())
                    // 다음 Fragment화면으로 이동
                    val nextFragment = Q2Fragment()

                    val bundle = Bundle()
                    bundle.putString("q1textValue", q1textValue)
                    bundle.putStringArrayList("selectedButtonTexts", ArrayList(selectedButtonTexts))
                    nextFragment.arguments = bundle

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
                if (charCount >= 0 && charCount <= 150) {
                    nextButton.background = mainColor
                    writeEditText.background = ContextCompat.getDrawable(requireContext(),
                        R.drawable.solid_stroke_q
                    )
                } else {
                    nextButton.background = defaultColor
                }
            }
        })

        val shortTextLayout = view.findViewById<LinearLayout>(R.id.textshort)
        val longTextLayout = view.findViewById<LinearLayout>(R.id.textlong)

        val textContent1 = adjective1.text.toString()

        if (textContent1.length < 9) {
            // 텍스트 길이가 짧으면 short_text_layout을 보이게 함
            shortTextLayout.visibility = View.VISIBLE
        } else {
            // 텍스트 길이가 길면 long_text_layout을 보이게 함
            longTextLayout.visibility = View.VISIBLE
        }

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
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme).apply {
            setTitle("1. 왜 감정1, 감정2 감정을 느꼈어?")
        }
    }

    fun hideKeyboard(activity: Activity){
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.applicationWindowToken, 0)
    }

    private fun saveLetterToFirestore(letterContent: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val currentDate = sharedViewModel.currentDate.value

        if (user != null && currentDate != null) {
            val userDocumentRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .collection("letters")
                .document(currentDate)

            val letterData = mapOf<String, Any>(
                "q1" to letterContent
            )
            userDocumentRef.update(letterData)
        }
    }

}
