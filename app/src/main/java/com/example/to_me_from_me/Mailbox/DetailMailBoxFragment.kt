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
import androidx.core.content.ContextCompat
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
    private val mailboxViewModel: MailboxViewModel by activityViewModels()

    private var selectedDate: Date? = null
    private var selectedEmoji: String? = null
    private var letter : String? = null


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
            letter = bundle.getString("letter")
            Log.d("letter상태", "Received letter:  $letter")
        }

        arguments?.let {
            selectedEmoji = it.getString("selectedEmoji")
            letter = it.getString("letter")
            Log.d("letter상태", "Received Emoji/letter: $selectedEmoji , $letter")

        }


        dateTv1 = view.findViewById(R.id.date1_tv)
        dateTv2 = view.findViewById(R.id.date2_tv)
        dateIv = view.findViewById(R.id.date_iv)
        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)


        situationTv = view.findViewById(R.id.user_situation_tv)
        emojiIv = view.findViewById(R.id.user_emoji_iv)
        letterTv  = view.findViewById(R.id.letter_tv)
        ad1Tv = view.findViewById(R.id.ad1_tv)
        ad2Tv = view.findViewById(R.id.ad2_tv)


        // send letter 부분
        if(letter=="send"){
            Log.d("letter상태", "send letter:  $letter")
            sendLetterLoad()
        }else if (letter=="random"){
            randomLetterLoad()
        }


        charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)

        // dateVisible에 따라 date_text 가시성 설정
        //updateDateVisibility()

        return view
    }

    private fun randomLetterLoad() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (uid != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val displayDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                        val matchingLetters = documents.filter { document ->
                            val emoji = document.getString("emoji")
                            emoji == selectedEmoji // 선택된 이모지와 일치하는지 확인
                        }.map { it.data }

                        if (matchingLetters.isNotEmpty()) {
                            val randomLetter = matchingLetters.random()
                            saveLetterToViewModel(randomLetter) // 편지를 저장
                            
                            displayLetter(randomLetter, dateFormat)
                            // 랜덤 편지가 로드되었음을 ViewModel에 설정
                            mailboxViewModel.setRandomLetterLoaded(true)
                            Log.d("mailboxViewModel", "mailboxViewModel1: $mailboxViewModel")
                            // 날짜 관련 UI 업데이트
                            dateIv.visibility = View.VISIBLE
                            dateTv2.visibility = View.VISIBLE

                            val selectedDate = randomLetter["date"] as? String
                            if (selectedDate != null) {
                                val formattedSelectedDate = displayDateFormat.format(dateFormat.parse(selectedDate)!!)
                                dateTv1.text = formattedSelectedDate
                            }

                            // 현재 날짜를 표시
                            val currentDate = Date()
                            val formattedCurrentDate = displayDateFormat.format(currentDate)
                            dateTv2.text = formattedCurrentDate
                            dateIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mail_random))

                        } else {
                            Log.d("letterLoad", "선택한 이모지에 해당하는 편지가 없습니다.")
                        }
                    } else {
                        Log.d("letterLoad", "편지 데이터가 없습니다.")
                    }
                }
        }
    }

    // 랜덤으로 선택한 편지 값을 ViewModel에 저장
    private fun saveLetterToViewModel(letterData: Map<String, Any?>) {
        mailboxViewModel.setRandomLetterData(letterData)
        Log.d("RandomLetter", "랜덤 편지 저장 완료: $letterData")
    }

    private fun sendLetterLoad() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val targetDate = selectedDate

        if (uid != null && targetDate != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val matchingLetter = documents.firstOrNull { document ->
                            val firebaseDate = document.getString("date")?.let { dateFormat.parse(it) }
                            firebaseDate != null && isSameDate(firebaseDate, targetDate)
                        }?.data

                        if (matchingLetter != null) {
                            displayLetter(matchingLetter, dateFormat)
                        } else {
                            Log.d("letterLoad", "선택된 날짜에 해당하는 편지가 없습니다.")
                        }
                    } else {
                        Log.d("letterLoad", "편지 데이터가 없습니다.")
                    }
                }
        }
    }

    private fun displayLetter(letterData: Map<String, Any?>, dateFormat: SimpleDateFormat) {
        val emoji = letterData["emoji"] as? String
        val situation = letterData["situation"] as? String
        val ad1 = letterData["ad1"] as? String
        val ad2 = letterData["ad2"] as? String
        val letter = letterData["letter"] as? String
        val firebaseDate = letterData["date"] as? String

        // UI 업데이트
        emoji?.let { emojiIv.setImageResource(getEmojiDrawable(it)) }
        situationTv.text = situation ?: ""
        ad1Tv.text = ad1 ?: ""
        ad2Tv.text = ad2 ?: ""
        firebaseDate?.let {
            val parsedDate = dateFormat.parse(it)
            val newDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            dateTv1.text = parsedDate?.let { newDateFormat.format(it) }

        }
        charCountTextView.text = (letter?.length ?: 0 + 2).toString()

        letter?.let {
            val letterLines = it.split("\n").toMutableList()
            if (letterLines.isNotEmpty()) {
                letterLines[0] = letterLines[0] + "에게"
            }
            letterTv.text = letterLines.joinToString("\n")
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