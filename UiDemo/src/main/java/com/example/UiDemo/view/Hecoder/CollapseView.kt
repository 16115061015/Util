package com.example.UiDemo.view.Hecoder

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.UiDemo.R
import kotlin.math.min

/**
 * User: hzy
 * Date: 2020/9/13
 * Time: 10:30 PM
 * Description: 折叠显示View   熟悉图形变换api
 */
class CollapseView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val paint by lazy {
        Paint().apply {
            color = Color.parseColor("#000000")
        }
    }

    private val bitmap by lazy {
        getBitMap()
    }

    private val camera by lazy {
        Camera().apply {
            rotateX(30f)
            setLocation(0f, 0f, -4f * resources.displayMetrics.density)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(width / 2.toFloat(), height / 2.toFloat())
        canvas.rotate(-30f)
        canvas.clipRect(-bitmap.width.toFloat(), -bitmap.height.toFloat(), bitmap.width.toFloat(), 0f)
        canvas.rotate(30f)
        canvas.translate(-bitmap.width / 2.toFloat(), -bitmap.height / 2.toFloat())
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        canvas.restore()

        canvas.save()
        canvas.translate(width / 2.toFloat(), height / 2.toFloat())
        canvas.rotate(-30f)
        camera.applyToCanvas(canvas)
        canvas.clipRect(-bitmap.width.toFloat(), 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.rotate(30f)
        canvas.translate(-bitmap.width / 2.toFloat(), -bitmap.height / 2.toFloat())
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        canvas.restore()

    }


    private fun getBitMap(): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.drawable.launcher, options)
        options.inJustDecodeBounds = false
        options.inDensity = min(options.outWidth, options.outHeight)
        options.inTargetDensity = 200.dp.toInt()
        return BitmapFactory.decodeResource(resources, R.drawable.launcher, options)
    }
}