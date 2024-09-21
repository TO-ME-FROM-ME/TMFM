package com.example.to_me_from_me.StatisticalReport

import android.graphics.Color
import android.os.Bundle
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

class MonthlyReportFragment : Fragment() {
    private var _binding: FragmentMonthlyReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userScoreTv: TextView

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

        val barChart = binding.barChart
        val barText = view.findViewById<TextView>(R.id.bar_chart_tv) // TextView 찾기
        configureChartAppearance(barChart, barText)


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
    }

    private fun configureChartAppearance(barChart: BarChart, barText: TextView) {
        // BarEntry 데이터 생성 (x, y 값을 설정)
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 100f)) // BarEntry 사용

        // BarDataSet 생성 및 설정
        val barDataSet = BarDataSet(entries, "감정 데이터")
        barDataSet.color = Color.parseColor("#FFBC9B") // 막대 색상 설정
        // BarData 생성 및 BarChart에 설정
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate() // 데이터 갱신 및 차트 리프레시

        // 첫 번째 막대의 값을 TextView에 표시
        val firstBarValue = entries[0].y.toInt()
        barText.text = "$firstBarValue%"

        // BarChart 설정
        barChart.isDragEnabled = false
        barChart.setTouchEnabled(false) // 터치 유무
        barChart.setScaleEnabled(false) // 확대 불가능
        barChart.setDrawGridBackground(false)   // 차트의 배경 그리드 비활성화
        barChart.setDrawBorders(false)  // 차트의 경계선 비활성화
        barChart.setPinchZoom(false)
        barChart.legend.isEnabled = false
        barChart.isEnabled = false
        barChart.setExtraOffsets(10f, 0f, 40f, 0f)

        // Description 숨기기
        barChart.description.isEnabled = false

        // XAxis 설정
        val xAxis: XAxis = barChart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.gridLineWidth = 15f
        xAxis.textSize = 0f
        xAxis.gridColor = Color.parseColor("#80E5E5E5")
        barDataSet.setDrawValues(false) // 막대 위의 값 숨기기
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "" // 레이블 숨기기
            }
        }

        // YAxis(Left) 설정
        val yAxisLeft = barChart.axisLeft
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.setDrawAxisLine(false)
        yAxisLeft.axisMinimum = 0f // 최솟값
        yAxisLeft.axisMaximum = 50f // 최댓값
        yAxisLeft.granularity = 0f // 값만큼 라인선 설정
        yAxisLeft.isEnabled = false
        yAxisLeft.setDrawLabels(false)

        // YAxis(Right) 설정
        val yAxisRight = barChart.axisRight
        yAxisRight.setDrawLabels(false)
        yAxisRight.setDrawAxisLine(false)
        yAxisRight.isEnabled = false
        yAxisRight.setDrawLabels(false)

        // ValueFormatter를 사용하여 값 숨기기
        barChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "" // 값 숨기기
            }
        }
        barChart.axisRight.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "" // 값 숨기기
            }
        }

        // 데이터 갱신 및 차트 리프레시
        barChart.invalidate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun loadUserScore() {
        val user = auth.currentUser

        if (user != null) {
            // Firestore에서 사용자 문서 참조
            val userRef = firestore.collection("users").document(user.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // "totalScore" 필드를 number 타입으로 불러옴
                        val score = document.getLong("totalScore") // 혹은 getDouble() 사용
                        if (score != null) {
                            if(score<20){
                                reportTv.text = "낮은"
                                reportTv.setTextColor(Color.parseColor("#FF7377"))
                                report2Tv.text = " 자존감을 갖고 있어."
                                userScoreTv.text = "${score.toString()}점"
                                reportEmoTv.text="\uD83D\uDE22"
                                scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph1))

                            }
                           else if(score<30) {
                                reportTv.text = "보통수준"
                                reportTv.setTextColor(Color.parseColor("#E1B735"))
                                report2Tv.text = "의 자존감을 갖고 있어."
                                userScoreTv.text = "${score.toString()}점"
                                reportEmoTv.text = "\uD83D\uDE03"
                                scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph2))
                            }else{
                                reportTv.text = "매우 높은"
                                reportTv.setTextColor(Color.parseColor("#45826B"))
                                report2Tv.text = " 자존감을 갖고 있어."
                                userScoreTv.text = "${score.toString()}점"
                                reportEmoTv.text = "\uD83E\uDD70"
                                scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph3))
                            }
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
}

