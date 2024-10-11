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

        sendMailLl = view.findViewById(R.id.send_ll)
        sendMail = view.findViewById<LinearLayout>(R.id.send_mail)
        receiveMailLl = view.findViewById<LinearLayout>(R.id.receiv_ll)
        receiveMail = view.findViewById<LinearLayout>(R.id.receiv_mail)
        randomMailLl = view.findViewById<LinearLayout>(R.id.random_ll)
        randomMail = view.findViewById<LinearLayout>(R.id.random_mail)


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
        randomAdTv1= view.findViewById(R.id.adjective_r_1)
        randomAdTv2 = view.findViewById(R.id.adjective_r_2)

        // Bundle로 전달된 selectedDate 값을 받아옴
        selectedDate = arguments?.getSerializable("selectedDate") as? Date




        letterLoad()

        sendMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter","send")
            // 선택된 날짜를 Long 형식으로 변환하여 추가
            selectedDate?.let {
                intent.putExtra("selectedDate", it.time) // Date를 Long으로 변환
            }
            startActivity(intent)
        }

        receiveMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter","receive")
            startActivity(intent)
        }

        randomMail.setOnClickListener{
            val intent = Intent(context, DetailMailBoxActivity::class.java)
            intent.putExtra("letter","random")
            startActivity(intent)
        }


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

                            // 선택된 날짜와 Firestore에서 가져온 날짜가 일치하는지 비교
                            if (firebaseDate != null && isSameDate(firebaseDate, targetDate)) {
                                Log.d(
                                    "letterLoad",
                                    "firebaseDate: $firebaseDate \n targetDate: $targetDate"
                                )

                                // 문서에서 데이터를 가져와 UI에 표시
                                val emoji = document.getString("emoji")
                                val situation = document.getString("situation")
                                val ad1 = document.getString("ad1")
                                val ad2 = document.getString("ad2")
                                Log.d(
                                    "letterLoad",
                                    "emoji: $emoji \n situation: $situation \n ad1, ad2: $ad1, $ad2"
                                )

                                if (emoji != null) { // emoji가 null이 아닐 경우만 호출
                                    sendIv.setImageResource(getEmojiDrawable(emoji))
                                    sendTv.text = situation.toString()
                                    sendAdTv1.text = ad1.toString()
                                    sendAdTv2.text = ad2.toString()

                                    // 다른 항목 숨기기
                                    receiveMailLl.isVisible = false
                                    receiveMail.isVisible = false
                                    randomMailLl.isVisible = false
                                    randomMail.isVisible = false
                                } else {
                                    Log.d("letterLoad", "보낸 편지가 없습니다.")
                                    sendMailLl.isVisible = false
                                    sendMail.isVisible = false
                                }
                                // 날짜가 일치하는 문서를 찾았으면 나머지 문서들은 무시
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


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme3)
        dialog.setCanceledOnTouchOutside(true)  // 배경 클릭 시 닫힘
        return dialog
    }
}