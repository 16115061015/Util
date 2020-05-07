package com.hzy.cnn.CustomView.application

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.facebook.drawee.backends.pipeline.Fresco

/**
 * User: hzy
 * Date: 2020/4/28
 * Time: 5:38 PM
 * Description:
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Fresco图片库初始化
        Fresco.initialize(this)

        if (isDebug()) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        //ARouter初始化 //需要在init之前调用ARouter.openDebug()
        ARouter.init(this)
    }

    //从配置文件中获取
    private fun isDebug(): Boolean {
        return true
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }
}