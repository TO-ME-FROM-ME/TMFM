package com.example.to_me_from_me.LetterWrite

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.Timestamp

class TimePickerDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_SELECTED_DATE = "selectedDate"

        fun newInstance(selectedDate: String): TimePickerDialogFragment {
            val fragment = TimePickerDialogFragment()
            val args = Bundle()
            args.putString(ARG_SELECTED_DATE, selectedDate)
            fragment.arguments = args
            return fragment
        }
    }

    private val sharedViewModel: ViewModel by activityViewModels()
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(ARG_SELECTED_DATE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.time_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timePicker: CustomTimePicker = view.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(false)

        val okButton: Button = view.findViewById(R.id.ok_btn)
        okButton.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val selectedTime = "$hour:$minute"

            saveTimeToFirestore(hour, minute)

            val nextFragment = RecorderFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()

            dismiss()
        }

        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun saveTimeToFirestore(hour: Int, minute: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val currentDate = sharedViewModel.currentDate.value
        val selectedDateStr = selectedDate

        if (user != null && currentDate != null && selectedDateStr != null) {
            try {
                // 선택한 날짜와 시간 결합 (예: "2024-10-10 15:30")
                val dateTimeString = "$selectedDateStr $hour:$minute"
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date: Date = formatter.parse(dateTimeString) ?: throw IllegalArgumentException("잘못된 날짜 형식")

                val reservedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)

                // Firestore에 업데이트
                val userDocumentRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .collection("letters")
                    .document(currentDate)

                // Firestore에 저장할 데이터
                val letterData = mapOf<String, Any>(
                    "reservedate" to reservedDate
                )

                userDocumentRef.update(letterData)
                    .addOnSuccessListener {
                        Log.d("FirestoreUpdate", "성공적으로 시간 저장됨: $reservedDate")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreError", "시간 저장 실패: ", e)
                    }
            } catch (e: Exception) {
                Log.e("DateParseError", "오류 발생: ", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle("몇 시에 받고 싶어?")
        }
    }
}
