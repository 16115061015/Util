package com.hzy.cnn.CustomView.main

import android.content.Context
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hzy.aopdemo.Track.ClickTrack
import com.hzy.cnn.CustomView.R
import com.hzy.cnn.CustomView.contract.Components

/**
 * User: hzy
 * Date: 2020/5/7
 * Time: 1:09 PM
 * Description:
 */
class MainAdapter(i: Int = R.layout.main_item) : BaseQuickAdapter<Components, BaseViewHolder>(i) {

    override fun convert(helper: BaseViewHolder?, item: Components?) {
        helper?.setText(R.id.tv, item?.description)
        helper?.getView<ConstraintLayout>(R.id.root_view)?.setOnClickListener {
            item?.route?.let { route -> click(helper.itemView.context, route) }
        }
    }


    @ClickTrack
    private fun click(context: Context, route: String) {
        ARouter.getInstance().build(route).navigation()
    }
}