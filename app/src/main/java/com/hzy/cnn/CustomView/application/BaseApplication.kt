package com.hzy.cnn.CustomView.application

import android.app.Application
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
        Fresco.initialize(this)
//        val cacheDir = File(this.cacheDir,"http")
//        HttpResponseCache.install(cacheDir,1024  *  1024  *  128)
    }
}