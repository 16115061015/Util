package com.hzy.cnn.CustomView.Ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hzy.cnn.CustomView.R
import com.hzy.cnn.CustomView.contract.DataEnrty

/**
 * User: hzy
 * Date: 2020/3/30
 * Time: 12:17 PM
 * Description:
 */
class LoadDataAdapter(i: Int = R.layout.load_data_item) : BaseQuickAdapter<DataEnrty, BaseViewHolder>(i) {
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