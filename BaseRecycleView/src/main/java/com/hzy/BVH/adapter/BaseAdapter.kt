package com.hzy.BVH.adapter

import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 5:18 PM
 * Description:
 */
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseVH>() {
    private var datas = mutableListOf<T>()

    //储存对应关系
    private val relationLayout = SparseIntArray()

    init {
        setLayout()
    }

    /***
     * 添加多布局对应layout
     */
    protected open fun addLayout(vararg pair: Pair<Int, Int>) {
        if (pair.isEmpty()) return
        for ((type, layoutId) in pair) {
            relationLayout.put(type, layoutId)
        }
    }


    abstract fun setLayout()

    /***
     * 添加单布局对应layout
     */
    protected fun addSingleLayout(id: Int) {
        addLayout(Pair(0, id))
    }

    /***
     * 添加单个布局layout
     */


    fun addData(data: List<T>) {
        this.datas.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        //创建
        val view = View.inflate(parent.context, relationLayout.get(viewType), null)
        return BaseVH(view)
    }

    override fun getItemCount(): Int = datas.size


    final override fun getItemViewType(position: Int): Int {
        return getItemType(datas[position])
    }

    open fun getItemType(data: T) = 0

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        bindData(holder, position, datas[position], getItemViewType(position))
    }

    abstract fun bindData(holder: BaseVH, position: Int, data: T, type: Int)

}

open class BaseVH(item: View) : ViewHolder(item) {
    private val viewCache = SparseArray<View>()


    fun setText(id: Int, @StringRes stringId: Int) {
        (getView(id) as TextView).text = itemView.context.getString(stringId)
    }

    fun setText(id: Int, string: String) {
        (getView(id) as TextView).text = string
    }

    fun setOnClick(id: Int, onClick: () -> Unit) {
        getView(id).setOnClickListener {
            onClick()
        }
    }

    private fun getView(id: Int): View {
        var view = viewCache.get(id)
        if (view == null) {
            view = itemView.findViewById(id)
            viewCache.put(id, view)
        }
        return view
    }

}