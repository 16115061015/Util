package com.hzy.buildsrc

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
        pro.task("testTask").doLast {
            println("task Test....")
        }

    }
}

