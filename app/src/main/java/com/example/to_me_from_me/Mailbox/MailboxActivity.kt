package com.example.to_me_from_me.Mailbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.example.to_me_from_me.databinding.ActivityMailboxBinding
import java.util.Calendar
import java.util.Date

class MailboxActivity : AppCompatActivity(), MonthPickerDialogFragment.MonthSelectionListener {

    private lateinit var binding: ActivityMailboxBinding
    private lateinit var recyclerView: RecyclerView
    private var adapter: MonthAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMailboxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)
    }


    private fun initView(binding: ActivityMailboxBinding) {
        recyclerView = binding.calRecycler
        val position: Int = Int.MAX_VALUE / 2

        // 멤버 변수인 adapter 초기화
        adapter = MonthAdapter(
            onDayClickListener = { clickedDate, hasImage ->
                if(hasImage){
                    showNotNullMailboxFragment(clickedDate)
                }else {
                    showNullMailboxFragment(clickedDate) // 날짜 클릭 시 바텀시트 표시
                }
            },
            fragmentManager = supportFragmentManager

        )

        // 커스텀 LinearLayoutManager 생성 및 설정
        recyclerView.layoutManager = NoScrollLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(position)
    }

    override fun onMonthSelected(month: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        adapter?.setCurrentMonth(calendar.time)
        Log.d("MonthPicker", "Mailbox Activity : ${calendar.time}")
    }


    private fun showNullMailboxFragment(selectedDate: Date) {
        val nullMailboxFragment = NullMailboxFragment()
        // 선택한 날짜를 전달하려면 Bundle 사용 가능
        val args = Bundle()
        args.putSerializable("selectedDate", selectedDate)
        nullMailboxFragment.arguments = args
        nullMailboxFragment.show(supportFragmentManager, nullMailboxFragment.tag)
    }

    private fun showNotNullMailboxFragment(selectedDate: Date) {
        val mailboxFragment = MailBoxFragment()
        val args = Bundle()
        args.putSerializable("selectedDate", selectedDate)
        mailboxFragment.arguments = args
        mailboxFragment.show(supportFragmentManager, mailboxFragment.tag)
        Log.d("selectedDate", "selectedDate : $selectedDate")
    }

}