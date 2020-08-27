package com.hzy.repository.base

import android.app.Application

/**
 * User: hzy
 * Date: 2020/8/13
 * Time: 2:30 PM
 * Description:
 */
class CustomApplication:Application() {
    override fun onCreate() {
        super.onCreate()

    }

    fun initDB() {
//        val boxStore = MyObjectBox.builder().androidContext(mApplication.getApplicationContext()).build();
    }

}