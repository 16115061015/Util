package com.hzy.cnn.CustomView.Test

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.hzy.cnn.CustomView.R
import com.hzy.cnn.CustomView.Ui.adapter.LoadDataAdapter
import com.hzy.cnn.CustomView.Ui.view.LoadMoreDataView
import com.hzy.cnn.CustomView.contract.DataEnrty
import kotlinx.android.synthetic.main.aty_loaddata.*
import kotlinx.coroutines.GlobalScope


/**
 * User: hzy
 * Date: 2020/3/29
 * Time: 11:57 PM
 * Description:
 */
open class LoadDataAty : AppCompatActivity() {
    private lateinit var adapter: LoadDataAdapter
    private var dataIndex: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_loaddata)
        initView()
        bindEvent()
        loadData()
        btn.setOnClickListener {
            checkIsAndroidO()
        }
    }

    open fun checkIsAndroidO() {
        if (Build.VERSION.SDK_INT >= 26) {
            val b = packageManager.canRequestPackageInstalls()
            if (b) {
                installApk()
            } else { //请求安装未知应用来源的权限
                var uri: Uri = Uri.parse("package:"+getPackageName())
                var intent =  Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,uri)
                startActivityForResult(intent, 19900)
            }
        } else {
            installApk()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> installApk()
            else -> {//  引导用户手动开启安装权}限
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                startActivityForResult(intent, 100)
            }

        }
    }

    private fun installApk() {
        Toast.makeText(this, "installAPK", Toast.LENGTH_SHORT).show()
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
        GlobalScope.run {
            var data = ArrayList<DataEnrty>()
            data.add(DataEnrty(1, "${dataIndex++}"))
            data.add(DataEnrty(2, "${dataIndex++}"))
            data.add(DataEnrty(1, "${dataIndex++}"))
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