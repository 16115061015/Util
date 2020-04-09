package com.hzy.cnn.CustomView.Ui.view

import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.hzy.cnn.CustomView.R

/**
 * User: hzy
 * Date: 2020/3/30
 * Time: 11:46 PM
 * Description:
 */
class LoadMoreDataView :LoadMoreView(){
    override fun getLayoutId(): Int {
        return R.layout.load_data_more
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