package com.hzy.BVH

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hzy.BVH.adapter.BaseAdapter
import com.hzy.BVH.adapter.BaseVH
import kotlinx.android.synthetic.main.activity_main.*

/**
 * User: hzy
 * Date: 2020/7/4
 * Time: 4:55 PM
 * Description:粘性头部布局
 */
class StickHeaderAty : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = StickAdapter()
        rv.adapter = adapter
        rv.addItemDecoration(StickHeaderItemDecoration(R.layout.stick_view, 80).apply {
            setTransfer(object : StickHeadTransfer() {
                override fun isFirst(position: Int): Boolean {
                    return adapter.getData()[position].isFirst
                }

                override fun isLast(position: Int): Boolean {
                    return adapter.getData()[position].isLast
                }

                override fun bindData(stickView: View, position: Int) {
                    val data = adapter.getData()[position]
                    stickView.findViewById<TextView>(R.id.tvTitle).text = data.description
                }

            })
        })
        adapter.addData(listOf(
                StickDataBean("1", 0, "第一个分组").apply { isFirst = true },
                StickDataBean("2", 0, "第一个分组"),
                StickDataBean("3", 0, "第一个分组"),
                StickDataBean("4", 0, "第一个分组"),
                StickDataBean("5", 0, "第一个分组"),
                StickDataBean("6", 0, "第一个分组"),
                StickDataBean("7", 0, "第一个分组"),
                StickDataBean("8", 0, "第一个分组"),
                StickDataBean("9", 0, "第一个分组"),
                StickDataBean("fadasd", 0, "第一个分组").apply { isLast = true },
                StickDataBean("456", 1, "第二个分组").apply { isFirst = true },
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("789", 1, "第二个分组"),
                StickDataBean("dasd", 1).apply { isLast = true }))

    }


}

data class StickDataBean(val title: String, val type: Int = 0, var description: String = "") {
    var isFirst = false
    var isLast = false
}

class StickAdapter : BaseAdapter<StickDataBean>() {
    override fun bindData(holder: BaseVH, position: Int, data: StickDataBean, itemtype: Int) {
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

    override fun getItemType(data: StickDataBean): Int {
        return data.type
    }
}


