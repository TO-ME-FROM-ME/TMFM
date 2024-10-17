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
        val dayDateString = daySdf.format(currentDate) // 현재 날짜의 일 부분
        holder.dayText.text = dayDateString


        // 현재 날짜와 비교
        val today = Calendar.getInstance().time
        if (currentDate.date == today.date && currentDate.month == today.month && currentDate.year == today.year) {
            holder.dayText.setTextColor(Color.BLACK)  // 현재 날짜면 텍스트를 검정색으로 설정
            holder.todayIv.isVisible = true             // dayCv를 true로 설정
        } else {
            holder.dayText.setTextColor(Color.GRAY)   // 다른 날짜는 기본 회색 설정
            holder.todayIv.isVisible = false            // dayCv를 false로 설정
        }


        if(uid != null ){
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    holder.dayImg.isVisible = false // 기본적으로 숨김 처리
                    holder.hasImage = false // 기본적으로 false 설정

                    if (!documents.isEmpty){
                        for (document in documents) {
                            val dateString = document.getString("date") // Firebase 값

                            Log.d("DayAdapter","dateString : $dateString")

                            if (dateString!=null) {
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val firebaseDate = dateFormat.parse(dateString)


                                if (firebaseDate != null && firebaseDate.date == currentDate.date && firebaseDate.month == currentDate.month && firebaseDate.year == currentDate.year) {
                                    val emoji = document.getString("emoji")
                                    Log.d("DayAdapter","emoji : $emoji")
                                    if (emoji !=null) { // emoji가 null이 아닐 경우만 호출
                                        holder.dayImg.setImageResource(getEmojiDrawable(emoji))
                                        holder.dayImg.isVisible = true // 이미지가 있는 경우 보이기
                                        holder.dayCv.isVisible=false
                                        holder.hasImage = true // 이미지를 보유
                                    }else{
                                        holder.dayImg.isVisible = false // 이미지가 있는 경우 보이기
                                        holder.dayCv.isVisible=true
                                        holder.hasImage = false // 이미지를 보유
                                    }
                                }
                            }
                        }

                    }
                }
        }



        holder.itemView.setOnClickListener {
            onDayClickListener(currentDate, holder.hasImage)
        }

    }
    private fun getEmojiDrawable(emoji: String): Int {
        return when (emoji) {
            "excited_s" -> R.drawable.ic_mailbox_01  // excited_s에 해당하는 drawable
            "happy_s" -> R.drawable.ic_mailbox_02_s      // happy_s에 해당하는 drawable
            "normal_s" -> R.drawable.ic_mailbox_03_s    // normal_s에 해당하는 drawable
            "upset_s" -> R.drawable.ic_mailbox_04_s          // sad_s에 해당하는 drawable
            "angry_s" -> R.drawable.ic_mailbox_05_s       // upset_s에 해당하는 drawable
            else -> R.drawable.ic_mailbox_01 // 기본 이미지
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }


}
