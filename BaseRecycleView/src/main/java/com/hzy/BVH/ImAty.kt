package com.hzy.BVH

import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzy.BVH.adapter.BaseAdapter
import com.hzy.BVH.adapter.BaseVH
import kotlinx.android.synthetic.main.activity_im.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * User: hzy
 * Date: 2020/12/25
 * Time: 10:55 AM
 * Description:
 */
class ImAty : AppCompatActivity() {
    private val layoutManger = LinearLayoutManager(this)
    private val adapter = ImAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_im)
        rv.adapter = adapter
        rv.layoutManager = layoutManger
        layoutManger.stackFromEnd = false
        adapter.addData(listOf(
                DataBean("1"),
                DataBean("2"),
                DataBean("3"),
                DataBean("4"),
                DataBean("5")))
        change()

        send.setOnClickListener {
            adapter.addData(listOf(DataBean(edit.text.toString())))
            edit.setText("")
            change()
        }
        val child = FrameLayout(this)
        child.setBackgroundColor(ActivityCompat.getColor(this, R.color.gray))
        windowManager.addView(child, getLayoutParams())
        child.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                val padding = (oldBottom - bottom).coerceAtLeast(0)
                window.decorView.setPadding(0, 0, 0, padding)
                if (rv.scrollState == RecyclerView.SCROLL_STATE_IDLE) rv.scrollToPosition(adapter.getData().size - 1)
            } else window.decorView.setPadding(0, 0, 0, 0)
        }
    }

    fun change() {
        if (rv.scrollState == RecyclerView.SCROLL_STATE_IDLE) rv.scrollToPosition(adapter.getData().size - 1)

    }


    private fun getLayoutParams(): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION
        layoutParams.softInputMode = SOFT_INPUT_ADJUST_RESIZE
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        layoutParams.format = PixelFormat.TRANSLUCENT
        return layoutParams
    }

}


class ImAdapter : BaseAdapter<DataBean>() {
    override fun bindData(holder: BaseVH, position: Int, data: DataBean, itemtype: Int) {
        when (itemtype) {
            0 -> {
                holder.setText(R.id.content, data.title)
            }
//            1 -> {
//                holder.setText(R.id.textView, data.title)
//                holder.setOnClick(R.id.textView) {
//                    Toast.makeText(holder.itemView.context, data.title, Toast.LENGTH_LONG).show()
//                }
//            }
        }

    }

    override fun setLayout() {
        addLayout(Pair(0, R.layout.item_im_me))
    }

    override fun getItemType(data: DataBean): Int {
        return data.type
    }
}

