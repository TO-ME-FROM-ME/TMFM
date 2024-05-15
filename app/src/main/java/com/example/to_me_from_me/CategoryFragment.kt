package com.example.to_me_from_me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        // 여기에서 버튼들에 대한 클릭 리스너를 설정할 수 있습니다.
        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {
            // 다음 버튼이 클릭되었을 때 수행할 동작을 여기에 작성합니다.
        }

        // 필요한 경우 다른 버튼들에 대한 클릭 리스너를 설정할 수 있습니다.

        return view
    }
}
