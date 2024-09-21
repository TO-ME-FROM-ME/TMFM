package com.example.to_me_from_me.StatisticalReport

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart:HorizontalBarChart,
    animator: com.github.mikephil.charting.animation.ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val radius = 10f // 둥근 모서리 반경

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        Log.d("RoundedBarChartRenderer", "drawDataSet: $dataSet, $index")

        val barDataSet = dataSet as BarDataSet
        // 좌표 변환기 가져오기
        val trans = mChart.getTransformer(dataSet.axisDependency)

        // 막대 버퍼 준비
        val buffer = mBarBuffers[index]
        trans.pointValuesToPixel(buffer.buffer)


        // 각 막대의 개수에 따라 반복
        for (i in 0 until buffer.buffer.size / 4) {
            val left = buffer.buffer[i * 4]
            val top = buffer.buffer[i * 4 + 1]
            val right = buffer.buffer[i * 4 + 2]
            val bottom = buffer.buffer[i * 4 + 3]


            // RectF 객체 생성
            val rectF = RectF(left, top, right, bottom)

            // 좌표 디버깅 출력
            Log.d("RoundedBarChartRenderer", "rectF: $rectF")

            // 둥근 모서리 그리기
            c.drawRoundRect(rectF, radius, radius, mRenderPaint)
        }
    }
}
