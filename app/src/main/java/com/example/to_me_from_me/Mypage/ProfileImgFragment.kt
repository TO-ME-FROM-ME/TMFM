package com.example.to_me_from_me.Mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.setFragmentResult
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileImgFragment : BottomSheetDialogFragment() {

    private var selectedImgResId: Int = R.drawable.ic_my_01
    private var selectedImageView: ImageView? = null

    private val imageResourceMap = mapOf(
        R.id.excited_btn to R.drawable.ic_my_01,
        R.id.happy_btn to R.drawable.ic_my_02,
        R.id.normal_btn to R.drawable.ic_my_03,
        R.id.upset_btn to R.drawable.ic_my_04,
        R.id.angry_btn to R.drawable.ic_my_05
    )

    private val selectedImageResourceMap = mapOf(
        R.id.excited_btn to R.drawable.ic_my_01_s,
        R.id.happy_btn to R.drawable.ic_my_02_s,
        R.id.normal_btn to R.drawable.ic_my_03_s,
        R.id.upset_btn to R.drawable.ic_my_04_s,
        R.id.angry_btn to R.drawable.ic_my_05_s
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profileimg, container, false)

        val imageViews = imageResourceMap.keys.map { view.findViewById<ImageView>(it) }

        val okButton = view.findViewById<Button>(R.id.ok_btn)
        okButton.setOnClickListener {
            setFragmentResult("profileImgKey", Bundle().apply {
                putInt("selectedImgResId", selectedImgResId)
            })
            dismiss()
        }

        // 초기 이미지 설정 및 클릭 리스너
        imageViews.forEach { imageView ->
            //imageView.setImageResource(imageResourceMap[imageView.id] ?: R.drawable.ic_my_01)
            imageView.setOnClickListener { updateImageSelection(imageView) }
        }

        // 선택된 이미지 강조
        updateImageSelection(view.findViewById(selectedImageView?.id ?: R.id.excited_btn))

        return view
    }

    // 선택된 이미지를 업데이트하는 함수
    private fun updateImageSelection(newImageView: ImageView) {
        // 선택된 이미지가 있다면 기본 상태로 되돌리기
        selectedImageView?.let {
            it.setImageResource(imageResourceMap[it.id] ?: R.drawable.ic_my_01)
            it.visibility = View.VISIBLE // 원래 이미지를 다시 보이게 함

        }

        // 새로운 이미지를 강조하고 숨기기
        newImageView.setImageResource(selectedImageResourceMap[newImageView.id] ?: R.drawable.ic_my_01_s)
        selectedImageView = newImageView

        // 현재 선택된 이미지 뷰 및 리소드 ID 업데이트
        selectedImageView = newImageView
        selectedImgResId = selectedImageResourceMap[newImageView.id] ?: R.drawable.ic_my_01_s
    }




    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme2).apply {
            setCanceledOnTouchOutside(true)
        }
    }
}
