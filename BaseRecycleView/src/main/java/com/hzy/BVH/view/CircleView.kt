package com.hzy.BVH.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.hzy.BVH.R

/**
 * User: hzy
 * Date: 2020/9/20
 * Time: 10:47 AM
 * Description: 圆形ImageView
 */
class CircleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var radius: Float

    init {
        val type = getContext().obtainStyledAttributes(attrs, R.styleable.CircleView)
        radius = type.getDimension(R.styleable.CircleView_radius, 0f)
        type.recycle()
    }

    private val paint by lazy {
        Paint()
    }


    private val inXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        val count = canvas.saveLayer(paddingLeft.toFloat(), paddingTop.toFloat(), paddingLeft + radius * 2, paddingTop + radius * 2, paint)
        canvas.drawCircle(paddingLeft.toFloat() + radius, paddingTop.toFloat() + radius, radius, paint)
        paint.xfermode = inXfermode
        canvas.drawBitmap(drawable2Bitmap(drawable, radius.toInt() * 2), paddingLeft.toFloat(), paddingTop.toFloat(), paint)
        paint.xfermode = null
        canvas.restoreToCount(count)
    }


    private fun drawable2Bitmap(drawable: Drawable, width: Int): Bitmap {
        // 建立对应 bitmap
        val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, width)
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)
        return bitmap
    }


}