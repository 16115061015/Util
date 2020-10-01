package com.hzy.BVH.recycleview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hzy.BVH.layoutManager.GalleryLayoutManger
import com.hzy.BVH.layoutManager.ScaleLayoutManager

/**
 * 继承RecyclerView重写[.getChildDrawingOrder]对Item的绘制顺序进行控制
 */
class ScaleRecycleView : RecyclerView {
    /**
     * 按下的X轴坐标
     */
    private var mDownX = 0f

    /**
     * 布局器构建者
     */
    private var mManagerBuilder: GalleryLayoutManger.Builder? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        init()
    }

    private fun init() {
        isChildrenDrawingOrderEnabled = false //开启重新排序
        overScrollMode = View.OVER_SCROLL_NEVER
    }


    override fun setLayoutManager(layout: LayoutManager?) {
        require(layout is ScaleLayoutManager) { "The layout manager must be CoverFlowLayoutManger" }
        super.setLayoutManager(layout)
    }

//    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
//        var center = (coverFlowLayout!!.getCenterPosition()
//                - coverFlowLayout!!.getFirstVisiblePosition()) //计算正在显示的所有Item的中间位置
//        if (center < 0) center = 0 else if (center > childCount) center = childCount
//        return when {
//            i == center -> childCount - 1
//            i > center -> center + childCount - 1 - i
//            else -> i
//        }
//
//    }

    /**
     * 获取LayoutManger，并强制转换为CoverFlowLayoutManger
     */
    val coverFlowLayout: ScaleLayoutManager?
        get() = layoutManager as ScaleLayoutManager?

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.x
                parent.requestDisallowInterceptTouchEvent(true) //设置父类不拦截滑动事件
            }
            MotionEvent.ACTION_MOVE -> if (ev.x > mDownX && coverFlowLayout!!.getCenterPosition() == 0 ||
                    ev.x < mDownX && coverFlowLayout!!.getCenterPosition() ==
                    coverFlowLayout!!.itemCount - 1) {
                //如果是滑动到了最前和最后，开放父类滑动事件拦截
                parent.requestDisallowInterceptTouchEvent(false)
            } else {
                //滑动到中间，设置父类不拦截滑动事件
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}