package com.example.to_me_from_me.Mailbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.R

class MonthPickerDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.month_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val monthGrid = view.findViewById<GridLayout>(R.id.month_grid)

        // 월 선택 리스너 설정
//        monthGrid.children.forEach { child ->
//            (child as? TextView)?.setOnClickListener {
//                val selectedMonth = (it as TextView).text.toString().replace("월", "").toInt()
//                // 선택한 월 처리
//                dismiss()
//            }
//        }
    }
}
