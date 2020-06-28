package com.hzy.BVH

import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hzy.BVH.adapter.BaseAdapter
import com.hzy.BVH.adapter.BaseVH
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:27 PM
 * Description: 使用ItemTouchHelperDemo
 */
val helper by lazy {
    ItemTouchHelper(TouchCallBack())
}
val adapter = TouchAdapter()

class ItemTouchAty : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv.adapter = adapter
        helper.attachToRecyclerView(rv)
        adapter.addData(listOf(
                DataBean("123", 0),
                DataBean("456", 1),
                DataBean("789", 1),
                DataBean("dasd", 1),
                DataBean("fadasd", 0)))
    }

}


class TouchCallBack : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    /***
     *   上下滑动后触发
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        Collections.swap(adapter.getData(), viewHolder.adapterPosition, target.adapterPosition)
        adapter.notifyItemRangeChanged(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    /***
     * 左右滑动后触发
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.getData().removeAt(viewHolder.adapterPosition)
        adapter.notifyItemRemoved(viewHolder.adapterPosition)
    }

    /***
     * 长按启动拖动 ，如果使用helper.startDrag()用别的方式触发拖动这里要返回false
     */
    override fun isLongPressDragEnabled(): Boolean = true


    /***
     * 在被拖动时或左右滑动开始时会触发 --可以做例如拖动或滑动时修改背景色
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
    }

    /***
     * 在被拖动左右滑动结束时会触发 --可以在滑动结束之后复位
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
    }

    /***
     * 被拖拽或滑动时会触发-->可以拿到位移 可以用于做一些item切换时的动画效果，例如卡片滑动
     */
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /***
     * 在绘制之后触发，可以做到item覆盖
     */
    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}


class TouchAdapter : BaseAdapter<DataBean>() {
    override fun bindData(holder: BaseVH, position: Int, data: DataBean, itemtype: Int) {
        when (itemtype) {
            0 -> {
                holder.setText(R.id.tv, data.title)
                holder.setOnClick(R.id.tv) {
                    Toast.makeText(holder.itemView.context, data.title, Toast.LENGTH_LONG).show()
                }
            }
            1 -> {
                holder.setText(R.id.textView, data.title)
                holder.setOnClick(R.id.textView) {
                    Toast.makeText(holder.itemView.context, data.title, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    override fun setLayout() {
        addLayout(
                Pair(0, R.layout.recycleview_item1),
                Pair(1, R.layout.recycleview_item2)
        )
    }

    override fun getItemType(data: DataBean): Int {
        return data.type
    }
}


