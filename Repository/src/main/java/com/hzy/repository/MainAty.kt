package com.hzy.repository

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:27 PM
 * Description: 数据仓库DEMO
 */
class MainAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv.setOnClickListener {
            request()
        }
    }


    private fun request() {

    }

}

