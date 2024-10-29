package com.example.to_me_from_me.StatisticalReport

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.R
import com.example.to_me_from_me.databinding.FragmentAnnualReportBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnnualReportFragment : Fragment(), AnnualPickerDialogFragment.YearSelectionListener {

    private var _binding: FragmentAnnualReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardView1Tv: TextView
    private lateinit var cardView2Tv: TextView
    private lateinit var cardView3Tv: TextView

    private lateinit var pbLIv: ImageView
    private lateinit var pb1Iv: ImageView
    private lateinit var pb2Iv: ImageView
    private lateinit var pb3Iv: ImageView
    private lateinit var pb4Iv: ImageView


    private lateinit var framelayout : FrameLayout
    private lateinit var textlayout : LinearLayout


    private lateinit var firestore: FirebaseFirestore
    private val user = FirebaseAuth.getInstance().currentUser
    private val uid = user?.uid
    private var calendar: Calendar = Calendar.getInstance()

    private lateinit var yearTv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnnualReportBinding.inflate(inflater, container, false)
        val view = binding.root

        firestore = FirebaseFirestore.getInstance()

        framelayout = view.findViewById(R.id.annual_fl)

        pbLIv = view.findViewById(R.id.pb_l_iva)
        pb1Iv = view.findViewById(R.id.pb_1_iva)
        pb2Iv = view.findViewById(R.id.pb_2_iva)
        pb3Iv = view.findViewById(R.id.pb_3_iva)
        pb4Iv = view.findViewById(R.id.pb_4_iva)

        cardView1Tv = view.findViewById(R.id.adjective1_tva)
        cardView2Tv = view.findViewById(R.id.adjective2_tva)
        cardView3Tv = view.findViewById(R.id.adjective3_tva)

        textlayout = view.findViewById(R.id.year_ll)

        yearTv = view.findViewById<TextView>(R.id.year_text) // yearTextView의 ID를 사용해야 합니다.
        yearTv.text = "${calendar.get(Calendar.YEAR)}년"
        Log.d("annual", "yearTv -> : ${yearTv.text}")

        val tipButton = view.findViewById<ImageView>(R.id.annual_tip1)
        tipButton.setOnClickListener {
            val dialog = StatisticalMonthTipDialogFragment()
            dialog.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialog.show(parentFragmentManager, "StatisticalMonthTipDialogFragment")
        }

        val tipButton2 = view.findViewById<ImageView>(R.id.annual_tip2)
        tipButton2.setOnClickListener {
            val dialog = StatisticalTipYDialogFragment()
            dialog.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialog.show(parentFragmentManager, "StatisticalTipYDialogFragment")
        }

        val lineChart = binding.lineChart
        configureChartAppearance(lineChart, 0) // 범위 값을 설정하여 차트 구성
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        fetchScoresFromFirestore(lineChart, currentYear)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        //val yearIv = view.findViewById<ImageView>(R.id.year_iv)
        loadUserGraph()

        textlayout.setOnClickListener {
            val dialogFragment = AnnualPickerDialogFragment(
                selectedYear = calendar.get(Calendar.YEAR)
            )
            dialogFragment.setTargetFragment(this, 0)
            Log.d("annual", "리포트 넘길때 -> : ${calendar.get(Calendar.YEAR)}")
            dialogFragment.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialogFragment.show(parentFragmentManager, "YearPickerDialogFragment")
        }
    }

    private fun setChartData(lineChart: LineChart, entries: ArrayList<Entry>) {
        // 데이터 세트를 설정
        val lineDataSet = LineDataSet(entries, "Scores Data")
        lineDataSet.color = Color.rgb(244, 146, 146)
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 3f
        lineDataSet.valueTextSize = 0f
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // 차트를 업데이트하여 반영
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    private fun fetchScoresFromFirestore(lineChart: LineChart, selectedYear: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()

        if (user == null) {
            Log.e("Firestore", "User UID is null")
            return
        }

        val scoresRef = firestore.collection("users").document(user.uid).collection("scores")

        // Firestore에서 데이터를 가져옴
        scoresRef.orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val entries = ArrayList<Entry>()

                // Firestore에서 가져온 각 문서를 처리
                for (document in documents) {
                    val documentId = document.id // "YYYY-MM" 형식
                    val score = document.getDouble("score")?.toFloat() ?: 0f

                    // 문서 ID에서 연도와 월을 추출
                    val parts = documentId.split("-")
                    val year = parts[0].toInt()
                    val month = parts[1].toFloat() - 1 // 0부터 시작하도록 -1

                    // 선택된 연도에 맞는 데이터만 추가
                    if (year == selectedYear) {
                        // Entry 객체에 x축: month, y축: score 값으로 추가
                        entries.add(Entry(month, score))
                        // 로그 확인
                        Log.d("Firestore", "Year: $year, Month: $month, Score: $score")
                    }
                }

                // 차트에 데이터 설정
                setChartData(lineChart, entries)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents: ", exception)
            }
    }



    private fun configureChartAppearance(lineChart: LineChart, range: Int) {
        lineChart.extraBottomOffset = 15f
        lineChart.description.isEnabled = false
        lineChart.isDragEnabled = false   // 드래그 불가능
        lineChart.setScaleEnabled(false) // 확대 불가능
        lineChart.setTouchEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.legend.isEnabled = false

        // XAxis 설정
        val xAxis = lineChart.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineWidth = 3f
        xAxis.axisLineColor = Color.rgb(163, 163, 163)
        xAxis.granularity = 1f
        xAxis.textSize = 14f
        xAxis.textColor = Color.rgb(118, 118, 118)
        xAxis.spaceMin = 0f
        xAxis.spaceMax = 0f
        // x축의 범위를 0부터 11까지 설정
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 11f
        xAxis.setLabelCount(12, true) // 라벨 개수를 12개로 고정
        // 월별 레이블 포맷터 설정
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val month = value.toInt()
                return if (month in 0..11) {
                    (month + 1).toString().padStart(2, '0')
                } else {
                    ""
                }
            }
        }

        // YAxis 설정
        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.setDrawAxisLine(true)
        yAxisLeft.axisLineColor = Color.rgb(163, 163, 163) // Y축 테두리 색상 설정
        yAxisLeft.axisLineWidth = 3f // Y축 테두리 두께 설정
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.textSize = 14f
        yAxisLeft.textColor = Color.rgb(163, 163, 163)
        yAxisLeft.axisMinimum = 0f // 최솟값
        yAxisLeft.axisMaximum = 37f // 최댓값 설정
        yAxisLeft.granularity = 10f // 간격 설정

        // Y축 레이블에서 0을 숨기기 위한 설정
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    10 -> "10"
                    20 -> "20"
                    30 -> "30"
                    else -> "" // 0은 숨김
                }
            }
        }

        val yAxisRight = lineChart.axisRight
        yAxisRight.setDrawLabels(false)
        yAxisRight.setDrawAxisLine(false)
        yAxisRight.setDrawGridLines(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onYearSelected(year: Int) {
        calendar.set(Calendar.YEAR, year)
        yearTv.text = "${calendar.get(Calendar.YEAR)}년"
        Log.d("annual", "년이 업데이트되었습니다: ${yearTv.text}년")
        // 차트 데이터 업데이트 메서드 호출
        //setChartData(binding.lineChart)
        fetchScoresFromFirestore(binding.lineChart, year)
        loadUserGraph()
    }

    private fun loadUserGraph() {
        val selectedYear = calendar.get(Calendar.YEAR)
        val displayedYear = yearTv.text.toString().replace("년", "").toInt()

        Log.d("annual", "selectedYear > $selectedYear ,displayedYear > $displayedYear ")


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

                                    // 선택된 연도와 월에 맞는 데이터만 필터링
                                    if (docYear == selectedYear) {

                                        val emoji = document.getString("emoji")
                                        if (emoji != null) {
                                            when (emoji) {
                                                "excited_s" -> emojiCounts[0]++ // excited
                                                "happy_s" -> emojiCounts[1]++ // happy
                                                "normal_s" -> emojiCounts[2]++ // normal
                                                "upset_s" -> emojiCounts[3]++ // sad
                                                "angry_s" -> emojiCounts[4]++ // upset
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
                            Log.d("annual", "annual의 값이 없습니다.")
                            framelayout.isVisible = false
                        } else {
                            // 비율 계산 및 ProgressBar 업데이트
                            framelayout.isVisible = true
                            AnnualupdateProgressBars(emojiCounts, totalCount)
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
                        Log.d("annual", " $topThreeAds")
                    } else {
                        framelayout.isEnabled =false
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("UserPref", "Error getting documents: ", e)
                }
        }
    }

    private fun AnnualupdateProgressBars(emojiCounts: IntArray, totalCount: Int) {
        val percentages =
            emojiCounts.map { if (totalCount > 0) (it.toFloat() / totalCount) * 100 else 0f }

        // 이모지 순서 정의 (excited_s, happy_s, normal_s, sad_s, upset_s 순)
        val emojiOrder = listOf("excited_s", "happy_s", "normal_s", "upset_s", "angry_s")

        // 이모지 이름과 퍼센티지 값 쌍으로 만들기
        val emojiPercentagePairs = emojiOrder.zip(percentages)

        // 퍼센티지 내림차순으로 정렬, 동일 퍼센티지일 경우 emojiOrder 순서에 맞춰 정렬
        val sortedEmojiPercentagePairs =
            emojiPercentagePairs.sortedWith(compareByDescending<Pair<String, Float>> { it.second }.thenBy {
                emojiOrder.indexOf(it.first)
            })
        Log.d("annual", " $sortedEmojiPercentagePairs")


        // 각 ProgressBar, TextView, ImageView에 순서대로 적용
        sortedEmojiPercentagePairs.forEachIndexed { index, (emojiName, percentage) ->
            val aProgressBar = when (index) {
                0 -> binding.pbL
                1 -> binding.pb1
                2 -> binding.pb2
                3 -> binding.pb3
                4 -> binding.pb4
                else -> null
            }

            val aProgressTextView = when (index) {
                0 -> binding.pbLTv
                1 -> binding.pb1Tv
                2 -> binding.pb2Tv
                3 -> binding.pb3Tv
                4 -> binding.pb4Tv
                else -> null
            }

            val aProcessImg = when (index) {
                0 -> binding.pbLIva
                1 -> binding.pb1Iva
                2 -> binding.pb2Iva
                3 -> binding.pb3Iva
                4 -> binding.pb4Iva
                else -> null
            }

            // ProgressBar와 TextView 업데이트
            aProgressBar?.progress = percentage.toInt()
            aProgressTextView?.text = "${percentage.toInt()}%"

            // 각 emoji에 맞는 이미지 설정
            val emojiResId = when (emojiName) {
                "excited_s" -> R.drawable.ic_my_01_s
                "happy_s" -> R.drawable.ic_my_02_s
                "normal_s" -> R.drawable.ic_my_03_s
                "upset_s" -> R.drawable.ic_my_04_s
                "angry_s" -> R.drawable.ic_my_05_s
                else -> R.drawable.ic_my_01_s
            }
            aProcessImg?.setImageResource(emojiResId)

            // ProgressBar의 Drawable 설정
            val aDrawableResId = when (emojiName) {
                "excited_s" -> R.drawable.progress_exited
                "happy_s" -> R.drawable.progress_happy
                "normal_s" -> R.drawable.progress_normal
                "upset_s" -> R.drawable.progress_sad
                "angry_s" -> R.drawable.progress_upset
                else -> R.drawable.progress_exited
            }
            aProgressBar?.progressDrawable = requireContext().getDrawable(aDrawableResId)
        }
    }
}
