package com.hzy.BVH.recycleview

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * User: hzy
 * Date: 2020/8/25
 * Time: 1:48 PM
 * Description: 自动滚动RecyclerView
 */
class AutoPollRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {
    var direction: Direction = Direction.Up
    private val autoPollTask = AutoPollTask(this)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.i("/forTest","onAttachedToWindow")
        startAutoPoll()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.i("/forTest","onDetachedFromWindow")
        stopAutoPoll()
    }


    fun stopAutoPoll(){
        handler?.removeCallbacks(autoPollTask)
    }

    fun startAutoPoll() {
        adapter?.let { scrollToPosition(it.itemCount / 2) }
        stopAutoPoll()
        post(autoPollTask)
    }


    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return true
    }


    fun autoScroll() {
        val y = if (direction == Direction.Up) 2 else -2
        scrollBy(0, y)
    }

    class AutoPollTask(reference: AutoPollRecyclerView) : Runnable {
        private val weakReference by lazy {
            WeakReference<AutoPollRecyclerView>(reference)
        }

        override fun run() {
            val recyclerView = weakReference.get()
            if (recyclerView != null) {
                recyclerView.autoScroll()
                recyclerView.postDelayed(recyclerView.autoPollTask, 20)
            }
        }

    }

    enum class Direction {
        Up,
        Down
    }
}



