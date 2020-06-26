package com.hzy.rollscreenview

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.annotation.StringRes
import java.util.*

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:45 PM
 * Description: 滚动控件
 */
class RollScreenLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewFlipper(context, attrs) {
    var adapter: Adapter<*>? = null
        set(value) {
            adapter?.dataChangesListener = {
                showNext()
            }
            field = value
            showNext()
        }

    override fun showNext() {
        adapter?.getNextItemView(this)?.let {
            addView(it.itemView)
            super.showNext()
        }
    }


    abstract class Adapter<T> {
        private val data = LinkedList<T>()
        private val viewsCache = SparseArray<ViewHolder>()
        var dataChangesListener: () -> Unit? = {}

        fun setNewData(data: List<T>) {
            this.data.addAll(data)
            dataChangesListener.invoke()
        }

        private fun onCreateViewHolder(vp: ViewGroup, data: T): ViewHolder? {
            return onCreateView(vp, data)?.let { ViewHolder(it) }
        }

        abstract fun onCreateView(vp: ViewGroup, data: T): View?

        fun getNextItemView(vp: ViewGroup): ViewHolder? {
            if (data.isNullOrEmpty()) return null
            val data = data.poll()
            var viewHolder = viewsCache.get(getItemType(data))
            if (viewHolder == null) {
                viewHolder = onCreateViewHolder(vp, data)
                viewsCache.put(getItemType(data), viewHolder)
            }
            viewHolder?.let {
                viewHolder.itemView.parent?.let { (it as ViewGroup).removeView(viewHolder.itemView) }
                convert(data, viewHolder)
            }
            return viewHolder
        }

        open fun getItemType(data: T): Int = 0

        abstract fun convert(data: T, holder: ViewHolder)
    }

    class ViewHolder(var itemView: View) {
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
}

