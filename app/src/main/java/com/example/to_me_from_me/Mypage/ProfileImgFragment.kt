package com.example.to_me_from_me.Mypage


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileImgFragment : BottomSheetDialogFragment() {

    private var selectedImgResId: Int = R.drawable.ic_profile_01 // 초기값 설정
    private var selectedImageView: ImageView? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profileimg, container, false)
        view.isHorizontalScrollBarEnabled=false

        val excited = view.findViewById<ImageView>(R.id.excited_img)
        val happy = view.findViewById<ImageView>(R.id.happy_img)
        val normal = view.findViewById<ImageView>(R.id.normal_img)
        val upset = view.findViewById<ImageView>(R.id.upset_img)
        val angry = view.findViewById<ImageView>(R.id.angry_img)

        val okButton = view.findViewById<Button>(R.id.ok_btn)
        // OK 버튼 클릭 시 선택된 이미지 리소스 ID 전달하고 프래그먼트 닫기
        okButton.setOnClickListener {
            setFragmentResult("profileImgKey", Bundle().apply {
                putInt("selectedImgResId", selectedImgResId)
            })
            dismiss() // 프래그먼트 닫기
        }

        // 선택된 이미지에 해당하는 ImageView를 강조 처리
        when (selectedImgResId) {
            R.drawable.ic_profile_01_s -> excited.setImageResource(R.drawable.ic_profile_01_s)
            R.drawable.ic_profile_02_s -> happy.setImageResource(R.drawable.ic_profile_02_s)
            R.drawable.ic_profile_03_s -> normal.setImageResource(R.drawable.ic_profile_03_s)
            R.drawable.ic_profile_04_s -> upset.setImageResource(R.drawable.ic_profile_04_s)
            R.drawable.ic_profile_05_s -> angry.setImageResource(R.drawable.ic_profile_05_s)
        }


        // 각 이미지 클릭 시 동작 정의
        excited.setOnClickListener { updateImageSelection(excited, R.drawable.ic_profile_01_s) }
        happy.setOnClickListener { updateImageSelection(happy, R.drawable.ic_profile_02_s) }
        normal.setOnClickListener { updateImageSelection(normal, R.drawable.ic_profile_03_s) }
        upset.setOnClickListener { updateImageSelection(upset, R.drawable.ic_profile_04_s) }
        angry.setOnClickListener { updateImageSelection(angry, R.drawable.ic_profile_05_s) }

        // 초기 이미지 리소스 설정 (선택된 이미지가 없으므로 기본 이미지 설정)
        excited.setImageResource(R.drawable.ic_profile_01)
        happy.setImageResource(R.drawable.ic_profile_02)
        normal.setImageResource(R.drawable.ic_profile_03)
        upset.setImageResource(R.drawable.ic_profile_04)
        angry.setImageResource(R.drawable.ic_profile_05)

        return view
    }

    // 선택된 이미지를 업데이트하는 함수
    private fun updateImageSelection(newImageView: ImageView, selectedImageResource: Int) {
        // 이전에 선택된 이미지가 있으면 초기 상태로 되돌리기
        selectedImageView?.let { previousImageView ->
            previousImageView.setImageResource(getDefaultImageResource(previousImageView))
        }

        // 새로운 이미지 선택 처리
        newImageView.setImageResource(selectedImageResource)

        // 선택된 이미지 업데이트
        selectedImageView = newImageView
        selectedImgResId = selectedImageResource // 선택된 이미지 리소스 ID 업데이트
        Log.d("이미지","사용자 : $selectedImgResId")
    }


    private fun getDefaultImageResource(imageView: ImageView): Int {
        return when (imageView?.id) {
            R.id.excited_img -> R.drawable.ic_profile_01
            R.id.happy_img -> R.drawable.ic_profile_02
            R.id.normal_img -> R.drawable.ic_profile_03
            R.id.upset_img -> R.drawable.ic_profile_04
            R.id.angry_img -> R.drawable.ic_profile_05
            else -> R.drawable.ic_profile_01 // 기본 이미지
        }

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireActivity(),
            R.style.TransparentBottomSheetDialogTheme2
        )
        dialog.setCanceledOnTouchOutside(true)  // 배경 클릭 시 닫힘
        return dialog
    }

}

