package com.example.to_me_from_me

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import java.util.Calendar

class StorageDialogFragment : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.storage_dialog, container, false)

        // 백 스택의 항목 수 가져오기
        val backStackCount = parentFragmentManager.backStackEntryCount

        // 다이얼로그의 닫기 버튼 설정
        val closeIv: ImageView = view.findViewById(R.id.close_iv)
        closeIv.setOnClickListener {
            dismiss()
        }

        // "저장하고 나가기" 버튼 클릭 이벤트 설정
        val saveBtn: Button = view.findViewById(R.id.save_btn)
        saveBtn.setOnClickListener {
            val indexToShow = backStackCount - 2 // index-1 값
            if (indexToShow >= 0) {
                // 해당 인덱스에 해당하는 백 스택 항목 가져오기
                val backStackEntry = parentFragmentManager.getBackStackEntryAt(indexToShow)
                // 해당 백 스택 항목의 태그 가져오기
                val fragmentTag = backStackEntry.name
                // 해당 태그를 사용하여 프래그먼트 찾기
                val fragmentToShow = parentFragmentManager.findFragmentByTag(fragmentTag)
                // 프래그먼트가 null이 아니면 보여주기
                Log.d("BackStack", "StorageDialogFragment ： $indexToShow , $fragmentTag")

                // 값을 전달할 번들 생성
                val bundle = Bundle().apply {
                    putString("fragmentTag", fragmentTag)
                    putInt("indexToShow", indexToShow)
                }
                // HomeDialogFragment로 번들 전달
                val homeDialogFragment = HomeDialogFragment().apply {
                    arguments = bundle
                }
                // HomeDialogFragment 표시
                homeDialogFragment.show(parentFragmentManager, "HomeDialogFragment")
                activity?.finish()

            }
        }

        return view
    }
}

