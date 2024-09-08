package com.example.to_me_from_me.Mailbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.R
import com.example.to_me_from_me.databinding.ActivityMailboxBinding
import java.util.Date

class MailboxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMailboxBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var nullMailboxFragment: NullMailboxFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMailboxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView(binding)
        createDate()
    }

    private fun createDate() {
        binding.calRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initView(binding: ActivityMailboxBinding) {
        recyclerView = binding.calRecycler
        val position: Int = Int.MAX_VALUE / 2

        // MonthAdapter에 클릭 리스너 추가
        val adapter = MonthAdapter { clickedDate ->
            showNullMailboxFragment(clickedDate) // 날짜 클릭 시 바텀시트 표시
        }

        binding.calRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.calRecycler.adapter = adapter
        binding.calRecycler.scrollToPosition(position)
    }

    private fun showNullMailboxFragment(selectedDate: Date) {
        val nullMailboxFragment = NullMailboxFragment()

        // 선택한 날짜를 전달하려면 Bundle 사용 가능
        val args = Bundle()
        args.putSerializable("selectedDate", selectedDate)
        nullMailboxFragment.arguments = args

        nullMailboxFragment.show(supportFragmentManager, nullMailboxFragment.tag)
    }
}
