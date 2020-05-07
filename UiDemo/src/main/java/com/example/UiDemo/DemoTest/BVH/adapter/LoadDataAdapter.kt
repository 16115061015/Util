package com.example.UiDemo.DemoTest.BVH.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.UiDemo.DemoTest.BVH.contract.DataEnrty
import com.example.UiDemo.R

/**
 * User: hzy
 * Date: 2020/3/30
 * Time: 12:17 PM
 * Description:
 */
class LoadDataAdapter(i: Int = R.layout.aty_bvh_data_item) : BaseQuickAdapter<DataEnrty, BaseViewHolder>(i) {
    override fun convert(helper: BaseViewHolder?, item: DataEnrty?) {
        when (item?.type) {
            1 -> convert1(helper, item)
            2 -> convert2(helper, item)
        }
    }

    private fun convert1(helper: BaseViewHolder?, item: DataEnrty?) {
        helper?.setText(R.id.tv, "${item?.data}  --->1")
    }

    private fun convert2(helper: BaseViewHolder?, item: DataEnrty?) {
        helper?.setText(R.id.tv, "${item?.data}   -->2")
    }

}