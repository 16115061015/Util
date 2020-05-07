package com.hzy.cnn.CustomView.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.UiDemo.config.UiRouterConfig
import com.hzy.cnn.CustomView.R
import com.hzy.cnn.CustomView.contract.Components
import com.sgxy.hzy.photoselector.config.PSRouteConfig
import kotlinx.android.synthetic.main.aty_main.*

/**
 * User: hzy
 * Date: 2020/5/7
 * Time: 1:06 PM
 * Description:
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_main)
        initComponents()
    }

    private fun initComponents() {
        recycleView.adapter = MainAdapter().apply {
            setNewData(listOf(
                    Components("MotionLayout演示", UiRouterConfig.MotionLayout),
                    Components("支付效果", UiRouterConfig.AlipaySuccessView),
                    // Components("Svg演示效果", MainRouterConfig.Lottie_SVGA_Demo),
                    Components("音乐波浪播放", UiRouterConfig.WavePlay),
                    Components("波浪头像", UiRouterConfig.WaveView),
                    Components("下拉刷新框架", UiRouterConfig.FreshLayout),
                    Components("水球等级动画", UiRouterConfig.WaveBubblesView),
                    Components("BannerView", UiRouterConfig.BannerView),
                    Components("标签布局", UiRouterConfig.LabelLayout),
                    Components("圆形指示器", UiRouterConfig.BezierDotIndicator),
                    Components("适配BannerView的指示器", UiRouterConfig.BannerViewIndicator),
                    Components("BVH框架使用", UiRouterConfig.BVH),
                    Components("图片选择器", PSRouteConfig.PhotoSelect)
            ))
            notifyDataSetChanged()
        }
    }
}