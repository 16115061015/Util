package com.hzy.aopdemo.aop

/**
 * User: hzy
 * Date: 2020/7/15
 * Time: 1:13 AM
 * Description: 埋点注入AOP
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ClickTrack {
}