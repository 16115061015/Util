package com.hzy.BVH

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.hzy.BVH.adapter.AutoPollAdapter
import com.hzy.BVH.recycleview.AutoPollRecyclerView
import kotlinx.android.synthetic.main.aty_autopoll.*

/**
 * User: hzy
 * Date: 2020/8/25
 * Time: 2:25 PM
 * Description:
 */
class AutoPollAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.aty_autopoll)

        val adapter = AutoPollAdapter()
        autoPoll.adapter = adapter
        val data = mutableListOf<Int>()
        data.add(R.drawable.poll1)
        data.add(R.drawable.poll2)
        data.add(R.drawable.poll3)
        data.add(R.drawable.poll4)
        adapter.setData(data)
        autoPoll.direction = AutoPollRecyclerView.Direction.Up
        autoPoll.startAutoPoll()
        val adapter2 = AutoPollAdapter()
        autoPoll2.adapter = adapter2
        autoPoll2.direction = AutoPollRecyclerView.Direction.Down
        val data2 = mutableListOf<Int>()
        data2.add(R.drawable.poll5)
        data2.add(R.drawable.poll6)
        data2.add(R.drawable.poll7)
        data2.add(R.drawable.poll8)
        adapter2.setData(data)
        autoPoll2.startAutoPoll()

    }

}