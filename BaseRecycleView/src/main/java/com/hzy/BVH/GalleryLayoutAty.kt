package com.hzy.BVH

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hzy.BVH.adapter.BaseAdapter
import com.hzy.BVH.adapter.BaseVH
import com.hzy.BVH.layoutManager.ScaleLayoutManager
import kotlinx.android.synthetic.main.activity_gallery.*

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:27 PM
 * Description:
 */
class GalleryLayoutAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        val adapter = GalleryAdapter()
        rv2.apply {
            setAlphaItem(true) //设置半透渐变
            setOnItemSelectedListener {

            }
        }
        rv.layoutManager = ScaleLayoutManager()
        val data = mutableListOf<Int>()
        for (i in 0..10) {
            data.add(i)
        }
        adapter.addData(data)
        rv.adapter = adapter
        rv2.adapter = adapter
    }

}

class GalleryAdapter : BaseAdapter<Int>() {
    override fun bindData(holder: BaseVH, position: Int, data: Int, itemtype: Int) {

    }

    override fun setLayout() {
        addSingleLayout(R.layout.item_grallery)
    }


}

