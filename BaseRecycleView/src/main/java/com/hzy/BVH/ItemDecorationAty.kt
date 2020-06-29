package com.hzy.BVH

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hzy.BVH.adapter.BaseAdapter
import com.hzy.BVH.adapter.BaseVH
import kotlinx.android.synthetic.main.activity_main.*

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:27 PM
 * Description: ItemDecoration 使用解析
 */
class ItemDecorationAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MainAdapter()
        rv.adapter = adapter
        rv.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
        rv.addItemDecoration(SpanItemDecoration(10, 10, 10, 10))

        adapter.addData(listOf(
                DataBean("123", 0),
                DataBean("456", 1),
                DataBean("789", 1),
                DataBean("dasd", 1),
                DataBean("fadasd", 0),
                DataBean("sda", 0),
                DataBean("23", 0),
                DataBean("sd1a", 0),
                DataBean("sd2a", 0),
                DataBean("sd3a", 0)))
    }

    class SpanItemDecoration(val left: Int = 0, val right: Int = 0, val top: Int = 0, val bottom: Int = 0) : RecyclerView.ItemDecoration() {
        //实现边距
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            when (parent.layoutManager) {
                is GridLayoutManager -> getGridLayoutItemOffsets(outRect, view, parent, state, (parent.layoutManager as GridLayoutManager).spanCount)
                is StaggeredGridLayoutManager -> getStaggeredGridHorizontalItemOffsets(outRect, view, parent, state, (parent.layoutManager as StaggeredGridLayoutManager).spanCount, (parent.layoutManager as StaggeredGridLayoutManager).orientation)
                is LinearLayoutManager -> getLinearLayoutItemOffsets(outRect, view, parent, state, (parent.layoutManager as LinearLayoutManager).orientation)
            }
        }

        /***
         * 针对LinearLayoutManager的边距
         */
        private fun getLinearLayoutItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State, orientation: Int) {
            val itemCount = parent.adapter?.itemCount ?: 0
            val currentCount = parent.getChildAdapterPosition(view)
            when (orientation) {
                LinearLayoutManager.HORIZONTAL -> outRect.set(left.dp, top.dp, if (itemCount - 1 == currentCount) right.dp else 0, bottom.dp)
                LinearLayoutManager.VERTICAL -> outRect.set(left.dp, top.dp, right.dp, if (itemCount - 1 == currentCount) bottom.dp else 0)
            }
        }

        /***
         * 针对GridLayoutManager和StaggeredGridLayoutManager的VERTICAL模式的设置边距离
         */
        private fun getGridLayoutItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State, spanCount: Int) {
            val itemCount = parent.adapter?.itemCount ?: 0
            val currentCount = parent.getChildAdapterPosition(view)
            //每一行的最后一个View才需要添加right
            val lastOfLine = currentCount % spanCount == spanCount - 1 || currentCount == itemCount - 1
            outRect.set(left.dp, top.dp, if (lastOfLine) right.dp else 0, if (lastOfLine) bottom.dp else 0)
        }

        /***
         * 针对StaggeredGridLayoutManager的设置边距离
         */
        private fun getStaggeredGridHorizontalItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State, spanCount: Int, orientation: Int) {
            when (orientation) {
                RecyclerView.HORIZONTAL -> {
                    /**
                     * 由于横向的StaggeredGridLayout排列是
                     * 0 2 4
                     * 1 3 5
                     * 其中可以通过spanIndex获取是第几行 则每一行的最后一个可以通过等差数列得出第n个的index
                     */
                    val itemCount = parent.adapter?.itemCount ?: 0
                    val currentCount = parent.getChildAdapterPosition(view)
                    val horizontalSpanCount = (itemCount + 1) / spanCount //最后一项的n值
                    val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
                    val spanIndex = params.spanIndex // 首项值
                    val lastOfLine = currentCount == params.spanIndex + 2 * (horizontalSpanCount - 1) || spanIndex == itemCount - 1
                    outRect.set(left.dp, top.dp, if (lastOfLine) right.dp else 0, if (lastOfLine) bottom.dp else 0)
                }
                RecyclerView.VERTICAL -> getGridLayoutItemOffsets(outRect, view, parent, state, spanCount)
            }

        }


    }

}


val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
    ).toInt()


