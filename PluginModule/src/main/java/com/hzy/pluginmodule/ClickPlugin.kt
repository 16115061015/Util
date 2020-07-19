package com.hzy.pluginmodule

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: hzy
 * Date: 2020/7/15
 * Time: 11:04 PM
 * Description:
 */
class ClickPlugin:Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.create("testMe"){
            doLast{
                println("test last")
            }
        }
    }
}