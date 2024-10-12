package com.example.to_me_from_me

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.LetterWrite.SaveDialogFragment
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.Mailbox.MailboxActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeDialogFragment : DialogFragment() {
    private var fragmentTag: String? = null
    private var indexToShow: Int? = null
    private val tag = "HomeDialogFragment"
    override fun onStart() {
        super.onStart()
        // 다이얼로그 위치
        dialog?.window?.apply {
            val params = attributes
            params.x = 80
            params.y = -280
            attributes = params
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 번들에서 값 가져오기
        fragmentTag = arguments?.getString("fragmentTag")
        indexToShow = arguments?.getInt("indexToShow")
        Log.d("BackStack", "HomeDialogFragment: $indexToShow, $fragmentTag")

        // 레이아웃 초기화
        val view = inflater.inflate(R.layout.home_dialog, container, false)

        // 버튼 초기화 및 클릭 리스너 설정
        val writeMailIv = view.findViewById<Button>(R.id.writemail_iv)

        Log.d("BackStack", "1: $indexToShow, $fragmentTag")

        val firestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid


        if (uid != null) {
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    val storageField = document.getBoolean("storage")  // 'storage' 필드 값 가져오기

                    // storage 필드가 true인 경우
                    if (storageField == true) {
                        Log.d("BackStack", "2: $indexToShow, $fragmentTag")
                        writeMailIv.setOnClickListener {
                            Log.d("BackStack", "3: $indexToShow, $fragmentTag")
                            val saveDialogFragment = SaveDialogFragment().apply {
                                arguments = Bundle().apply {
                                    putString("fragmentTag", fragmentTag)
                                }
                            }
                            saveDialogFragment.setStyle(
                                DialogFragment.STYLE_NORMAL,
                                R.style.RoundedBottomSheetDialogTheme
                            )
                            saveDialogFragment.show(parentFragmentManager, "SaveDialogFragment")
                        }
                    } else {
                        // storage 필드가 true가 아니거나 없을 경우
                        writeMailIv.setOnClickListener {
                            Log.d("BackStack", "4: $indexToShow, $fragmentTag")
                            val intent = Intent(activity, WriteLetterActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다른 뷰들 초기화 및 설정
        val mailBoxIv: RelativeLayout = view.findViewById(R.id.mailbox_iv)
        mailBoxIv.setOnClickListener {
            val intent = Intent(requireContext(), MailboxActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentDialogTheme)
    }
}

