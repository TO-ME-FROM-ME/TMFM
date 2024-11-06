package com.example.to_me_from_me.Mailbox

import android.app.Dialog
import android.content.Intent
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DetailMailBoxFragment : BottomSheetDialogFragment() {
    private val mailboxViewModel: MailboxViewModel by activityViewModels()

    private var selectedDate: Date? = null
    private var selectedEmoji: String? = null
    private var emoji: String? = null
    private var letter : String? = null
    private var letters : String? = null
    private var situation : String? = null
    private var ad1 : String? = null
    private var ad2 : String? = null



    private var reservedate: String? = null

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
        }

        arguments?.let {
            selectedEmoji = it.getString("selectedEmoji")
            letter = it.getString("letter")

        }


        arguments?.let {
            reservedate = it.getString("reservedate")
            letter = it.getString("letter")
            Log.d("DetailMailBoxFragment", "Reservedate received: $reservedate + $letter")
        }


        arguments?.let {bundle ->
            val selectedDateMillis = bundle.getLong("selectedDate", -1L)
            if (selectedDateMillis != -1L) {
                selectedDate = Date(selectedDateMillis) // Long을 Date로 변환
            }
            situation = bundle.getString("situation")
            emoji = bundle.getString("emoji")
            ad1 = bundle.getString("ad1")
            ad2 = bundle.getString("ad2")
            letter = bundle.getString("letter")
        }


        arguments?.let {
            reservedate = it.getString("reservedate")
            letter = it.getString("letter")
        }


        // 전달된 Bundle에서 데이터를 받기
        arguments?.let { bundle ->
            selectedDate = Date(bundle.getLong("selectedDate", -1L))
            selectedEmoji = bundle.getString("selectedEmoji")
            letter = bundle.getString("letter")
            situation = bundle.getString("situation")
            emoji = bundle.getString("emoji")
            ad1 = bundle.getString("ad1")
            ad2 = bundle.getString("ad2")
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



        // 편지 로드
        when (letter) {
            "send" -> sendLetterLoad()
            "random" -> randomLetterLoad()
            "receive" -> receiveLetterLoad()
            "receive2" -> receiveLetterLoad2()
            "randomMail" -> randomLetterUpdateUI()
        }

        charCountTextView = view.findViewById<TextView>(R.id.char_count_tv)

        return view
    }

    private fun receiveLetterLoad2() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 오늘 날짜를 가져옵니다.
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetDate = dateFormat.format(Date()) // 오늘 날짜 문자열
        var firstDateShown = false // 첫 번째 date가 표시되었는지 여부를 추적

        if (uid != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        var hasMatchingLetter = false
                        for (document in documents) {
                            val reservedateFromDb = document.getString("reservedate")
                            val date = document.getString("date")

                            // onCreateView에서 받아온 reservedate와 Firestore의 reservedate 비교
                            if (reservedateFromDb == reservedate) { // 일치하는 경우
                                hasMatchingLetter = true

                                // 날짜 형식 변환하여 표시
                                val displayDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                                if (!firstDateShown && date != null) {
                                    val formattedDate = displayDateFormat.format(dateFormat.parse(date)!!)
                                    dateTv2.text = formattedDate
                                    Log.d("DetailMailBoxFragment", "표시된 날짜: $formattedDate")
                                    firstDateShown = true
                                }

                                // 오늘 날짜를 표시
                                val formattedCurrentDate = displayDateFormat.format(Date())
                                dateTv2.text = formattedCurrentDate
                                dateIv.visibility = View.VISIBLE
                                dateTv2.visibility = View.VISIBLE
                                dateIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mail_re))

                                displayLetter(document.data, dateFormat) // 일치하는 편지 표시
                            }
                        }

                        if (!hasMatchingLetter) {
                            Log.d("DetailMailBoxFragment", "일치하는 reservedate가 없습니다.")
                        }
                    } else {
                        Log.d("letterLoad", "편지 데이터가 없습니다.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("letterLoad", "오류 발생: ${exception.message}")
                }
        }
    }



    private fun randomLetterUpdateUI() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 오늘 날짜를 가져옵니다.
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val targetDate = dateFormat.format(Date()) // 오늘 날짜 문자열
        var firstDateShown = false // 첫 번째 date가 표시되었는지 여부를 추적

        if (uid != null && targetDate != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val displayDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

                        // targetDate를 YYYY.MM.dd 형식으로 변환
                        val formattedTargetDate = displayDateFormat.format(targetDate)

                        // randomDate와 targetDate가 일치하는 편지를 찾기
                        val matchingLetter = documents.firstOrNull { document ->
                            val randomdate = document.getString("randomDate") // Firestore에서 가져온 randomDate

                            // randomDate가 targetDate와 일치하는지 확인
                            randomdate != null && formattedTargetDate == displayDateFormat.format(dateFormat.parse(randomdate))
                        }?.data

                        if (matchingLetter != null) {
                            // randomDate가 일치하는 경우 UI 업데이트 및 편지 표시
                            dateIv.visibility = View.VISIBLE
                            dateTv2.visibility = View.VISIBLE
                            dateTv2.text = formattedTargetDate // 포맷된 targetDate 설정
                            dateIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mail_random))

                            // 일치하는 편지 내용을 UI에 업데이트
                            displayLetter(matchingLetter, dateFormat)
                        } else {
                            Log.d("random", "선택된 날짜에 해당하는 편지가 없습니다.")
                            // 필요에 따라 UI 업데이트: 예를 들어, 빈 상태를 표시할 수 있습니다.
                        }
                    } else {
                        Log.d("random", "편지 데이터가 없습니다.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("random", "Error getting documents: ", exception)
                }
        }
    }


    private fun receiveLetterLoad() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val targetDate = selectedDate

        if (uid != null && targetDate != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val displayDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

                        // targetDate를 YYYY.MM.dd 형식으로 변환
                        val formattedTargetDate = displayDateFormat.format(targetDate)

                        // randomDate와 targetDate가 일치하는 편지를 찾기
                        val matchingLetter = documents.firstOrNull { document ->
                            val reservedate = document.getString("reservedate") // Firestore에서 가져온 randomDate

                            // randomDate가 targetDate와 일치하는지 확인
                            reservedate != null && formattedTargetDate == displayDateFormat.format(dateFormat.parse(reservedate))
                        }?.data

                        if (matchingLetter != null) {
                            // randomDate가 일치하는 경우 UI 업데이트 및 편지 표시
                            dateIv.visibility = View.VISIBLE
                            dateTv2.visibility = View.VISIBLE
                            dateTv2.text = formattedTargetDate // 포맷된 targetDate 설정
                            dateIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mail_re))

                            // 일치하는 편지 내용을 UI에 업데이트
                            displayLetter(matchingLetter, dateFormat)
                        } else {
                            Log.d("흘러온 편지", "선택된 날짜에 해당하는 편지가 없습니다.")
                            // 필요에 따라 UI 업데이트: 예를 들어, 빈 상태를 표시할 수 있습니다.
                        }
                    } else {
                        Log.d("흘러온 편지", "편지 데이터가 없습니다.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("흘러온 편지", "Error getting documents: ", exception)
                }
        }
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
                        }

                        if (matchingLetters.isNotEmpty()) {
                            val randomLetterDocument = matchingLetters.random()
                            val randomLetterData = randomLetterDocument.data

                            // 편지 데이터를 ViewModel에 저장
                            saveLetterToViewModel(randomLetterData)

                            displayLetter(randomLetterData, dateFormat)
                            mailboxViewModel.setRandomLetterLoaded(true)

                            // 날짜 관련 UI 업데이트
                            dateIv.visibility = View.VISIBLE
                            dateTv2.visibility = View.VISIBLE

                            val selectedDate = randomLetterData["date"] as? String
                            if (selectedDate != null) {
                                val formattedSelectedDate = displayDateFormat.format(dateFormat.parse(selectedDate)!!)
                                dateTv1.text = formattedSelectedDate
                            }

                            // 현재 날짜 표시
                            val currentDate = Date()
                            val formattedCurrentDate = displayDateFormat.format(currentDate)
                            dateTv2.text = formattedCurrentDate
                            dateIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mail_random))

                            // 랜덤 편지의 'random' 필드를 true로 업데이트하고 createdDate와 randomDate를 저장
                            val randomLetterId = randomLetterDocument.id
                            val updates = mapOf(
                                "random" to true,
                                "createdDate" to selectedDate, // 기존의 date를 createdDate로 저장
                                "randomDate" to dateFormat.format(currentDate) // 현재 날짜를 randomDate로 저장
                            )
                            firestore.collection("users").document(uid).collection("letters").document(randomLetterId)
                                .update(updates)
                                .addOnSuccessListener {
                                    Log.d("randomLetterLoad", "랜덤 편지의 'random', 'createdDate', 'randomDate' 필드를 성공적으로 업데이트했습니다.")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("randomLetterLoad", "랜덤 편지의 필드 업데이트에 실패했습니다.", e)
                                }

                        } else {
                            Log.d("randomLetterLoad", "선택한 이모지에 해당하는 편지가 없습니다.")
                        }
                    } else {
                        Log.d("randomLetterLoad", "편지 데이터가 없습니다.")
                    }
                }
        }
    }


    // 랜덤으로 선택한 편지 값을 ViewModel에 저장
    private fun saveLetterToViewModel(letterData: Map<String, Any?>) {
        mailboxViewModel.setRandomLetterData(letterData)
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
                        val displayDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

                        val matchingLetter = documents.firstOrNull { document ->
                            val firebaseDate = document.getString("date")?.let { dateFormat.parse(it) }
                            val reservedate = document.getString("reservedate")

                            if(reservedate!=null){
                                try {
                                    val reservedateDate = dateFormat.parse(reservedate) // 문자열을 Date로 변환
                                    val formattedSelectedDate = displayDateFormat.format(reservedateDate!!) // 원하는 형식으로 변환

                                    // UI 업데이트
                                    dateTv2.text = formattedSelectedDate
                                    dateIv.visibility = View.VISIBLE
                                    dateTv2.visibility = View.VISIBLE
                                    dateTv2.setTextColor(ContextCompat.getColor(requireContext(),R.color.Gray3))
                                    dateIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mail_send))

                                    Log.d("send편지", "Formatted reservedate: $formattedSelectedDate")
                                } catch (e: ParseException) {
                                    Log.e("sendLetter", "Error parsing reservedate", e)
                                }
                        }else{
                                dateTv2.visibility = View.GONE
                                dateIv.visibility = View.GONE
                            }
                            firebaseDate != null && isSameDate(firebaseDate, targetDate)
                        }?.data

                        if (matchingLetter != null) {
                            displayLetter(matchingLetter, dateFormat)

                        } else {
                            Log.d("sendLetter", "선택된 날짜에 해당하는 편지가 없습니다.")
                        }
                    } else {
                        Log.d("sendLetter", "편지 데이터가 없습니다.")
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme)

    }
}