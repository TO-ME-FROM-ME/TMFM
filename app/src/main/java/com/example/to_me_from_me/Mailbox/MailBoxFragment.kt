package com.example.to_me_from_me.Mailbox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MailBoxFragment: BottomSheetDialogFragment()  {
    private lateinit var mailboxViewModel: MailboxViewModel

    private var randomLetterData: Map<String, Any?>? = null
    private var receiveLetterData: Map<String, Any?>? = null

    private var selectedDate: Date? = null // Date 타입의 변수 선언
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    private lateinit var sendTv: TextView
    private lateinit var receiveTv: TextView
    private lateinit var randomTv: TextView

    private lateinit var sendIv: ImageView
    private lateinit var receiveIv: ImageView
    private lateinit var randomIv: ImageView

    private lateinit var sendAdTv1: TextView
    private lateinit var sendAdTv2: TextView

    private lateinit var receiveAdTv1: TextView
    private lateinit var receiveAdTv2: TextView

    private lateinit var randomAdTv1: TextView
    private lateinit var randomAdTv2: TextView


    private lateinit var sendMailLl : LinearLayout
    private lateinit var sendMail : LinearLayout
    private lateinit var receiveMailLl : LinearLayout
    private lateinit var receiveMail : LinearLayout
    private lateinit var randomMailLl : LinearLayout
    private lateinit var randomMail : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_mailbox, container, false)

        mailboxViewModel = ViewModelProvider(this).get(MailboxViewModel::class.java)
        mailboxViewModel.randomLetterData.observe(viewLifecycleOwner) { letterData ->
            letterData?.let {
                // UI 업데이트
                //loadRandom(it)
                loadRandomLetterUI(it)
            }
        }


        mailboxViewModel.hasLetterToday.observe(viewLifecycleOwner) { hasLetterToday ->
            if (hasLetterToday) {
                randomMail.visibility = View.GONE
                randomMailLl.visibility = View.GONE
            } else {
                randomMail.visibility = View.GONE
                randomMailLl.visibility = View.GONE
            }
        }

        // Bundle로 전달된 selectedDate 값을 받아옴
        selectedDate = arguments?.getSerializable("selectedDate") as? Date


        // UI 요소 초기화
        initializeUI(view)

        letterLoad()
        loadRandomDateLetters()
        loadReceiveLetters()

        sendMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter","send")
            // 선택된 날짜를 Long 형식으로 변환하여 추가
            selectedDate?.let {
                intent.putExtra("selectedDate", it.time) // Date를 Long으로 변환
            }
            updateReadStatus("send")
            startActivity(intent)
        }

        receiveMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter","receive")
            // 선택된 날짜를 Long 형식으로 변환하여 추가
            selectedDate?.let {
                intent.putExtra("selectedDate", it.time) // Date를 Long으로 변환
            }
            // 안전한 호출을 사용하여 letterData2의 값을 가져옵니다.
            receiveLetterData?.let { letterData2 ->
                intent.putExtra("emoji", letterData2["emoji"] as? String)
                intent.putExtra("situation", letterData2["situation"] as? String)
                intent.putExtra("ad1", letterData2["ad1"] as? String)
                intent.putExtra("ad2", letterData2["ad2"] as? String)
                intent.putExtra("reservedate", letterData2["reservedate"] as? String)
                intent.putExtra("readStatus", letterData2["readStatus"] as? Boolean ?: false)
            }
            updateReadStatus("send")
            startActivity(intent)
            Log.d("흘러온 편지", "receiveLetterData.arguments : ${receiveLetterData}")
        }

        randomMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter", "random")
            // 선택된 날짜를 Long 형식으로 변환하여 추가
            selectedDate?.let {
                val selectedDateLong = it.time
                intent.putExtra("selectedDate", selectedDateLong) // Date를 Long으로 변환
                Log.d("IntentData", "Selected Date: $selectedDateLong")  // 로그 출력
            }



            randomLetterData?.let { letterData ->
                val emoji = letterData["emoji"] as? String
                val situation = letterData["situation"] as? String
                val ad1 = letterData["ad1"] as? String
                val ad2 = letterData["ad2"] as? String
                val readStatus = letterData["readStatus"] as? Boolean ?: false

                intent.putExtra("emoji", emoji)
                intent.putExtra("situation", situation)
                intent.putExtra("ad1", ad1)
                intent.putExtra("ad2", ad2)
                intent.putExtra("readStatus", readStatus)

                Log.d("IntentData", "Emoji: $emoji")          // 로그 출력
                Log.d("IntentData", "Situation: $situation")  // 로그 출력
                Log.d("IntentData", "Ad1: $ad1")              // 로그 출력
                Log.d("IntentData", "Ad2: $ad2")              // 로그 출력
                Log.d("IntentData", "Read Status: $readStatus")  // 로그 출력
            }


            updateReadStatus("send")
            startActivity(intent)

        }



        return view
    }




    private fun initializeUI(view: View) {
        sendMailLl = view.findViewById(R.id.send_ll)
        sendMail = view.findViewById(R.id.send_mail)
        receiveMailLl = view.findViewById(R.id.receiv_ll)
        receiveMail = view.findViewById(R.id.receiv_mail)
        randomMailLl = view.findViewById(R.id.random_ll)
        randomMail = view.findViewById(R.id.random_mail)

        sendTv = view.findViewById(R.id.user_situation_tv)
        receiveTv = view.findViewById(R.id.user_situation_tv2)
        randomTv = view.findViewById(R.id.user_situation_tv3)

        sendIv = view.findViewById(R.id.user_emo_iv)
        receiveIv = view.findViewById(R.id.user_emo_iv2)
        randomIv = view.findViewById(R.id.user_emo_iv3)

        sendAdTv1 = view.findViewById(R.id.adjective_s_1)
        sendAdTv2 = view.findViewById(R.id.adjective_s_2)
        receiveAdTv1 = view.findViewById(R.id.adjective_g_1)
        receiveAdTv2 = view.findViewById(R.id.adjective_g_2)
        randomAdTv1 = view.findViewById(R.id.adjective_r_1)
        randomAdTv2 = view.findViewById(R.id.adjective_r_2)

    }

    private fun updateReadStatus(letterType: String) {
        if (uid != null && selectedDate != null) {
            val targetDate = selectedDate
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val dateString = document.getString("date")
                        val firebaseDate = dateString?.let { dateFormat.parse(it) }

                        if (firebaseDate != null && targetDate != null && isSameDate(firebaseDate, targetDate)) {
                            // readStatus를 true로 업데이트
                            document.reference.update("readStatus", true)
                                .addOnSuccessListener {
                                    Log.d("updateReadStatus", "문서 ${document.id}의 readStatus값 true")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("updateReadStatus", "문서 ${document.id}의 readStatus 업데이트 실패", e)
                                }
                            break // 날짜가 일치하는 문서를 찾았으면 종료
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("updateReadStatus", "편지 데이터 로드 실패", e)
                }
        }
    }

    private fun saveLetterToViewModel(letterData: Map<String, Any?>) {
        // readStatus를 false로 설정
        val updatedLetterData = letterData.toMutableMap().apply {
            this["readStatus"] = false // readStatus 값을 false로 설정
        }
        Log.d("readStatus", "readStatus : $updatedLetterData")

        mailboxViewModel.setRandomLetterData(updatedLetterData)
    }

    private fun letterLoad() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore = FirebaseFirestore.getInstance()

        val targetDate = selectedDate
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        if (targetDate != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            val letterData = document.data
                            val dateString = document.getString("date")
                            val firebaseDate = dateString?.let { dateFormat.parse(it) }
                            val emoji = document.getString("emoji")
                            val situation = document.getString("situation")
                            val ad1 = document.getString("ad1")
                            val ad2 = document.getString("ad2")
                            val readStatus = document.getBoolean("readStatus") ?: false

                            if (firebaseDate != null && isSameDate(firebaseDate, targetDate)) {
                                // 보낸 편지 표시
                                loadSentLetterUI(emoji, situation, ad1, ad2, readStatus)
                                // readStatus 필드 추가 또는 업데이트
                                if (document.get("readStatus") == null) {
                                    document.reference.update("readStatus", false)
                                        .addOnSuccessListener {
                                            Log.d("addReadStatus", "문서 ${document.id}에 readStatus 필드가 추가되었습니다.")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("addReadStatus", "문서 ${document.id}에 readStatus 추가 실패", e)
                                        }
                                }

                                // 보낸 편지를 찾았으면 반복 종료
                                break
                            }
                        }
                    } else {
                        Log.d("loadLetters", "편지 데이터가 없습니다.")
                        randomMailLl.isVisible = false
                        sendMailLl.isVisible = false
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("loadLetters", "편지 데이터 로드 실패", e)
                }
        }
    }

    private fun loadReceiveLetters() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val targetDate = selectedDate
        Log.d("보낸편지", "targetDate: $targetDate")

        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val selectedDateString = targetDate?.let { dateFormat.format(it) }

        if (uid != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            val reservedate = document.getString("reservedate")
                            Log.d("보낸편지", "reservedate: $reservedate")

                            // reservedate를 yyyy.MM.dd 형식으로 변환
                            val formattedReservedate = reservedate?.let {
                                val originalDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Firestore에서 저장된 형식
                                val date = originalDateFormat.parse(it) // String을 Date로 변환
                                dateFormat.format(date) // 원하는 형식으로 변환
                            }

                            Log.d("보낸편지", "formattedReservedate: $formattedReservedate, selectedDate: $selectedDateString")

                            // targetDate와 formattedReservedate가 일치하는 경우
                            if (formattedReservedate != null && formattedReservedate == selectedDateString) {
                                // 사용자에게 보여주기 위해 Letter 객체 생성
                                val letterData2: Map<String, Any?> = mapOf(
                                    "emoji" to document.getString("emoji"),
                                    "situation" to document.getString("situation"),
                                    "ad1" to document.getString("ad1"),
                                    "ad2" to document.getString("ad2"),
                                    "readStatus" to (document.getBoolean("readStatus") ?: false)
                                )
                                receiveLetterData = letterData2 // 랜덤 편지 데이터 저장

                                Log.d("보낸편지", "일치하는 편지 데이터: $letterData2")

                                // UI를 업데이트하는 함수 호출
                                loadReceiveLetterUI(letterData2) // Map을 전달합니다.
                            }
                        }
                    } else {
                        Log.d("보낸편지", "편지 데이터가 없습니다.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("보낸편지", "편지 데이터 로드 실패", e)
                }
        }
    }

    private fun loadReceiveLetterUI(letterData2: Map<String, Any?>) {
        val emoji = letterData2["emoji"] as? String
        val situation = letterData2["situation"] as? String
        val ad1 = letterData2["ad1"] as? String
        val ad2 = letterData2["ad2"] as? String
        val readStatus = letterData2["readStatus"] as? Boolean ?: false

        // UI 요소 업데이트
        emoji?.let {  receiveIv.setImageResource(getEmojiDrawable(it)) }
        receiveTv.text = situation.toString()
        receiveAdTv1.text = ad1.toString()
        receiveAdTv2.text = ad2.toString()
        receiveMailLl.visibility = View.VISIBLE
        receiveMail.visibility = View.VISIBLE
        receiveMail.setBackgroundResource(if (readStatus) R.drawable.rounded else R.drawable.rounded_false)
    }


    private fun loadRandomDateLetters() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        if (uid != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val randomDateLetters = documents.filter { document ->
                            val randomDate = document.getString("randomDate")
                            randomDate != null
                        }
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        // 미리 로드된 편지 리스트를 ViewModel에 저장
                        mailboxViewModel.setRandomDateLetters(randomDateLetters)
                        Log.d("랜덤편지", "편지 데이터가 저장됨: ${randomDateLetters.map { it.data }}") // 로그 추가
                        onDateSelected(dateFormat.format(selectedDate))

                    } else {
                        Log.d("랜덤편지", "편지 데이터가 없습니다.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("loadRandomDateLetters", "편지 데이터 로드 실패", e)
                }
        }
    }

    private fun onDateSelected(selectedDate: String) {
        Log.d("랜덤편지", "selectedDate -> $selectedDate")

        // mailboxViewModel에서 가져온 편지 목록을 변수에 저장
        val letters = mailboxViewModel.getRandomDateLetters()
        // 각 문서의 randomDate를 로그로 출력
        letters.forEach { document ->
            val randomDate = document.getString("randomDate")
            Log.d("랜덤편지", "randomDate -> $randomDate")
        }

        // 선택한 날짜에 맞는 편지를 찾음
        val matchingLetter = letters.find { document ->
            val randomDate = document.getString("randomDate")
            randomDate != null && randomDate == selectedDate
        }

        if (matchingLetter != null) {
            val letterData: Map<String, Any?> = mapOf(
                "emoji" to matchingLetter.getString("emoji"),
                "situation" to matchingLetter.getString("situation"),
                "ad1" to matchingLetter.getString("ad1"),
                "ad2" to matchingLetter.getString("ad2"),
                "readStatus" to (matchingLetter.getBoolean("readStatus") ?: false)
            )
            randomLetterData = letterData // 랜덤 편지 데이터 저장
            loadRandomLetterUI(letterData) // Map을 전달합니다.
        } else {
            Log.d("랜덤편지", "선택한 날짜에 해당하는 랜덤 편지가 없습니다.")
        }
    }


    private fun loadSentLetterUI(emoji: String?, situation: String?, ad1: String?, ad2: String?, readStatus: Boolean) {
        emoji?.let { sendIv.setImageResource(getEmojiDrawable(it)) }
        sendTv.text = situation.toString()
        sendAdTv1.text = ad1.toString()
        sendAdTv2.text = ad2.toString()
        sendMailLl.visibility = View.VISIBLE
        sendMail.visibility = View.VISIBLE
        sendMail.setBackgroundResource(if (readStatus) R.drawable.rounded else R.drawable.rounded_false)
    }

    private fun loadRandomLetterUI(letterData: Map<String, Any?>) {
        val emoji = letterData["emoji"] as? String
        val situation = letterData["situation"] as? String
        val ad1 = letterData["ad1"] as? String
        val ad2 = letterData["ad2"] as? String
        val readStatus = letterData["readStatus"] as? Boolean ?: false

        // UI 요소 업데이트
        emoji?.let { randomIv.setImageResource(getEmojiDrawable(it)) }
        randomTv.text = situation.toString()
        randomAdTv1.text = ad1.toString()
        randomAdTv2.text = ad2.toString()
        randomMailLl.visibility = View.VISIBLE
        randomMail.visibility = View.VISIBLE
        randomMail.setBackgroundResource(if (readStatus) R.drawable.rounded else R.drawable.rounded_false)
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
            "excited_s" -> R.drawable.excited_s2  // excited_s에 해당하는 drawable
            "happy_s" -> R.drawable.happy_s2      // happy_s에 해당하는 drawable
            "normal_s" -> R.drawable.normal_s2    // normal_s에 해당하는 drawable
            "upset_s" -> R.drawable.upset_s2          // upset_s에 해당하는 drawable
            "angry_s" -> R.drawable.angry_s2       // upset_s에 해당하는 drawable
            else -> 0 // 기본 이미지
        }
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme3)
        dialog.setCanceledOnTouchOutside(true)  // 배경 클릭 시 닫힘
        return dialog
    }
}