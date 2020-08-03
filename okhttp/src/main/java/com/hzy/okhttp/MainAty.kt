package com.hzy.okhttp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:27 PM
 * Description:
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
        val client = OkHttpClient()
        val request = Request.Builder().url("https://wanandroid.com/wxarticle/chapters/json").method("GET", null).build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    content.text = e.message
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val rs = response.message + "\n" + response.body?.string()
                MainScope().launch {
                    content.text = rs
                }
            }

        })
    }

}

