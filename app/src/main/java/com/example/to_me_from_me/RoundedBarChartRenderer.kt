package com.example.to_me_from_me

import android.graphics.*
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private var mRadius = 0

    fun setRadius(radius: Int) {
        this.mRadius = radius
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        mShadowPaint.color = dataSet.barShadowColor
        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        // Initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        for (j in (0..buffer.size() - 4).reversed() step 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
            if (!isSingleColor) {
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            // Create rounded rect path
            val path = roundRect(
                RectF(
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    buffer.buffer[j + 2],
                    buffer.buffer[j + 3]
                ),
                mRadius.toFloat(),
                mRadius.toFloat(),
                true,
                true,
                true,
                true
            )
            c.drawPath(path, mRenderPaint)
            if (drawBorder) {
                val borderPath = roundRect(
                    RectF(
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ),
                    mRadius.toFloat(),
                    mRadius.toFloat(),
                    true,
                    true,
                    true,
                    true
                )
                c.drawPath(borderPath, mBarBorderPaint)
            }
        }
    }

    private fun roundRect(rect: RectF, rx: Float, ry: Float, tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean): Path {
        val path = Path()

        // rx와 ry를 변경할 수 있는 var로 선언
        var cornerRadiusX = rx
        var cornerRadiusY = ry

        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom

        val width = right - left
        val height = bottom - top

        // 너무 큰 반경을 조정
        if (cornerRadiusX < 0) cornerRadiusX = 0f
        if (cornerRadiusY < 0) cornerRadiusY = 0f
        if (cornerRadiusX > width / 2) cornerRadiusX = width / 2
        if (cornerRadiusY > height / 2) cornerRadiusY = height / 2

        val heightMinusCorners = height - 2 * cornerRadiusY
        val widthMinusCorners = width - 2 * cornerRadiusX

        path.moveTo(left + cornerRadiusX, top)

        if (tl) {
            path.rQuadTo(-cornerRadiusX, 0f, -cornerRadiusX, -cornerRadiusY) // top-left corner
        } else {
            path.rLineTo(-cornerRadiusX, 0f)
            path.rLineTo(0f, -cornerRadiusY)
        }

        path.rLineTo(0f, heightMinusCorners)

        if (bl) {
            path.rQuadTo(0f, cornerRadiusY, cornerRadiusX, cornerRadiusY) // bottom-left corner
        } else {
            path.rLineTo(0f, cornerRadiusY)
            path.rLineTo(cornerRadiusX, 0f)
        }

        path.rLineTo(widthMinusCorners, 0f)

        if (br) {
            path.rQuadTo(cornerRadiusX, 0f, cornerRadiusX, -cornerRadiusY) // bottom-right corner
        } else {
            path.rLineTo(cornerRadiusX, 0f)
            path.rLineTo(0f, -cornerRadiusY)
        }

        path.rLineTo(0f, -heightMinusCorners)

        if (tr) {
            path.rQuadTo(0f, -cornerRadiusY, -cornerRadiusX, -cornerRadiusY) // top-right corner
        } else {
            path.rLineTo(0f, -cornerRadiusY)
            path.rLineTo(-cornerRadiusX, 0f)
        }

        path.close()
        return path
    }
}

