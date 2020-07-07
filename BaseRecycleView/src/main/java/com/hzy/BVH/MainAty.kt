package com.hzy.BVH

import android.os.Bundle
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.hzy.BVH.adapter.BaseAdapter
import com.hzy.BVH.adapter.BaseVH
import kotlinx.android.synthetic.main.activity_main.*

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:27 PM
 * Description:
 */
class MainAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MainAdapter()
        rv.adapter = adapter
        adapter.addData(listOf(
                DataBean("123", 0),
                DataBean("456", 1),
                DataBean("789", 1),
                DataBean("dasd", 1),
                DataBean("fadasd", 0)))
        rv.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                //点击时缩小
                val childView = rv.findChildViewUnder(e.x, e.y)
                if (e.action == ACTION_DOWN) {
                    childView?.scaleY = 0.8F
                } else {
                    childView?.scaleY = 1F
                }
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })
    }

}

class MainAdapter : BaseAdapter<DataBean>() {
    override fun bindData(holder: BaseVH, position: Int, data: DataBean, itemtype: Int) {
        when (itemtype) {
            0 -> {
                holder.setText(R.id.tv, data.title)
                holder.setOnClick(R.id.tv) {
                    Toast.makeText(holder.itemView.context, data.title, Toast.LENGTH_LONG).show()
                }
                holder.itemView.findViewById<TextView>(R.id.tv).setOnFocusChangeListener { _, hasFocus ->
                    run {
                        if (hasFocus) holder.itemView.findViewById<TextView>(R.id.tv).scaleX = 1.3F
                        else holder.itemView.findViewById<TextView>(R.id.tv).scaleX = 1F
                    }
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


class DataBean(
        val title: String = "",
        val type: Int = 0
)
