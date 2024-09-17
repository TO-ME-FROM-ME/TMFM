package com.example.to_me_from_me.StatisticalReport

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.R
import com.example.to_me_from_me.databinding.FragmentAnnualReportBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class AnnualReportFragment : Fragment() {

    private var _binding: FragmentAnnualReportBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnnualReportBinding.inflate(inflater, container, false)
        val view = binding.root

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
            val dialog = StatisticalTipDialogFragment()
            dialog.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialog.show(parentFragmentManager, "StatisticalTipDialogFragment")
        }


        val lineChart = binding.lineChart
        configureChartAppearance(lineChart, 0) // 범위 값을 설정하여 차트 구성
        setChartData(lineChart) // 차트에 데이터 설정



        val barChart = binding.barChart
        val barText = view.findViewById<TextView>(R.id.bar_chart_tv) // TextView 찾기
        configureChartAppearanceBar(barChart, barText)



        return view
    }

    private fun setChartData(lineChart: LineChart) {
        // y축 데이터 설정
        val entries = ArrayList<Entry>()
        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 18f))
        entries.add(Entry(3f, 13f))
        entries.add(Entry(4f, 15f))
        entries.add(Entry(5f, 25f))


        // 데이터 설정 - 필요한 데이터 세트 객체 생성
        val lineDataSet = LineDataSet(entries, "Sample Data")
        lineDataSet.color = Color.rgb(244,146,146)
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 3f
        lineDataSet.valueTextSize = 0f
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // 차트 업데이트
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    private fun configureChartAppearance(lineChart: LineChart, range: Int) {
        lineChart.extraBottomOffset = 15f
        lineChart.description.isEnabled = false
        lineChart.isDragEnabled=false   //드래그 불가능
        lineChart.setScaleEnabled(false) //확대 불가능
        lineChart.setTouchEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.legend.isEnabled=false

        // XAxis 설정
        val xAxis = lineChart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
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
        yAxisLeft.setDrawAxisLine(false)
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.textSize = 14f
        yAxisLeft.textColor = Color.rgb(163, 163, 163)
        yAxisLeft.axisLineWidth = 2f
        yAxisLeft.axisMinimum = 0f // 최솟값
        yAxisLeft.axisMaximum = 30f // 최댓값 설정
        yAxisLeft.granularity = 10f // 간격 설정

        val yAxis = lineChart.axisRight
        yAxis.setDrawLabels(false)
        yAxis.setDrawAxisLine(false)

    }

    private fun configureChartAppearanceBar(barChart: BarChart, barText: TextView) {
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
}
