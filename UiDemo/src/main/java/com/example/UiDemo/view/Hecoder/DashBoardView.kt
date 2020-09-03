package com.example.UiDemo.view.Hecoder

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 * User: hzy
 * Date: 2020/9/3
 * Time: 11:39 PM
 * Description:
 */

const val openAngle: Int = 120

var DashScaleWidth: Float = 2.dp
var DashScaleHeight: Float = 5.dp


class DashBoardView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attributes, defStyleAttr) {

    private val paint by lazy {
        Paint().apply {
            color = Color.parseColor("#000000")
            style = Paint.Style.STROKE
            strokeWidth = 2.dp
            isAntiAlias = true
        }
    }
    private val DashWidth by lazy {
        width.toFloat().coerceAtMost(height.toFloat())
    }


    private val rectF by lazy {
        RectF(2.dp, 2.dp, DashWidth - 2.dp, DashWidth - 2.dp)
    }

    private val arcPath by lazy {
        Path().apply { addArc(rectF, (openAngle / 2 + 90).toFloat(), (360 - openAngle).toFloat()) }
    }
    private val pathMeasure = PathMeasure()
    private val dashPath = Path().apply { addRect(0f, 0f, DashScaleWidth, DashScaleHeight, Path.Direction.CCW) }

    private val pathDashPathEffect by lazy {
        PathDashPathEffect(dashPath, (pathMeasure.length - DashScaleWidth) / 20, 0f, PathDashPathEffect.Style.ROTATE)
    }

    private val LineLength by lazy { DashWidth / 2 - 10.dp }
    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        //画弧
        canvas.drawPath(arcPath, paint)
        //画刻度
        //计算path长度
        pathMeasure.setPath(arcPath, false)
        paint.pathEffect = pathDashPathEffect
        canvas.drawPath(arcPath, paint)
        paint.pathEffect = null
        //画指针
        val dashIndex = 5
        val angle = openAngle / 2 + 90
        val eachDashAngle = (360 - openAngle) / 20
        val rsAngle = Math.toRadians((angle + eachDashAngle * dashIndex).toDouble()).toFloat()
        canvas.drawLine(DashWidth / 2, DashWidth / 2, DashWidth / 2 + LineLength * cos(rsAngle), DashWidth / 2 + LineLength * sin(rsAngle), paint)
    }
}


val Int.dp: Float
    get() = TypedValue.applyDimension(COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

fun Int.dp() = TypedValue.applyDimension(COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
