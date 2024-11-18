package com.example.to_me_from_me.Mailbox

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DayAdapter(
    private val tempMonth: Int,
    private val dayList: MutableList<Date?>,
    private val onDayClickListener: (Date, Boolean) -> Unit
) : RecyclerView.Adapter<DayAdapter.DayView>() {
    val ROW = 5
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    private var selectedDate: Date? = null // 선택된 날짜를 저장할 변수

    class DayView(val layout: View) : RecyclerView.ViewHolder(layout) {
        val dayText: TextView = layout.findViewById(R.id.item_day_text)
        val todayIv: ImageView = layout.findViewById(R.id.today_iv)
        val dayCv: CardView = layout.findViewById(R.id.item_day_cv)
        val dayImg: ImageView = layout.findViewById(R.id.item_day_iv)
        var hasImage: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mailbox_day, parent, false)
        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentDate = dayList[position]

        // currentDate가 null이면 해당 View를 숨기기
        if (currentDate == null) {
            holder.itemView.visibility = View.INVISIBLE
            holder.dayText.text = ""
            holder.todayIv.alpha = 0f
            return
        } else {
            holder.itemView.visibility = View.VISIBLE
            val daySdf = SimpleDateFormat("d", Locale.getDefault()).format(currentDate)
            holder.dayText.text = daySdf
        }

        // 오늘 날짜와 비교
        val today = Calendar.getInstance().time

        // todayIv 초기화: 기본적으로 숨김
        holder.todayIv.isVisible = false

        // 오늘 날짜일 경우
        if (currentDate.date == today.date && currentDate.month == today.month && currentDate.year == today.year) {
            holder.dayText.setTextColor(Color.BLACK)
            holder.todayIv.isVisible = true // 오늘 날짜의 아이콘 보이기
        } else {
            holder.dayText.setTextColor(Color.BLACK)
        }

        // Firestore에서 데이터 가져오기
        if (uid != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    holder.dayImg.alpha = 0f
                    holder.hasImage = false

                    if (!documents.isEmpty) {
                        for (document in documents) {
                            val dateString = document.getString("date")

                            val date = document.id
                            val reservedate = document.getString("reservedDate")

                            if (dateString != null) {
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val firebaseDate = dateFormat.parse(dateString)

                                val reservedDateString = reservedate?.substring(0, 10) // yyyy-MM-dd 부분 추출
                                val firebaseReservedate = reservedDateString?.let { dateFormat.parse(it) }


                                // Firebase Date와 Current Date 비교
                                if (firebaseDate != null && isSameDate(firebaseDate, currentDate)) {
                                    val emoji = document.getString("emoji")
                                    if (emoji != null) {
                                        holder.dayImg.setImageResource(getEmojiDrawable(emoji))
                                        holder.dayImg.alpha = 1f  // 아이콘을 보이도록 처리
                                        holder.dayCv.alpha = 0f  // 카드뷰를 투명하게 처리
                                        holder.hasImage = true
                                    } else {
                                        holder.dayImg.alpha = 0f  // 아이콘을 숨기고
                                        holder.dayCv.alpha = 1f  // 카드뷰 보이게 처리
                                        holder.hasImage = false
                                    }

                                    // reservedDate가 일치하면 receiveMail 보이기 로직 추가
                                    if (firebaseReservedate != null && isSameDate(firebaseReservedate, currentDate)) {
                                        Log.d("DayAdapter", "receiveMail의 가시성을 조정하는 로직을 호출")
                                        onDayClickListener(currentDate, true) // true는 이미지를 표시하라는 의미
                                    }
                                }
                            }
                        }
                    }
                }
        }

        // 날짜 클릭 리스너
        holder.itemView.setOnClickListener {
            // 클릭된 날짜를 선택된 날짜로 저장
            selectedDate = currentDate
            onDayClickListener(currentDate, holder.hasImage)

            // 선택된 날짜에 대해 todayIv의 가시성 설정
            notifyDataSetChanged() // 모든 항목을 업데이트하여 새로 고침
        }

        // 선택된 날짜에 대해 todayIv의 가시성 설정
        holder.todayIv.isVisible = selectedDate?.let {
            isSameDate(currentDate, it)
        } ?: (isSameDate(currentDate, today))
    }

    private fun isSameDate(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply { time = date1 }
        val calendar2 = Calendar.getInstance().apply { time = date2 }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }

    private fun getEmojiDrawable(emoji: String): Int {
        return when (emoji) {
            "excited_s1" -> R.drawable.ic_mailbox_e
            "happy_s1" -> R.drawable.ic_mailbox_h
            "normal_s1" -> R.drawable.ic_mailbox_n
            "upset_s1" -> R.drawable.ic_mailbox_u
            "angry_s1" -> R.drawable.ic_mailbox_a
            else -> R.drawable.ic_mailbox_e // 기본 이미지
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }
}