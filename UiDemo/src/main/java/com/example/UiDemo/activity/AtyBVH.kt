package com.example.UiDemo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.example.UiDemo.DemoTest.BVH.adapter.LoadDataAdapter
import com.example.UiDemo.DemoTest.BVH.contract.DataEnrty
import com.example.UiDemo.DemoTest.BVH.view.LoadMoreDataView
import com.example.UiDemo.R
import com.example.UiDemo.config.UiRouterConfig
import kotlinx.android.synthetic.main.aty_bvh.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay


/**
 * User: hzy
 * Date: 2020/3/29
 * Time: 11:57 PM
 * Description: BVH 框架使用测试
 */
@Route(path = UiRouterConfig.BVH)
open class AtyBVH : AppCompatActivity() {
    private lateinit var adapter: LoadDataAdapter
    private var dataIndex: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_bvh)
        initView()
        bindEvent()
        loadData()

    }

    private fun initView() {
        adapter = LoadDataAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.setLoadMoreView(getLoadMoreView())
        adapter.setEnableLoadMore(true)
        adapter.openLoadAnimation()
    }

    /***
     * 加载数据
     */
    protected open fun loadData() {
        GlobalScope.async {
            //测试冲突
            var data = ArrayList<DataEnrty>()
            data.add(DataEnrty(1, "${dataIndex++}"))
            data.add(DataEnrty(2, "${dataIndex++}"))
            data.add(DataEnrty(1, "${dataIndex++}"))
            delay(1000)
            runOnUiThread {
                adapter.addData(data)
                adapter.loadMoreComplete()
            }
        }
    }

    protected open fun getLoadMoreView(): LoadMoreView {
        return LoadMoreDataView()
    }


    protected open fun bindEvent() {
        adapter.setOnLoadMoreListener({
            loadData()
        }, recyclerView)
    }

}