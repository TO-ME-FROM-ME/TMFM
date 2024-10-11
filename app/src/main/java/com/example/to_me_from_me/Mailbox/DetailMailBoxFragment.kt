package com.example.to_me_from_me.Mailbox

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.LetterWrite.AdjectiveQ1Adapter
import com.example.to_me_from_me.LetterWrite.ButtonData
import com.example.to_me_from_me.LetterWrite.ViewModel
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DetailMailBoxFragment : BottomSheetDialogFragment() {

    private var selectedDate: Date? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    private lateinit var recyclerView : RecyclerView
    private lateinit var dateTv1: TextView
    private lateinit var dateTv2: TextView
    private lateinit var dateIv: ImageView

    private lateinit var situationTv: TextView
    private lateinit var emojiIv: ImageView
    private lateinit var ad1Tv: TextView
    private lateinit var ad2Tv: TextView
    private lateinit var letterTv: TextView

    private lateinit var charCountTextView : TextView
    private var dateVisible: Boolean = false // date_text 가시성 제어 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_mailbox, container, false)

        // Bundle로 전달된 selectedDate 값을 Long으로 가져와 Date로 변환
        arguments?.let { bundle ->
            val selectedDateMillis = bundle.getLong("selectedDate", -1L)
            if (selectedDateMillis != -1L) {
                selectedDate = Date(selectedDateMillis) // Long을 Date로 변환
            }
        }
        Log.d(
            "DetailLetterLoad", "selectedDate: $selectedDate"
        )


        dateTv1 = view.findViewById(R.id.date1_tv)
        dateTv2 = view.findViewById(R.id.date2_tv)
        dateIv = view.findViewById(R.id.date_iv)
        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)


        situationTv = view.findViewById(R.id.user_situation_tv)
        emojiIv = view.findViewById(R.id.user_emoji_iv)
        letterTv  = view.findViewById(R.id.letter_tv)
        ad1Tv = view.findViewById(R.id.ad1_tv)
        ad2Tv = view.findViewById(R.id.ad2_tv)


        letterLoad()


        charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)

        // dateVisible에 따라 date_text 가시성 설정
        //updateDateVisibility()

        return view
    }


    private fun letterLoad() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 선택된 날짜를 Date 형식으로 가져옴
        val targetDate = selectedDate

        if (uid != null && targetDate != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->

                    if (!documents.isEmpty) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        for (document in documents) {
                            val dateString = document.getString("date") // Firebase에 저장된 날짜 String 값
                            val firebaseDate =
                                dateString?.let { dateFormat.parse(it) } // String -> Date 변환
                            val newDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())


                            // 선택된 날짜와 Firestore에서 가져온 날짜가 일치하는지 비교
                            if (firebaseDate != null && isSameDate(firebaseDate, targetDate)) {
                                // 문서에서 데이터를 가져와 UI에 표시
                                val emoji = document.getString("emoji")
                                val situation = document.getString("situation")
                                val ad1 = document.getString("ad1")
                                val ad2 = document.getString("ad2")
                                val letter = document.getString("letter")
                                val charCount = (letter?.length ?: 0) + 2

                                if (emoji != null) {
                                    emojiIv.setImageResource(getEmojiDrawable(emoji))
                                    situationTv.text = situation.toString()
                                    ad1Tv.text = ad1.toString()
                                    ad2Tv.text = ad2.toString()
                                    //letterTv.text = letter.toString()
                                    dateTv1.text = firebaseDate?.let { newDateFormat.format(it) }
                                    charCountTextView.text = "$charCount"

                                    val letterLines = letter?.split("\n")?.toMutableList() // letter를 줄바꿈 기준으로 나눔
                                    if (!letterLines.isNullOrEmpty()) {
                                        letterLines[0] = letterLines[0] + "에게" // 첫 줄에 "에게" 추가
                                    }
                                    val modifiedLetter = letterLines?.joinToString("\n") // 다시 문자열로 결합
                                    letterTv.text = modifiedLetter

                                } else {
                                    Log.d("letterLoad", "보낸 편지가 없습니다.")
                                }
                                break
                            }
                        }
                    } else {
                        Log.d("letterLoad", "편지 데이터가 없습니다.")
                    }
                }
        }
    }

    // 두 날짜가 같은 날인지 확인하는 함수 (시간 제외)
    private fun isSameDate(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date1

        val calendar2 = Calendar.getInstance()
        calendar2.time = date2

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }

    private fun getEmojiDrawable(emoji: String): Int {
        return when (emoji) {
            "excited_s" -> R.drawable.excited_s  // excited_s에 해당하는 drawable
            "happy_s" -> R.drawable.happy_s      // happy_s에 해당하는 drawable
            "normal_s" -> R.drawable.normal_s    // normal_s에 해당하는 drawable
            "upset_s" -> R.drawable.upset_s          // upset_s에 해당하는 drawable
            "angry_s" -> R.drawable.angry_s       // upset_s에 해당하는 drawable
            else -> 0 // 기본 이미지
        }
    }

    private fun updateDateVisibility() {
        val visibility = if (dateVisible) View.VISIBLE else View.GONE
        dateTv1.visibility = visibility
        dateTv2.visibility = visibility
        dateIv.visibility = visibility
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme)

    }
}