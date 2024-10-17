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
    private val dayList: MutableList<Date>,
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
        val daySdf = SimpleDateFormat("d", Locale.getDefault()).format(currentDate)
        holder.dayText.text = daySdf

        // 오늘 날짜와 비교
        val today = Calendar.getInstance().time

        // todayIv 초기화: 기본적으로 숨김
        holder.todayIv.isVisible = false

        // 오늘 날짜일 경우
        if (currentDate.date == today.date && currentDate.month == today.month && currentDate.year == today.year) {
            holder.dayText.setTextColor(Color.BLACK)
            holder.todayIv.isVisible = true // 오늘 날짜의 아이콘 보이기
        } else {
            holder.dayText.setTextColor(Color.GRAY) // 다른 날짜는 기본 회색 설정
        }

        // Firestore에서 데이터 가져오기
        if (uid != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    holder.dayImg.isVisible = false
                    holder.hasImage = false

                    if (!documents.isEmpty) {
                        for (document in documents) {
                            val dateString = document.getString("date")
                            if (dateString != null) {
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val firebaseDate = dateFormat.parse(dateString)

                                if (firebaseDate != null && firebaseDate.date == currentDate.date && firebaseDate.month == currentDate.month && firebaseDate.year == currentDate.year) {
                                    val emoji = document.getString("emoji")
                                    if (emoji != null) {
                                        holder.dayImg.setImageResource(getEmojiDrawable(emoji))
                                        holder.dayImg.isVisible = true
                                        holder.dayCv.isVisible = false
                                        holder.hasImage = true
                                    } else {
                                        holder.dayImg.isVisible = false
                                        holder.dayCv.isVisible = true
                                        holder.hasImage = false
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
            currentDate.date == it.date && currentDate.month == it.month && currentDate.year == it.year
        } ?: (currentDate.date == today.date && currentDate.month == today.month && currentDate.year == today.year)
    }

    private fun getEmojiDrawable(emoji: String): Int {
        return when (emoji) {
            "excited_s" -> R.drawable.ic_mailbox_01
            "happy_s" -> R.drawable.ic_mailbox_02_s
            "normal_s" -> R.drawable.ic_mailbox_03_s
            "upset_s" -> R.drawable.ic_mailbox_04_s
            "angry_s" -> R.drawable.ic_mailbox_05_s
            else -> R.drawable.ic_mailbox_01 // 기본 이미지
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }
}
