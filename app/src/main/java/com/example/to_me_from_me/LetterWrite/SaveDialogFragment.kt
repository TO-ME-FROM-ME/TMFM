import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.to_me_from_me.LetterWrite.SituationFragment
import com.example.to_me_from_me.LetterWrite.ViewModel
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.Mypage.SharedViewModel
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SaveDialogFragment : DialogFragment() {

    private lateinit var myViewModel: ViewModel
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var uid: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        val view = inflater.inflate(R.layout.save_dialog, container, false)

        myViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // 다이얼로그의 닫기 버튼 설정
        val closeIv: ImageView = view.findViewById(R.id.close_iv)
        closeIv.setOnClickListener {
            dismiss()
        }

        val new_btn: Button = view.findViewById(R.id.new_btn)
        val cwrite_btn: Button = view.findViewById(R.id.cwrite_btn)

        new_btn.setOnClickListener {
            uid?.let { userId ->
                firestore.collection("users").document(userId).collection("letters")
                    .whereEqualTo("storage", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Log.d("Firebase", "삭제할 문서가 없습니다.")
                        } else {
                            for (document in documents) {
                                document.reference.delete()
                            }
                        }
                    }
            }
            updateStorageField1(false)
            val intent = Intent(activity, WriteLetterActivity::class.java)
            startActivity(intent)
        }

        cwrite_btn.setOnClickListener {
            loadSavedLetter(object : OnLoadCompleteListener {
                override fun onLoadComplete(situation: String?, emoji: String?, ad1: String?, ad2: String?,
                                            q1: String?, q2: String?, q3: String?, letter: String?) {
                    updateStorageField1(false)

                    myViewModel.setSituation(situation ?: "")
                    myViewModel.setEmoji(emoji ?: "")
                    myViewModel.setAd1(ad1 ?: "")
                    myViewModel.setAd2(ad2 ?: "")
                    myViewModel.setQ1(q1 ?: "")
                    myViewModel.setQ2(q2 ?: "")
                    myViewModel.setQ3(q3 ?: "")
                    myViewModel.setLetter(letter ?: "")

                    deleteStorageLetters()

                    val intent = Intent(activity, WriteLetterActivity::class.java).apply {
                        putExtra("situation", situation)
                        putExtra("emoji", emoji)
                        putExtra("ad1", ad1)
                        putExtra("ad2", ad2)
                        putExtra("q1", q1)
                        putExtra("q2", q2)
                        putExtra("q3", q3)
                        putExtra("letter", letter)
                    }

                    startActivity(intent)
                    dismiss()

                }
            })
        }


        return view
    }

    private fun updateStorageField1(isStored: Boolean) {
        uid?.let { userId ->
            firestore.collection("users").document(userId)
                .update("storage", isStored)
        }
    }

    private fun deleteStorageLetters() {
        uid?.let { userId ->
            firestore.collection("users").document(userId).collection("letters")
                .whereEqualTo("storage", true)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.d("Firebase", "삭제할 문서가 없습니다.")
                    } else {
                        for (document in documents) {
                            document.reference.delete()
                        }
                    }
                }
        }
        }

    interface OnLoadCompleteListener {
        fun onLoadComplete(
            situation: String?, emoji: String?, ad1: String?, ad2: String?,
            q1: String?, q2: String?, q3: String?, letter: String?
        )
    }

    private fun loadSavedLetter(onLoadComplete: OnLoadCompleteListener) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid

            firestore.collection("users").document(userId).collection("letters")
                .whereEqualTo("storage", true)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.d("Firebase", "임시저장된 편지가 없습니다.")
                        onLoadComplete.onLoadComplete(null, null, null, null, null, null, null, null)
                    } else {
                        val document = documents.first()
                        // Firestore에서 값 가져오기
                        val situation = document.getString("situation")
                        val emoji = document.getString("emoji")
                        val ad1 = document.getString("ad1")
                        val ad2 = document.getString("ad2")
                        val q1 = document.getString("q1")
                        val q2 = document.getString("q2")
                        val q3 = document.getString("q3")
                        val letter = document.getString("letter")

                        // ViewModel에 저장하는 함수 호출
                        saveViewModel(myViewModel, situation, emoji, ad1, ad2, q1, q2, q3, letter)

                        // 로그로 확인
                        Log.d("FirestoreData", "Loaded and saved letter data.")
                        onLoadComplete.onLoadComplete(situation, emoji, ad1, ad2, q1, q2, q3, letter) // 값 전달
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "임시저장된 편지를 가져오는 중 오류 발생", e)
                    onLoadComplete.onLoadComplete(null, null, null, null, null, null, null, null) // 값 전달
                }
        }
    }


    private fun saveViewModel(
        viewModel: ViewModel,
        situation: String?,
        emoji: String?,
        ad1: String?,
        ad2: String?,
        q1: String?,
        q2: String?,
        q3: String?,
        letter: String?
    ) {
        viewModel.setSituation(situation ?: "")
        viewModel.setEmoji(emoji ?: "")
        viewModel.setAd1(ad1 ?: "")
        viewModel.setAd2(ad2 ?: "")
        viewModel.setQ1(q1 ?: "")
        viewModel.setQ2(q2 ?: "")
        viewModel.setQ3(q3 ?: "")
        viewModel.setLetter(letter ?: "")


        Log.d("FirestoreData", "ViewModel에 값이 설정되었습니다.")
    }
}

