package com.hzy.libgdxspine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playSpine.setOnClickListener {
            //跳转礼物界面
            PlaySpineAty.start(this@MainActivity)
        }
    }


}



