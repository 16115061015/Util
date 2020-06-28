package com.hzy.rollscreenview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
        rl.adapter = adapter
        val data = mutableListOf<DataBean>()
        for(index in 0..21){
            data.add(DataBean(0, "index:$index"))
        }
        adapter.setNewData(data)

    }
}

class MainAdapter : RollScreenLayout.Adapter<DataBean>() {
    override fun onCreateView(vp: ViewGroup, data: DataBean): View? {
        return when (data.type) {
            0 -> return View.inflate(vp.context, R.layout.rollscreen_item1, null)
            1 -> return View.inflate(vp.context, R.layout.rollscreen_item2, null)
            else -> TextView(vp.context)
        }
    }


    override fun getItemType(data: DataBean): Int {
        return data.type
    }

    override fun convert(data: DataBean, holder: RollScreenLayout.ViewHolder) {
        when (data.type) {
            0 -> holder.setText(R.id.tv, data.content)
            1 -> holder.setText(R.id.textView, data.content)
        }
    }

}

class DataBean(val type: Int, val content: String)

