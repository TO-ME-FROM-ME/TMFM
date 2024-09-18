package com.example.to_me_from_me.Mailbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.NoScrollLinearLayoutManager
import com.example.to_me_from_me.databinding.ActivityMailboxBinding
import java.util.Date

class MailboxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMailboxBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMailboxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView(binding)
    }

    private fun initView(binding: ActivityMailboxBinding) {
        recyclerView = binding.calRecycler
        val position: Int = Int.MAX_VALUE / 2

        // MonthAdapter에 클릭 리스너 추가
        val adapter = MonthAdapter(
            onDayClickListener = { clickedDate ->
                showNullMailboxFragment(clickedDate) // 날짜 클릭 시 바텀시트 표시
            },
            fragmentManager = supportFragmentManager // FragmentManager 전달
        )

        // 커스텀 LinearLayoutManager 생성 및 설정
        recyclerView.layoutManager = NoScrollLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(position) // 중앙으로 스크롤 위치 설정
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
