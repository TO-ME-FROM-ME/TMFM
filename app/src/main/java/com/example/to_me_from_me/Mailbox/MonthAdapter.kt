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

    private val emojiMap = mutableMapOf<String, String>()


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
            Log.d("MonthPicker","넘길때 -> ${calendar.get(Calendar.YEAR)}, ${calendar.get(Calendar.MONTH)}")
            dialogFragment.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialogFragment.show(fragmentManager, "MonthPickerDialogFragment")
        }


        val titleText: TextView = holder.view.findViewById(R.id.title)
        titleText.text ="${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        val tempMonth = calendar.get(Calendar.MONTH)

        // 5주 7일로 날짜를 표시
        val dayList: MutableList<Date> = MutableList(5 * 7) { Date() }
        for (i in 0..4) { // 주
            for (k in 0..6) { // 요일
                calendar.add(Calendar.DAY_OF_MONTH, (1 - calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time // 배열 인덱스만큼 요일 데이터 저장
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        listLayout.layoutManager = GridLayoutManager(holder.view.context, 7)
        listLayout.adapter = DayAdapter(tempMonth, dayList) { clickedDate, hasImage ->
            onDayClickListener(clickedDate, hasImage) // 날짜 클릭 시 리스너 호출
            Log.d("DayAdapter", "Clicked: $clickedDate, HasImage: $hasImage")
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