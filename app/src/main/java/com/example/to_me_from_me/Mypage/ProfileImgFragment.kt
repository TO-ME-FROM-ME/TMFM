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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileImgFragment : BottomSheetDialogFragment() {

    private var selectedImgResId: Int = R.drawable.ic_my_01
    private var selectedImageView: ImageView? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val imageResourceMap = mapOf(
        R.id.excited_btn to R.drawable.excited2,
        R.id.happy_btn to R.drawable.happy2,
        R.id.normal_btn to R.drawable.normal2,
        R.id.upset_btn to R.drawable.upset2,
        R.id.angry_btn to R.drawable.angry2
    )

    private val selectedImageResourceMap = mapOf(
        R.id.excited_btn to R.drawable.excited_s1,
        R.id.happy_btn to R.drawable.happy_s1,
        R.id.normal_btn to R.drawable.normal_s1,
        R.id.upset_btn to R.drawable.upset_s1,
        R.id.angry_btn to R.drawable.angry_s1
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profileimg, container, false)
        val uid = auth.currentUser?.uid

        // Firestore에서 profileImage 필드를 가져와서 해당하는 버튼을 선택 상태로 설정
        uid?.let {
            firestore.collection("users").document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val profileImageNumber = document.getLong("profileImage")?.toInt() ?: 1
                        val selectedButtonId = when (profileImageNumber) {
                            2131165489 -> R.id.excited_btn
                            2131165491 -> R.id.happy_btn
                            2131165493 -> R.id.normal_btn
                            2131165495 -> R.id.upset_btn
                            2131165497 -> R.id.angry_btn
                            else -> R.id.excited_btn // 기본값
                        }
                        // 초기 선택된 이미지 강조
                        updateImageSelection(view.findViewById(selectedButtonId))
                    }
                }
        }

        val imageViews = imageResourceMap.keys.map { view.findViewById<ImageView>(it) }

        val okButton = view.findViewById<Button>(R.id.ok_btn)
        okButton.setOnClickListener {
            setFragmentResult("profileImgKey", Bundle().apply {
                putInt("selectedImgResId", selectedImgResId)
            })
            dismiss()
        }

        // 각 이미지 뷰에 클릭 리스너 추가
        imageViews.forEach { imageView ->
            imageView.setOnClickListener { updateImageSelection(imageView) }
        }

        return view
    }

    private fun updateImageSelection(newImageView: ImageView) {
        // 이전에 선택된 이미지가 있다면 원래 이미지로 변경
        selectedImageView?.let {
            it.setImageResource(imageResourceMap[it.id] ?: R.drawable.ic_my_01)
        }

        // 새로운 선택된 이미지 강조
        newImageView.setImageResource(selectedImageResourceMap[newImageView.id] ?: R.drawable.ic_my_01_s)
        selectedImageView = newImageView

        // 현재 선택된 이미지 리소스 ID 업데이트
        selectedImgResId = selectedImageResourceMap[newImageView.id] ?: R.drawable.ic_my_01_s
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.TransparentBottomSheetDialogTheme2).apply {
            setCanceledOnTouchOutside(true)
        }
    }
}
