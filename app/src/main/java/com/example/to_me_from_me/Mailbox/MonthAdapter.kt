package com.example.to_me_from_me.Mailbox

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.MainAlarm.MainAlarmFragment
import com.example.to_me_from_me.MainAlarm.MainNoAlarmFragment
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MonthAdapter(
    private val onDayClickListener: (Date, Boolean) -> Unit,
    private val fragmentManager: FragmentManager,
) : RecyclerView.Adapter<MonthAdapter.Month>() {

    private var calendar: Calendar = Calendar.getInstance()
    private var selectedDate: Date = calendar.time


    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Month {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mailbox_month, parent, false)

        val backButton: ImageView = view.findViewById(R.id.back_iv)
        backButton.setOnClickListener {
            val intent = Intent(parent.context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            parent.context.startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        return Month(view)
    }

    override fun onBindViewHolder(holder: Month, position: Int) {
        val listLayout = holder.view.findViewById<RecyclerView>(R.id.month_recycler)
        val monthIv = holder.view.findViewById<View>(R.id.month_down_iv)

        calendar.time = selectedDate // 현재 날짜 초기화
        calendar.set(Calendar.DAY_OF_MONTH, 1) // 스크롤 시 현재 월의 1일로 이동
        calendar.add(Calendar.MONTH, position) // 스크롤 시 포지션만큼 달 이동

        monthIv.setOnClickListener {
            val dialogFragment = MonthPickerDialogFragment(
                selectedYear = calendar.get(Calendar.YEAR),
                selectedMonth = calendar.get(Calendar.MONTH)
            )
            Log.d("MonthPicker","넘길때 -> ${calendar.get(Calendar.YEAR)}, ${calendar.get(Calendar.MONTH)+1}")
            dialogFragment.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialogFragment.show(fragmentManager, "MonthPickerDialogFragment")
        }


        val titleText: TextView = holder.view.findViewById(R.id.title)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        titleText.text ="${calendar.get(Calendar.YEAR)}년 ${currentMonth}월"

        // 현재 월의 첫 번째 날
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 일요일: 1 -> 배열 인덱스로 변경 (0부터 시작)

        // 현재 월의 마지막 날
        val maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        // 빈 날짜와 해당 월 날짜 리스트 생성
        //val dayList: MutableList<Date?> = MutableList(42) { null } // 총 6주를 표시하기 위해 42칸 (빈 칸을 포함)

        // MonthAdapter에서 dayList를 생성할 때 null 없이 Date 객체만 포함하도록 설정

        val dayList: MutableList<Date?> = MutableList(42) { null } // 최대 6주 표시를 위한 42칸

        // 해당 월 날짜 추가
        for (i in 0 until maxDayInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, i + 1) // i번째 날
            dayList[firstDayOfMonth + i] = calendar.time // 시작 요일 이후에 날짜 추가
        }



        val tempMonth = calendar.get(Calendar.MONTH)
        // RecyclerView에 데이터를 설정
        listLayout.layoutManager = GridLayoutManager(holder.view.context, 7)
        listLayout.adapter = DayAdapter(tempMonth, dayList) { clickedDate, hasImage ->
            if (!hasImage) {
                loadReservedateForDay(clickedDate) // HasImage가 false인 경우 reservedate 확인
            }
            onDayClickListener(clickedDate, hasImage) // 날짜 클릭 시 리스너 호출
            Log.d("DayAdapter", "Clicked: $clickedDate, HasImage: $hasImage")
        }


    }

    private fun loadReservedateForDay(clickedDate: Date) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(clickedDate)

        if (uid != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    var hasImage = false // 초기화

                    if (!documents.isEmpty) {
                        for (document in documents) {
                            val reservedate = document.getString("reservedate")
                            if (reservedate != null) {
                                val formattedReservedate = dateFormat.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(reservedate))

                                if (formattedDate == formattedReservedate) {
                                    Log.d("Firestore", "Matched reservedate for date: $formattedDate")
                                    hasImage = true
                                    break // 일치하는 경우 반복문 종료
                                }
                            }
                        }
                    }
                    // 결과에 따라 onDayClickListener 호출
                    if (hasImage) {
                        // 예약 날짜가 일치하면 showNotNullMailboxFragment 호출
                        onDayClickListener(clickedDate, true)
                    } else {
                        // 예약 날짜가 일치하지 않으면 showNullMailboxFragment 호출
                        onDayClickListener(clickedDate, false)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error checking reservedate for date: $formattedDate", e)
                    // 에러 발생 시 false로 처리
                    onDayClickListener(clickedDate, false)
                }
        }
    }




    override fun getItemCount(): Int {
        return Int.MAX_VALUE / 2
    }

    class Month(val view: View) : RecyclerView.ViewHolder(view)

    fun setCurrentMonth(date: Date) {
        // 현재 월을 설정하고 어댑터를 갱신
        Log.d("MonthPicker","data : $date")
        selectedDate = date
        Log.d("MonthPicker","MonthAdapter $selectedDate")
        notifyDataSetChanged()
    }
}