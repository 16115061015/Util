package com.hzy.BVH.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView


/**
 * User: hzy
 * Date: 2020/8/25
 * Time: 1:40 PM
 * Description:无限循环RecycleView
 */
class AutoPollAdapter : RecyclerView.Adapter<AutoPollVH>() {
    private var data = mutableListOf<@DrawableRes Int>()

    fun setData(data: MutableList<Int>) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoPollVH {
        return AutoPollVH(SimpleDraweeView(parent.context).apply {
            aspectRatio = 0.815f
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        })
    }

    override fun getItemCount() = Int.MAX_VALUE

    override fun onBindViewHolder(holder: AutoPollVH, position: Int) {
        val iv = holder.itemView as SimpleDraweeView
        iv.setActualImageResource(data[position % data.size])
    }
}


class AutoPollVH(item: View) : RecyclerView.ViewHolder(item) {
}