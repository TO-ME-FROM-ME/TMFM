package com.example.to_me_from_me.LetterWrite

import android.app.Dialog
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

class Q3Fragment : BottomSheetDialogFragment() {

    private val sharedViewModel: ViewModel by activityViewModels()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nicknameTextView: TextView

    private lateinit var dateTv1: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_q3, container, false)

        val textView = view.findViewById<TextView>(R.id.user_situation_tv)

        sharedViewModel.situationText.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }

        dateTv1 = view.findViewById(R.id.date1_tv)

        // 현재 날짜를 가져와 형식 지정
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val formattedDate = currentDate.format(formatter)

        dateTv1.text = formattedDate

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 닉네임을 표시할 TextView 초기화
        nicknameTextView = view.findViewById(R.id.nickname_tv)

        // Firebase에서 닉네임을 불러와 설정
        loadNickname()


        val imageView = view.findViewById<ImageView>(R.id.user_emo_iv)

        sharedViewModel.selectedImageResId.observe(viewLifecycleOwner) { resId ->
            if (resId != null) {
                imageView.setImageResource(resId)
                imageView.visibility = View.VISIBLE
            }
        }

        val q1TextValue = arguments?.getString("q1TextValue")
        val q2TextValue = arguments?.getString("q2TextValue")

        val writeEditText = view.findViewById<EditText>(R.id.write_et)
        val charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)
        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)

        val mainColor = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        val defaultColor = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)

        // 형용사 버튼 받아오는 부분
        val selectedButtonTexts = arguments?.getStringArrayList("selectedButtonTexts")
        val buttonDataList = selectedButtonTexts?.map { ButtonData(it) } ?: emptyList()
        Log.d("Buttondata", "Q3Fragment buttonDataList : $buttonDataList")

        // RecyclerView 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_buttons)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = AdjectiveQ1Adapter(requireContext(), buttonDataList) { buttonData ->
        }




        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {

            val textLength = writeEditText.text.length
            val toastLayout = LayoutInflater.from(requireContext()).inflate(R.layout.toast, layout, false)
            val toastTv = toastLayout.findViewById<TextView>(R.id.toast_tv)

            when {
                textLength < 50 -> {
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
                    val q3TextValue = writeEditText.text.toString()

                    val combinedTextValue = "$q1TextValue\n$q2TextValue\n$q3TextValue"

                    val nickname = nicknameTextView.text.toString()


                    val nextFragment = LetterFragment().apply {
                        val bundle = Bundle().apply {
                            putString("combinedTextValue", combinedTextValue)
                            putStringArrayList("selectedButtonTexts", ArrayList(selectedButtonTexts))
                            putString("nickname", nickname)
                        }

                        arguments = bundle
                    }


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
                    writeEditText.background = ContextCompat.getDrawable(requireContext(),
                        R.drawable.solid_stroke_q
                    )
                } else {
                    nextButton.background = defaultColor
                }
            }
        })



        return view
    }

    private fun loadNickname() {
        val user = auth.currentUser
        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nickname = document.getString("nickname") ?: "우리 동생" // 기본 닉네임 설정
                    //updateLetterTextWithNickname(nickname)
                    nicknameTextView.text = nickname
                } else {
                    Log.d("LetterFragment", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("LetterFragment", "get failed with ", exception)
            }
        }
    }



    private fun showToast(layout: View, writeEditText: EditText, duration: Int) {
        val toast = Toast(requireContext())
        val location = IntArray(2)
        writeEditText.getLocationOnScreen(location)
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val yOffset = location[1] - layout.measuredHeight

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
            setTitle("1. 왜 두려운, 불안한 감정을 느꼈어?")
        }
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
                "q3" to letterContent
            )
            userDocumentRef.update(letterData)
        }
    }

}