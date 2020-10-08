package com.hzy.libgdxspine

import android.app.Application
import android.content.Context

/**
 * User: hzy
 * Date: 2020/10/8
 * Time: 12:33 AM
 * Description:
 */
class MApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        globalContext = this
    }

    companion object {
        lateinit var globalContext: Context
    }

}