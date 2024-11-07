package com.example.to_me_from_me.MainAlarm

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.Mailbox.DetailMailBoxActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainAlarmAdapter(
    private val context: Context,
    private val alarmDataList: List<AlarmData>,
) : RecyclerView.Adapter<MainAlarmAdapter.AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_alram_layout, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarmData = alarmDataList[position]
        holder.bind(alarmData)
        saveClickedStatusToSharedPreferences(alarmData)
        logClickedStatus() // 저장된 데이터 확인
    }

    override fun getItemCount(): Int = alarmDataList.size

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val img: ImageView = itemView.findViewById(R.id.icon_iv)
        private val title: TextView = itemView.findViewById(R.id.title_tv)
        private val letter: TextView = itemView.findViewById(R.id.letter_tv)
        private val time: TextView = itemView.findViewById(R.id.time_tv)
        private val container: ConstraintLayout = itemView.findViewById(R.id.linear_ll)

        init {
            itemView.setOnClickListener {
                val alarmData = alarmDataList[adapterPosition]
                val reservedate = alarmData.reservedate

                // 클릭된 항목의 reservedate 가져오기
                updateClickedStatusInFirestore(reservedate, alarmData)

                // SharedPreferences에 클릭 상태 저장
                val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("clicked_${reservedate}", true) // 클릭된 상태로 업데이트
                editor.commit()

                // 배경색 변경
                container.setBackgroundResource(R.drawable.rounded_gray) // 클릭 후 배경색 변경

                // 상세 화면으로 데이터 전송
                val intent = Intent(context, DetailMailBoxActivity::class.java).apply {
                    putExtra("situation", letter.text.toString())
                    putExtra("letter", "receive2")
                    putExtra("reservedate", reservedate)
                }
                context.startActivity(intent) // Activity 전환
            }
        }

        fun getRelativeTimeString(reservedDate: String): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val alarmDateTime = LocalDateTime.parse(reservedDate, formatter)
            val now = LocalDateTime.now()

            val minutesDiff = ChronoUnit.MINUTES.between(alarmDateTime, now)
            val hoursDiff = ChronoUnit.HOURS.between(alarmDateTime, now)
            val daysDiff = ChronoUnit.DAYS.between(alarmDateTime, now)

            return when {
                minutesDiff < 1 -> "방금 전"
                minutesDiff < 60 -> "${minutesDiff}분 전"
                hoursDiff < 24 -> "${hoursDiff}시간 전"
                daysDiff < 7 -> "${daysDiff}일 전"
                else -> alarmDateTime.format(DateTimeFormatter.ofPattern("MM/dd"))
            }
        }

        fun bind(alarmData: AlarmData) {
            img.setImageResource(alarmData.imgResId)
            title.text = alarmData.title              // 제목 설정
            letter.text = alarmData.letter            // 본문 설정
            time.text = getRelativeTimeString(alarmData.reservedate) // 시간 설정

            // SharedPreferences에서 clicked 상태 확인
            val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val isClicked = sharedPreferences.getBoolean("clicked_${alarmData.reservedate}", false)

            Log.d("AlarmAdapter", "Checking clicked status for reservedate: ${alarmData.reservedate}, clicked: $isClicked")

            // 클릭 여부에 따라 배경색 변경
            if (isClicked) {
                container.setBackgroundResource(R.drawable.rounded_gray) // 읽은 편지
                container.alpha = 0.9f
                img.alpha = 0.2f
                title.setTextColor(ContextCompat.getColor(context, R.color.Gray3))
                letter.setTextColor(ContextCompat.getColor(context, R.color.Gray3))
                time.setTextColor(ContextCompat.getColor(context, R.color.Gray3))

            } else {
                container.setBackgroundResource(R.drawable.rounded_blue) // 읽지 않은 편지
            }
        }
    }

    // Firestore에서 클릭 상태 업데이트
    private fun updateClickedStatusInFirestore(reservedate: String, alarmData: AlarmData) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("letters")
                .whereEqualTo("reservedate", reservedate)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Firestore에서 clicked 상태를 true로 업데이트
                        document.reference.update("clicked", true)
                            .addOnSuccessListener {
                                Log.d("AlarmAdapter", "클릭 상태 Firestore 업데이트 성공")
                                // 클릭된 상태를 SharedPreferences에 저장
                                saveClickedStatusToSharedPreferences(alarmData)
                            }
                            .addOnFailureListener { e ->
                                Log.e("AlarmAdapter", "클릭 상태 Firestore 업데이트 실패", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AlarmAdapter", "Firestore에서 문서 가져오기 실패", e)
                }
        }
    }

    // SharedPreferences에 클릭된 상태 저장
    private fun saveClickedStatusToSharedPreferences(alarmData: AlarmData) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // "clicked_" + reservedate + 다른 식별자 추가 (예: letterId 등)
        val uniqueKey = "clicked_${alarmData.reservedate}_${alarmData.letter}"
        editor.putBoolean(uniqueKey, true) // 클릭된 상태 저장
        editor.apply()

        Log.d("AlarmAdapter", "SharedPreferences에 클릭 상태 저장: $uniqueKey")
    }

    // SharedPreferences의 모든 상태 로그 확인
    private fun logClickedStatus() {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        // 모든 저장된 값을 확인하려면 SharedPreferences에 있는 모든 키와 값 출력
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            Log.d("SharedPreferences", "Key: $key, Value: $value")
        }
    }
}
