package com.hzy.BVH

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hzy.BVH.helper.dp
import kotlin.math.roundToInt

/**
 * User: hzy
 * Date: 2020/7/4
 * Time: 3:52 PM
 * Description:粘性头部布局
 * @T 粘性数据类
 * @param layoutId 头部布局ID
 * 数据类需要扩展StickHeadBean接口
 */
abstract class StickHeadTransfer {
    abstract fun isFirst(position: Int): Boolean
    abstract fun isLast(position: Int): Boolean
    abstract fun bindData(stickView: View, position: Int)
}


class StickHeaderItemDecoration(private val layoutId: Int, private val stickViewHeight: Int = 0) : RecyclerView.ItemDecoration() {
    private var transfer: StickHeadTransfer? = null
    private lateinit var stickHeadView: View

    fun <T : StickHeadTransfer> setTransfer(transfer: T) {
        this.transfer = transfer
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        //获取的View的位置
        initStickView(parent)
        if (transfer?.isFirst(parent.getChildAdapterPosition(view)) == true) {
            //头部添加宽度
            outRect.top = stickViewHeight.dp
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        initStickView(parent)
        val left: Int = parent.paddingLeft
        val right: Int = parent.width - parent.paddingRight
        for (position in 0 until parent.childCount) {
            val child = parent.getChildAt(position)
            val adapterPosition = parent.getChildAdapterPosition(child)
            if (transfer?.isFirst(adapterPosition) == true) {
                //计算ItemView的实际top
                val bottom = getViewRealTop(child)
                val top = bottom - stickViewHeight.dp
                val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
                //先绑定数据再测量
                transfer?.bindData(view, adapterPosition)
                view.measure(child.measuredWidth, child.measuredHeight)
                view.layout(left, top, right, bottom)
                c.translate(0.toFloat(), top.toFloat())
                view.draw(c)
                c.translate(0.toFloat(), -top.toFloat())
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (parent.childCount <= 0) return
        initStickView(parent)
        /***
         * 1、不是第一个需要添加一个悬浮的View。
         * 2、是最后一个需要添加一个随着其的Top逐渐变化的View
         */
        val left: Int = parent.paddingLeft
        val right: Int = parent.width - parent.paddingRight
        val child = parent.getChildAt(0)
        val adapterPosition = parent.getChildAdapterPosition(child)
        val bottom = getViewRealTop(child)
        val top = bottom - stickViewHeight.dp
        //先绑定数据再测量
        transfer?.bindData(stickHeadView, adapterPosition)
        stickHeadView.measure(child.measuredWidth, child.measuredHeight)
        stickHeadView.layout(left, top, right, bottom)
        if (!beginTransfer(c, parent)) stickHeadView.draw(c)

    }


    private fun beginTransfer(c: Canvas, parent: RecyclerView): Boolean {
        //判断分组的最后一个距离头部的距离判断是否开始移动
        for (position in 0 until parent.childCount) {
            val view = parent.getChildAt(position)
            if (transfer?.isLast(parent.getChildAdapterPosition(view)) == true) {
                val top = getViewRealTop(view)
                val distance = top + view.height
                if (distance > stickViewHeight.dp) return false
                c.translate(0.toFloat(), (top - view.height).toFloat())
                stickHeadView.draw(c)
                c.translate(0.toFloat(), -(top - view.height).toFloat())
                return true
            }
        }
        return false
    }

    private fun getViewRealTop(child: View): Int {
        val params = child
                .layoutParams as RecyclerView.LayoutParams
        return child.top + params.topMargin +
                child.translationY.roundToInt()
    }

    private fun initStickView(parent: RecyclerView) {
        if (this::stickHeadView.isInitialized) return
        else stickHeadView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    }
}

