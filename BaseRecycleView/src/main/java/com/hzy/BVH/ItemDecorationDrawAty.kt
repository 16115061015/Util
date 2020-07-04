package com.hzy.BVH

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzy.BVH.helper.dp
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt


/**
 * User: hzy
 * Date: 2020/6/30
 * Time: 2:52 PM
 * Description: 自定义ItemDecoration分割线绘制  粘性头部
 */
class ItemDecorationDrawAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MainAdapter()
        rv.adapter = adapter
        rv.addItemDecoration(SpanItemDecoration(80))

        adapter.addData(listOf(
                DataBean("123", 0),
                DataBean("456", 1),
                DataBean("789", 1),
                DataBean("dasd", 1),
                DataBean("fadasd", 0),
                DataBean("sda", 0),
                DataBean("23", 0),
                DataBean("23", 0),
                DataBean("23", 0),
                DataBean("23", 0),
                DataBean("23", 0),
                DataBean("23", 0),
                DataBean("23", 0),
                DataBean("23", 0),
                DataBean("23", 0),
                DataBean("sd1a", 0),
                DataBean("sd2a", 0),
                DataBean("sd3a", 0)))
    }

    class SpanItemDecoration(private val dividerHeight: Int = 10) : RecyclerView.ItemDecoration() {
        private val paint by lazy {
            Paint().apply {
                isAntiAlias = true
            }
        }

        //实现边距
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (parent.layoutManager !is LinearLayoutManager) return
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) outRect.top = dividerHeight.dp
            val needDivider = position != parent.adapter?.itemCount ?: 0 - 1
            outRect.bottom = if (needDivider) dividerHeight.dp else 0
        }

        /***
         * 绘制在ItemView底下
         */
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//            val left: Int = parent.paddingLeft
//            val right: Int = parent.width - parent.paddingRight
//            val childCount: Int = parent.childCount
//            paint.color = parent.context.resources.getColor(R.color.colorAccent)
//            for (i in 0 until childCount) {
//                //计算分割线起始位置
//                val child: View = parent.getChildAt(i)
//                val params = child
//                        .layoutParams as RecyclerView.LayoutParams
//                // divider的top 应该是 item的bottom 加上 marginBottom 再加上 Y方向上的位移
//                val top = child.bottom + params.bottomMargin +
//                        child.translationY.roundToInt()
//                // divider的bottom就是top加上divider的高度了
//                val bottom = (top + dividerHeight.dp)
//                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
//            }
            super.onDraw(c, parent, state)

        }

        /***
         * 绘制在ItemView之上
         */
        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDrawOver(c, parent, state)
            //获得布局中第一个View的位置
            val child: View = parent.getChildAt(0)
            if (parent.getChildAdapterPosition(child) != 0) {
                //获取是否是adapter第一个View
                // 如果第一个item已经被回收,没有显示在recycler view上,则不需要draw header
                return
            }
            Log.i("/forTest", "onDrawOver")
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            // divider的top 应该是 item的bottom 加上 marginBottom 再加上 Y方向上的位移
            val top = child.top + params.topMargin -
                    child.translationY.roundToInt()
            //移动画布达到绘制的效果随布局移动的效果
            c.translate(0.toFloat(), top - dividerHeight.dp.toFloat())
            val view = TextView(parent.context).apply {
                text = "1231412412412"
                height = dividerHeight.dp
            }
            //每次滚动第一个位置改变时移动画布
            view.layout(0, 0, 200.dp, dividerHeight.dp)
            view.draw(c)
            c.translate(0.toFloat(), -(top - dividerHeight.dp.toFloat()))
            view.setOnClickListener {
                Toast.makeText(parent.context, "dadasd", Toast.LENGTH_SHORT).show()
            }
        }

    }

}





