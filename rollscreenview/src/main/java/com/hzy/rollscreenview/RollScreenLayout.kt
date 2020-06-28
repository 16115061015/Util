package com.hzy.rollscreenview

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
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
class RollScreenLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        ViewFlipper(context, attrs) {
    var dataChangesListener: () -> Unit = {
        if (!isFlipping) startFlipping()
    }
    var adapter: Adapter<*>? = null
        set(value) {
            field = value
            field?.dataChangesListener = dataChangesListener
            startFlipping()
        }

    override fun showNext() {
        val nextView = adapter?.getNextItemView(this)
        if (adapter?.getDataSize() == 0) adapter?.loadMoreListener?.invoke()
        if (nextView == null) return
        addView(nextView.itemView)
        super.showNext()
    }


    abstract class Adapter<T> {
        private val data = LinkedList<T>()
        private val viewsCache = SparseArray<Pair<ViewHolder?, ViewHolder?>>()
        var dataChangesListener: (() -> Unit?)? = null
        var loadMoreListener: (() -> Unit?)? = null
        fun setNewData(data: List<T>) {
            this.data.addAll(data)
            dataChangesListener?.invoke()
        }

        private fun onCreateViewHolder(vp: ViewGroup, data: T): ViewHolder? {
            return onCreateView(vp, data)?.let { ViewHolder(it) }
        }

        abstract fun onCreateView(vp: ViewGroup, data: T): View?

        fun getNextItemView(vp: ViewGroup): ViewHolder? {
            if (data.isNullOrEmpty()) return null
            val data = data.poll()
            var viewHolder = getViewFromCache(getItemType(data))
            if (viewHolder == null) {
                viewHolder = onCreateViewHolder(vp, data)
                addViewToCache(getItemType(data), viewHolder)
            }
            viewHolder?.let {
                viewHolder.itemView.parent?.let { (it as ViewGroup).removeView(viewHolder.itemView) }
                convert(data, viewHolder)
            }
            return viewHolder
        }

        open fun getItemType(data: T): Int = 0

        abstract fun convert(data: T, holder: ViewHolder)

        private fun getViewFromCache(key: Int): ViewHolder? {
            if (viewsCache.get(key) == null) return null
            val (first, second) = viewsCache.get(key)
            viewsCache.put(key, Pair(second, first))
            return second
        }

        private fun addViewToCache(key: Int, viewHolder: ViewHolder?) {
            if (viewsCache.get(key) == null) {
                viewsCache.put(key, Pair(viewHolder, null))
                return
            }
            val (_, second) = viewsCache.get(key)
            viewsCache.put(key, Pair(viewHolder, second))
        }

        fun getDataSize() = data.size
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

