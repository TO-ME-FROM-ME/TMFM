package com.example.to_me_from_me.Mailbox

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
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

        val mainDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.select_solid)

        val gridLayout: GridLayout = view.findViewById(R.id.month_grid)
        val monthTextViews = gridLayout.children.filterIsInstance<TextView>()
        monthTextViews.forEach { textView ->
            textView.setOnClickListener {
                // 모든 TextView의 배경색상을 기본 색상으로 변경
                monthTextViews.forEach { tv -> tv.setBackgroundResource(R.drawable.solid_no_stroke) }

                // 클릭된 TextView의 배경색상을 main 색상으로 변경
                textView.background = mainDrawable
            }
        }

    }
}
