package com.example.UiDemo.DemoTest.BVH.view

import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.example.UiDemo.R

/**
 * User: hzy
 * Date: 2020/3/30
 * Time: 11:46 PM
 * Description:
 */
class LoadMoreDataView : LoadMoreView() {
    override fun getLayoutId(): Int {
        return R.layout.aty_bvh_data_more
    }

    override fun getLoadingViewId(): Int {

        return R.id.loading
    }

    override fun getLoadEndViewId(): Int {
        return R.id.load_end
    }

    override fun getLoadFailViewId(): Int {
        return R.id.load_fail
    }


}