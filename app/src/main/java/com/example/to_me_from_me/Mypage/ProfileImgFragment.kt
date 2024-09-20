package com.example.to_me_from_me.Mypage


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileImgFragment : BottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profileimg, container, false)
        view.isHorizontalScrollBarEnabled=false


        val okButton = view.findViewById<Button>(R.id.ok_btn)

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireActivity(),
            R.style.TransparentBottomSheetDialogTheme2
        )
        dialog.setCanceledOnTouchOutside(true)  // 배경 클릭 시 닫힘
        return dialog
    }

}

