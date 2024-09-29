package com.example.to_me_from_me.StatisticalReport

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.Mailbox.MonthPickerDialogFragment
import com.example.to_me_from_me.R
import com.example.to_me_from_me.databinding.FragmentMonthlyReportBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.MONTH
import java.util.Date
import java.util.Locale

class MonthlyReportFragment : Fragment(), MonthPickerDialogFragment2.MonthSelectionListener2 {
    private var _binding: FragmentMonthlyReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userScoreTv: TextView

    private var calendar: Calendar = Calendar.getInstance()
    private var selectedDate: Date = calendar.time

    private lateinit var monthTv: TextView


    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    private lateinit var reportTv: TextView
    private lateinit var reportEmoTv: TextView
    private lateinit var scoreImage: ImageView

    private lateinit var report2Tv: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonthlyReportBinding.inflate(inflater, container, false)
        val view = binding.root

        monthTv = view.findViewById<TextView>(R.id.year_month_text)

        monthTv.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"




        val datePicker = view.findViewById<ImageView>(R.id.month_down_iv)
        datePicker.setOnClickListener {
            val dialogFragment = MonthPickerDialogFragment2(
                selectedYear = calendar.get(Calendar.YEAR),
                selectedMonth = calendar.get(Calendar.MONTH)
            )
            dialogFragment.setTargetFragment(this, 0)
            Log.d("MonthPicker","리포트 넘길때 -> ${calendar.get(Calendar.YEAR)}, ${calendar.get(Calendar.MONTH)+1}")
            dialogFragment.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialogFragment.show(parentFragmentManager, "MonthPickerDialogFragment2")
        }


        val tipButton = view.findViewById<ImageView>(R.id.month_tip1)
        tipButton.setOnClickListener {
            val dialog = StatisticalMonthTipDialogFragment()
            dialog.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialog.show(parentFragmentManager, "StatisticalMonthTipDialogFragment")
        }

        val tipButton2 = view.findViewById<ImageView>(R.id.month_tip2)
        tipButton2.setOnClickListener {
            val dialog = StatisticalTipMDialogFragment()
            dialog.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialog.show(parentFragmentManager, "StatisticalTipDialogFragment")
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        userScoreTv = view.findViewById(R.id.user_score_tv)

        reportTv = view.findViewById<TextView>(R.id.report_tv)
        reportEmoTv = view.findViewById<TextView>(R.id.report_emo_tv)
        scoreImage = view.findViewById<ImageView>(R.id.score_iv)

        report2Tv= view.findViewById<TextView>(R.id.report2_tv)

        loadUserScore()
        loadUserGraph()

    }


    override fun onMonthSelected2(month: Int) {
        calendar.set(Calendar.MONTH, month - 1)

        monthTv.text = "${calendar.get(Calendar.YEAR)}년 ${month}월"
        Log.d("MonthPicker", "월이 업데이트되었습니다: ${month}월") // 로그 추가
        // 선택된 월에 맞는 데이터를 불러옴
        loadUserScore()
        loadUserGraph()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



//    자아존중감 검사결과
    private fun loadUserScore() {
        val user = auth.currentUser
        val selectedMonth = calendar.get(Calendar.MONTH) + 1
        val selectedYear = calendar.get(Calendar.YEAR)


    if (user != null) {
            // Firestore에서 사용자 문서 참조
            val userRef = firestore.collection("users").document(user.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // "totalScore" 필드를 number 타입으로 불러옴
                        val score = document.getLong("totalScore") // 혹은 getDouble() 사용
                        if (score != null) {
                            updateScoreUI(score)
                        } else {
                            userScoreTv.text = " "
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "데이터 로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            userScoreTv.text = " "
        }
    }

    private fun updateScoreUI(score: Long) {
        if(score<20){
            reportTv.text = "낮은"
            report2Tv.text = " 자존감을 갖고 있어"
            userScoreTv.text = "${score.toString()}점"
            reportEmoTv.text="\uD83D\uDE22"
            scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph_01))

        }
        else if(score<30) {
            reportTv.text = "보통수준"
            report2Tv.text = "의 자존감을 갖고 있어"
            userScoreTv.text = "${score.toString()}점"
            reportEmoTv.text = "\uD83D\uDE03"
            scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph_02))
        }else{
            reportTv.text = "매우 높은"
            report2Tv.text = " 자존감을 갖고 있어"
            userScoreTv.text = "${score.toString()}점"
            reportEmoTv.text = "\uD83E\uDD70"
            scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph_03))
        }
    }


    //    감정통계
    private fun loadUserGraph() {
        val selectedMonth = calendar.get(Calendar.MONTH) + 1
        val selectedYear = calendar.get(Calendar.YEAR)

        if (uid != null) {
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val emojiCounts = IntArray(5)
                        var totalCount = 0

                        for (document in documents) {
                            val dateString = document.getString("date")
                            if (dateString != null) {
                                try {
                                    val date = sdf.parse(dateString)
                                    val docCalendar = Calendar.getInstance()
                                    docCalendar.time = date

                                    val docYear = docCalendar.get(Calendar.YEAR)
                                    val docMonth = docCalendar.get(Calendar.MONTH) + 1

                                    // 선택된 연도와 월에 맞는 데이터만 필터링
                                    if (docYear == selectedYear && docMonth == selectedMonth) {
                                        val emoji = document.getLong("emoji")?.toInt()
                                        if (emoji != null) {
                                            when (emoji) {
                                                2131165351 -> emojiCounts[0]++ // excited
                                                2131165355 -> emojiCounts[1]++ // happy
                                                2131165565 -> emojiCounts[2]++ // normal
                                                2131165612 -> emojiCounts[3]++ // sad
                                                2131165312 -> emojiCounts[4]++ // upset
                                            }
                                            totalCount++
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("UserPref", "Date parsing error: ${e.message}")
                                }
                            }
                        }

                        // 비율 계산 및 ProgressBar 업데이트
                        updateProgressBars(emojiCounts, totalCount)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("UserPref", "Error getting documents: ", e)
                }
        }
    }

    private fun updateProgressBars(emojiCounts: IntArray, totalCount: Int) {
        // 퍼센트 계산
        val percentages = emojiCounts.map { if (totalCount > 0) (it.toFloat() / totalCount) * 100 else 0f }

        // 큰 ProgressBar 업데이트
        binding.pbL.progress = percentages[0].toInt() // excited에 대한 ProgressBar
        binding.pbLTv.text = "${percentages[0].toInt()}%" // 텍스트 업데이트

        // 작은 ProgressBar 업데이트
        binding.pb1.progress = percentages[1].toInt() // happy에 대한 ProgressBar
        binding.pb1Tv.text = "${percentages[1].toInt()}%" // 텍스트 업데이트

        binding.pb2.progress = percentages[2].toInt() // normal에 대한 ProgressBar
        binding.pb2Tv.text = "${percentages[2].toInt()}%" // 텍스트 업데이트

        binding.pb3.progress = percentages[3].toInt() // sad에 대한 ProgressBar
        binding.pb3Tv.text = "${percentages[3].toInt()}%" // 텍스트 업데이트

        binding.pb4.progress = percentages[4].toInt() // upset에 대한 ProgressBar
        binding.pb4Tv.text = "${percentages[4].toInt()}%" // 텍스트 업데이트
    }


}

