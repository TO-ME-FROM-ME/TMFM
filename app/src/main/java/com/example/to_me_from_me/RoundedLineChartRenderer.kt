package com.example.to_me_from_me.StatisticalReport // 패키지 선언

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class RoundedLineChartRenderer(
    private val chart: LineChart,
    private val animator: ChartAnimator,
    private val viewPortHandler: ViewPortHandler
) : LineChartRenderer(chart, animator, viewPortHandler) {

    private val roundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f // X축의 라인 두께
        isAntiAlias = true
        color = Color.rgb(163, 163, 163)
    }


    override fun drawExtras(c: Canvas) {
        super.drawExtras(c)

        // X축의 왼쪽 및 오른쪽 둥글기 반경
        val cornerRadius = 10f

        // X축의 시작과 끝 좌표 계산
        val left = viewPortHandler.contentLeft()
        val right = viewPortHandler.contentRight()
        val yPos = chart.axisLeft.axisMinimum // X축의 y좌표

        // X축 선 그리기
        c.drawLine(left + cornerRadius, yPos, right - cornerRadius, yPos, roundPaint)

    }

}
