package com.example.UiDemo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.UiDemo.R
import com.example.UiDemo.config.UiRouterConfig

/**
 * User: hzy
 * Date: 2020/5/7
 * Time: 11:05 AM
 * Description:MotionLayout实现动画布局
 */
@Route(path = UiRouterConfig.MotionLayout)
class AtyMotionLayout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_motionlayout)
    }

}