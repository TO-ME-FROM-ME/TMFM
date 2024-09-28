package com.example.to_me_from_me.Mailbox

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.to_me_from_me.LetterWrite.ViewModel
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailMailBoxFragment : BottomSheetDialogFragment() {

    private val sharedViewModel: ViewModel by activityViewModels()
    private lateinit var dateTv1: TextView
    private lateinit var dateTv2: TextView
    private lateinit var dateIv: ImageView

    private var dateVisible: Boolean = false // date_text 가시성 제어 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_mailbox, container, false)

        dateTv1 = view.findViewById<TextView>(R.id.date1_tv)
        dateTv2 = view.findViewById<TextView>(R.id.date2_tv)
        dateIv = view.findViewById<ImageView>(R.id.date_iv)
        val layout = view.findViewById<LinearLayout>(R.id.custom_toast_container)

        // dateVisible에 따라 date_text 가시성 설정
        updateDateVisibility()

        return view
    }

    private fun updateDateVisibility() {
        val visibility = if (dateVisible) View.VISIBLE else View.GONE
        dateTv1.visibility = visibility
        dateTv2.visibility = visibility
        dateIv.visibility = visibility
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme)

    }
}