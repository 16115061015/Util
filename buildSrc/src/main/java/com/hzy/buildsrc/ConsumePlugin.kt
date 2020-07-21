package com.hzy.buildsrc

import com.android.build.gradle.AppExtension
import com.hzy.buildsrc.ClickTrack.ClickTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: hzy
 * Date: 2020/7/15
 * Time: 12:23 PM
 * Description: 自定义Plugin
 */
class ConsumePlugin : Plugin<Project> {
    override fun apply(pro: Project) {
        //注入转化
        val appExtension = pro.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(ClickTransform())

    }
}

