package com.example.to_me_from_me.Mailbox

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.MusicService
import com.example.to_me_from_me.R
import com.example.to_me_from_me.databinding.ActivityMailboxBinding
import com.example.to_me_from_me.startMusicService
import com.example.to_me_from_me.stopMusicService
import java.util.Calendar
import java.util.Date

class MailboxActivity : AppCompatActivity(), MonthPickerDialogFragment.MonthSelectionListener {

    private lateinit var binding: ActivityMailboxBinding
    private lateinit var recyclerView: RecyclerView
    private var adapter: MonthAdapter? = null
    private var nullMailboxFragment: NullMailboxFragment? = null


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
                    nullMailboxFragment?.dismiss()
                    showNotNullMailboxFragment(clickedDate)
                }else {
                    //showNullMailboxFragment(clickedDate)
                    showNotNullMailboxFragment(clickedDate)
                }
            },
            fragmentManager = supportFragmentManager

        )

        // 커스텀 LinearLayoutManager 생성 및 설정
        recyclerView.layoutManager = NoScrollLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(position)
    }

    override fun onMonthSelected(month: Int, year: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        adapter?.setCurrentMonth(calendar.time)
        Log.d("MonthPicker", "Mailbox Activity : ${calendar.time}")
    }


    private fun showNullMailboxFragment(selectedDate: Date) {
        // nullMailboxFragment가 이미 열려있지 않은 경우에만 새로 생성
        if (nullMailboxFragment == null) {
            nullMailboxFragment = NullMailboxFragment()
            val args = Bundle()
            args.putSerializable("selectedDate", selectedDate)
            nullMailboxFragment?.arguments = args
            nullMailboxFragment?.show(supportFragmentManager, nullMailboxFragment?.tag)
        } else {
            // 이미 열린 경우는 dismiss
            //nullMailboxFragment?.dismiss()
            //nullMailboxFragment = null // null로 설정하여 다음 호출 시 새로 생성
        }
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