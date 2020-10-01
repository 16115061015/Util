package com.hzy.BVH.layoutManager

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * User: hzy
 * Date: 2020/9/15
 * Time: 9:40 PM
 * Description: 比例缩放RecycleView LayoutManager 横向滚动
 */
class ScaleLayoutManager : RecyclerView.LayoutManager() {

    //总偏移量
    private var offsetX = 0


    private var itemHeight = 0
    private var itemWidth = 0


    /***
     * 缩放因子 控制item之间的距离
     */
    private var scaleFactor = 0.5f


    /**
     * 预先加载item布局数量
     */
    private var preLoadItemCount = 20

    /***
     * 所有View的布局位置
     */
    private var childFrames: LinkedHashMap<Int, Rect> = LinkedHashMap()

    private var recyclePos: HashSet<Int> = HashSet()

    /**
     * 滑动的方向：左
     */
    private val SCROLL_LEFT = 1

    /**
     * 滑动的方向：右
     */
    private val SCROLL_RIGHT = 2


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }
        if (itemCount == 0 || state.isPreLayout) {
            offsetX = 0
            return
        }
        recyclePos.clear()
        childFrames.clear()
        //  获取统一的itemView  作为所有View的默认计算宽高
        measureUniteView(recycler)
        calculateItemLayoutFrame()
        detachAndScrapAttachedViews(recycler)
        //布局
        fill(recycler, state)
    }


    /***
     * 计算统一的View的尺寸
     */
    private fun measureUniteView(recycler: RecyclerView.Recycler) {
        val child = recycler.getViewForPosition(0)
        addView(child)
        measureChildWithMargins(child, 0, 0)
        itemHeight = getDecoratedMeasuredWidth(child)
        itemWidth = getDecoratedMeasuredHeight(child)
    }

    /***
     * @param dy  dx滚动距离
     * @return 消耗值
     */
    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State, dir: Int = SCROLL_RIGHT) {
        //屏幕显示区域
        val displayFrame = Rect(offsetX, paddingTop, offsetX + getHorizontalSpace(), paddingTop + getVerticalSpace())
        layoutItem(recycler, state, displayFrame, dir)
    }

    /***
     * 计算各个item布局
     */
    private fun calculateItemLayoutFrame() {
        var left = 0f
        for (i in 0 until itemCount) {
            if (i > preLoadItemCount) break
            if (!childFrames.containsKey(i)) {
                childFrames[i] = getFrame(i)
            }
            left += getItemInterval()
        }
    }


    /***
     * 摆放子View
     */
    private fun layoutItem(recycler: RecyclerView.Recycler, state: RecyclerView.State?, disPlayFrame: Rect, dir: Int = SCROLL_RIGHT) {
        if (state == null || state.isPreLayout) return
        var min = itemCount - 1
        var max = 0
        Log.i("forTewtsss", "现在有的子View:$childCount")
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val pos = getPosition(child)
            val rect = getFrame(pos)
            if (!Rect.intersects(disPlayFrame, rect)) { //Item没有在显示区域，就说明需要回收
                removeAndRecycleView(child, recycler) //回收滑出屏幕的View
                recyclePos.remove(pos)
                Log.i("forTest", "清缓存$pos")
            } else { //Item还在显示区域内，更新滑动后Item的位置
                layoutDecoratedWithMargins(child, -offsetX + rect.left, rect.top, -offsetX + rect.right, rect.bottom) //更新Item位置
                recyclePos.add(pos)
                Log.i("forTest", "Item还在显示区域内 进缓存$pos")
                min = min(min, pos)
                max = max(max, pos)
            }
        }

        Log.i("forTewtsss", "检查添加布局 $min ->0  $max ->$itemCount")
        for (index in min..0) {
            Log.i("forTest","看下需不需要layout:$index")
            if (!layoutItemView(recycler, disPlayFrame, index, dir)) break
        }
        for (index in max until itemCount) {
            Log.i("forTest","看下需不需要layout:$index")
            if (!layoutItemView(recycler, disPlayFrame, index, dir)) break
        }

    }


    private fun layoutItemView(recycler: RecyclerView.Recycler, disPlayFrame: Rect, index: Int, dir: Int): Boolean {
        val child = recycler.getViewForPosition(index)
        val bound = getFrame(index)
        Log.i("forTest", "$index 在屏幕里吗？${Rect.intersects(disPlayFrame, bound)}  是滑动进来的吗，需要addView吗${!recyclePos.contains(index)}")
        if (Rect.intersects(disPlayFrame, bound) && !recyclePos.contains(index)) {
            measureChildWithMargins(child, 0, 0)
            if (dir == SCROLL_RIGHT) {
                //右滑动添加在后面
                addView(child)
            } else {
                //左滑动添加在前面
                addView(child, 0)
            }
            Log.i("forTest", "进缓存$index")
            recyclePos.add(index)
            layoutDecoratedWithMargins(child, -offsetX + bound.left, bound.top, -offsetX + bound.right, bound.bottom) //更新Item位置
        } else {
            Log.i("forTest", "不走了$index")
            return false
        }
        return true
    }

    /***
     * 各个item之间的间距
     */
    private fun getItemInterval() = itemWidth * scaleFactor


    /***
     * 获取position位置item的起始位置
     */
    private fun getItemStartX(position: Int) = itemWidth * scaleFactor * position

    private fun transformChild(left: Int, child: View) {
//        child.scaleX = computeScale(left)
//        child.scaleY = computeScale(left)
    }


    private fun computeScale(x: Int): Float {
        val screenWidth = width - paddingLeft - paddingRight
        Log.i("/forTest", "屏幕$screenWidth")
        Log.i("/forTest", "比率${abs(x - offsetX - screenWidth / 2) / (screenWidth / 2.toFloat())}")
        var scale: Float = 1 - abs(x + offsetX - screenWidth / 2) / (offsetX + screenWidth / 2.toFloat())
        if (scale < 0) scale = 0f
        if (scale > 1) scale = 1f
        return scale
    }


    /**
     * 获取中间位置
     *
     * Note:该方法主要用于 判断中间位置
     *
     * 如果需要获取被选中的Item位置，调用[.getSelectedPos]
     */
    fun getCenterPosition(): Int {
        var pos = (offsetX / getItemInterval()).toInt()
        val more = (offsetX % getItemInterval()).toInt()
        if (more > getItemInterval() * 0.5f) pos++
        Log.i("/forTest", "中心位置：$pos")
        return pos
//        val centerOffsetX = getHorizontalSpace() / 2
//        Log.i("forTest", "中心位置 坐标：$centerOffsetX")
//        childFrames.forEach {
//            if (it.value.left + offsetX <= centerOffsetX && it.value.right + offsetX >= centerOffsetX) {
//                Log.i("forTest", "中心位置：${it.key}")
//                return@getCenterPosition it.key
//            }
//        }
//        return -1
    }


    /**
     * 水平方向空间
     */
    private fun getHorizontalSpace() = width - paddingLeft - paddingRight

    /**
     * 竖直方向空间
     */
    private fun getVerticalSpace() = height - paddingTop - paddingBottom


    /**
     * 获取第一个可见的Item位置
     *
     * Note:该Item为绘制在可见区域的第一个Item，有可能被第二个Item遮挡
     */
    fun getFirstVisiblePosition(): Int {
        val displayFrame = Rect(offsetX, 0, offsetX + width - paddingLeft - paddingRight, height - paddingTop - paddingBottom)
        val cur = getCenterPosition()
        for (i in cur - 1 downTo 0) {
            val rect: Rect = getFrame(i)
            if (!Rect.intersects(displayFrame, rect)) {
                return i + 1
            }
        }
        return 0
    }


    private fun getFrame(index: Int): Rect {
        var frame: Rect? = childFrames[index]
        if (frame == null) {
            val offset: Float = getItemInterval() * index //原始位置累加（即累计间隔距离）
            frame = Rect(paddingLeft + offset.roundToInt(), paddingTop, paddingLeft + (offset + itemWidth).roundToInt(), getVerticalSpace())
        }
        return frame
    }

    /**
     * 计算Item所在的位置偏移
     *
     * @param position 要计算Item位置
     */
    private fun calculateOffsetForPosition(position: Int): Int {
        return (getItemInterval() * position).roundToInt()
    }


    /***
     * 预先测量
     */
    override fun isAutoMeasureEnabled(): Boolean = false

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return 0
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        Log.i("forTest", "消耗滑动$dx")
        var consumerX = dx
        if (dx + offsetX < 0) {
            consumerX = offsetX
        } else if (dx + offsetX > getMaxOffset()) {
            consumerX = ((offsetX - getMaxOffset()).toInt())
        }
        offsetX += consumerX //累计偏移量
        Log.i("forTest", "消耗滑动$consumerX")
        fill(recycler, state, if (consumerX < 0) SCROLL_LEFT else SCROLL_RIGHT)
        return consumerX
    }

    private fun getMaxOffset() = (itemCount - 1) * getItemInterval()

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        removeAllViews()
        childFrames.clear()
    }

    override fun canScrollVertically(): Boolean = false

    override fun canScrollHorizontally(): Boolean = true

    override fun supportsPredictiveItemAnimations(): Boolean {
        return true
    }
}