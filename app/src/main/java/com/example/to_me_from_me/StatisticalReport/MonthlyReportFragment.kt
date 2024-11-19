package com.example.to_me_from_me.StatisticalReport

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.LetterWrite.LetterFragment
import com.example.to_me_from_me.LetterWrite.WriteLetterActivity
import com.example.to_me_from_me.Mailbox.MonthPickerDialogFragment
import com.example.to_me_from_me.Mypage.DeleteAccActivity
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
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid


    private lateinit var userScoreTv: TextView

    private var calendar: Calendar = Calendar.getInstance()

    private lateinit var monthTv: TextView
    private lateinit var letterBtn : Button


    private lateinit var cardView1Tv : TextView
    private lateinit var cardView2Tv : TextView
    private lateinit var cardView3Tv : TextView

    private lateinit var reportTv: TextView
    private lateinit var reportEmoTv: TextView
    private lateinit var scoreImage: ImageView

    private lateinit var report2Tv: TextView

    private lateinit var adjectiveFl : FrameLayout
    private lateinit var monthlayout : LinearLayout

    private lateinit var pbLIv : ImageView
    private lateinit var pb1Iv : ImageView
    private lateinit var pb2Iv : ImageView
    private lateinit var pb3Iv : ImageView
    private lateinit var pb4Iv : ImageView


    // 선택된 연도와 월을 저장하는 변수
    private var selectedYear = calendar.get(Calendar.YEAR)
    private var selectedMonth = calendar.get(Calendar.MONTH)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonthlyReportBinding.inflate(inflater, container, false)
        val view = binding.root

        adjectiveFl = view.findViewById(R.id.adjective_fl)
        letterBtn = view.findViewById(R.id.letter_btn)

        monthTv = view.findViewById<TextView>(R.id.year_month_text)
        monthTv.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        monthlayout =view.findViewById(R.id.month_ll)

        pbLIv = view.findViewById(R.id.pb_l_iv)
        pb1Iv = view.findViewById(R.id.pb_1_iv)
        pb2Iv = view.findViewById(R.id.pb_2_iv)
        pb3Iv = view.findViewById(R.id.pb_3_iv)
        pb4Iv = view.findViewById(R.id.pb_4_iv)

        cardView1Tv = view.findViewById(R.id.adjective1_tv)
        cardView2Tv = view.findViewById(R.id.adjective2_tv)
        cardView3Tv = view.findViewById(R.id.adjective3_tv)

        letterBtn.setOnClickListener {
            startActivity(Intent(activity, WriteLetterActivity::class.java))
        }

        //val datePicker = view.findViewById<ImageView>(R.id.month_down_iv)
        monthlayout.setOnClickListener {
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


    override fun onMonthSelected2(month: Int, year: Int) {

        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year) // 선택한 연도 설정 추가

        selectedYear = year
        selectedMonth = month+1

        Log.d("selectedMonth","selectedYear : $selectedYear, selectedMonth $selectedMonth")

        monthTv.text = "${calendar.get(Calendar.YEAR)}년 ${month+1}월"
        Log.d("MonthPicker","monthTv $monthTv")
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

    if (user != null) {
        val dateString = "$selectedYear-$selectedMonth" // 예: 2024-11

        firestore.collection("scores")
            .document(dateString)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 로그: 문서가 존재하는지 확인
                    Log.d("loadUserScore", "Document exists: $document")

                    // score 값을 숫자로 가져오기
                    val score = document.getLong("score") // score 필드를 Long 형식으로 가져옵니다.
                    // 로그: score 값 확인
                    Log.d("loadUserScore", "Score: $score")

                    if (score != null) {
                        updateScoreUI(score)
                    } else {
                        userScoreTv.text = "Score not available"
                        Log.d("loadUserScore", "Score field is missing or not a number")
                    }
                } else {
                    // 문서가 없는 경우 로그
                    Log.d("loadUserScore", "Document does not exist.")
                    userScoreTv.text = "No data available"
                }
            }
            .addOnFailureListener { e ->
                // 실패 시 로그
                Log.d("loadUserScore", "Failed to load data: ${e.message}")
                Toast.makeText(activity, "데이터 로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } else {
        userScoreTv.text = "No user found"
        Log.d("loadUserScore", "No user is currently signed in.")
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
                        val adCounts = mutableMapOf<String, Int>()

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
                                        adjectiveFl.isVisible = true
                                        letterBtn.isVisible = false // 데이터가 있을 경우 버튼 숨기기

                                        val emoji = document.getString("emoji")
                                        if (emoji != null) {
                                            when (emoji) {
                                                "excited_s1" -> emojiCounts[0]++ // excited
                                                "happy_s1" -> emojiCounts[1]++ // happy
                                                "normal_s1" -> emojiCounts[2]++ // normal
                                                "upset_s1" -> emojiCounts[3]++ // sad
                                                "angry_s1" -> emojiCounts[4]++ // upset
                                            }
                                            totalCount++

                                            val ad1 = document.getString("ad1")
                                            val ad2 = document.getString("ad2")

                                            if (ad1 != null) {
                                                adCounts[ad1] = adCounts.getOrDefault(ad1, 0) + 1
                                            }

                                            if (ad2 != null) {
                                                adCounts[ad2] = adCounts.getOrDefault(ad2, 0) + 1
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("UserPref", "Date parsing error: ${e.message}")
                                }
                            }
                        }

                        // 데이터가 없으면 버튼 보여주기
                        if (totalCount == 0) {
                            adjectiveFl.isVisible = false // adjectiveFl 숨기기
                            letterBtn.isVisible = true // 버튼 보여주기
                        } else {
                            // 비율 계산 및 ProgressBar 업데이트
                            updateProgressBars(emojiCounts, totalCount)
                            // 버튼 숨기기
                            letterBtn.isVisible = false
                        }

                        // 빈도수를 기준으로 정렬
                        val sortedAds = adCounts.toList().sortedByDescending { it.second }
                        // 상위 3개 선택
                        val topThreeAds = sortedAds.take(3)

                        // 각 TextView에 결과 표시
                        when (topThreeAds.size) {
                            0 -> {
                                cardView1Tv.text = ""
                                cardView2Tv.text = ""
                                cardView3Tv.text = ""
                            }
                            1 -> {
                                cardView1Tv.text = topThreeAds[0].first
                                cardView2Tv.text = ""
                                cardView3Tv.text = ""
                            }
                            2 -> {
                                cardView1Tv.text = topThreeAds[0].first
                                cardView2Tv.text = topThreeAds[1].first
                                cardView3Tv.text = ""
                            }
                            else -> {
                                cardView1Tv.text = topThreeAds[0].first
                                cardView2Tv.text = topThreeAds[1].first
                                cardView3Tv.text = topThreeAds[2].first
                            }
                        }
                        Log.d("MtopThreeAds", " $topThreeAds")
                    } else {
                        // 문서가 없는 경우 버튼 보여주기
                        adjectiveFl.isVisible = false // adjectiveFl 숨기기
                        letterBtn.isVisible = true // 버튼 보여주기
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("UserPref", "Error getting documents: ", e)
                }
        }
    }


    private fun updateProgressBars(emojiCounts: IntArray, totalCount: Int) {
        // 퍼센티지 계산
        val percentages = emojiCounts.map { if (totalCount > 0) (it.toFloat() / totalCount) * 100 else 0f }

        // 이모지 순서 정의 (excited_s, happy_s, normal_s, sad_s, upset_s 순)
        val emojiOrder = listOf("excited_s1", "happy_s1", "normal_s1", "upset_s1", "angry_s1")

        // 이모지 이름과 퍼센티지 값 쌍으로 만들기
        val emojiPercentagePairs = emojiOrder.zip(percentages)

        // 퍼센티지 내림차순으로 정렬, 동일 퍼센티지일 경우 emojiOrder 순서에 맞춰 정렬
        val sortedEmojiPercentagePairs = emojiPercentagePairs.sortedWith(compareByDescending<Pair<String, Float>> { it.second }.thenBy { emojiOrder.indexOf(it.first) })
        Log.d("ProgressList"," $sortedEmojiPercentagePairs")



        // 각 ProgressBar, TextView, ImageView에 순서대로 적용
        sortedEmojiPercentagePairs.forEachIndexed { index, (emojiName, percentage) ->
            val progressBar = when (index) {
                0 -> binding.pbL
                1 -> binding.pb1
                2 -> binding.pb2
                3 -> binding.pb3
                4 -> binding.pb4
                else -> null
            }

            val progressTextView = when (index) {
                0 -> binding.pbLTv
                1 -> binding.pb1Tv
                2 -> binding.pb2Tv
                3 -> binding.pb3Tv
                4 -> binding.pb4Tv
                else -> null
            }

            val processImg = when (index) {
                0 -> binding.pbLIv
                1 -> binding.pb1Iv
                2 -> binding.pb2Iv
                3 -> binding.pb3Iv
                4 -> binding.pb4Iv
                else -> null
            }

            // ProgressBar와 TextView 업데이트
            progressBar?.progress = percentage.toInt()
            progressTextView?.text = "${percentage.toInt()}%"

            // 각 emoji에 맞는 이미지 설정
            val emojiResId = when (emojiName) {
                "excited_s1" -> R.drawable.ic_my_01_s
                "happy_s1" -> R.drawable.ic_my_02_s
                "normal_s1" -> R.drawable.ic_my_03_s
                "upset_s1" -> R.drawable.ic_my_04_s
                "angry_s1" -> R.drawable.ic_my_05_s
                else -> R.drawable.ic_my_01_s
            }
            processImg?.setImageResource(emojiResId)

            // ProgressBar의 Drawable 설정
            val drawableResId = when (emojiName) {
                "excited_s1" -> R.drawable.progress_exited
                "happy_s1" -> R.drawable.progress_happy
                "normal_s1" -> R.drawable.progress_normal
                "upset_s1" -> R.drawable.progress_sad
                "angry_s1" -> R.drawable.progress_upset
                else -> R.drawable.progress_exited
            }
            progressBar?.progressDrawable = requireContext().getDrawable(drawableResId)
        }
    }



}

