package com.hzy.cnn.CustomView.application

import kotlinx.coroutines.*

/**
 * User: hzy
 * Date: 2020/5/10
 * Time: 1:05 AM
 * Description:
 */
class Test {

}

fun main() {
    GlobalScope.launch {
        async {
            println("star---")
            delay(2000)
            println("end---")
        }.await()
        async {
            println("star2---")
            delay(1000)
            println("end2---")
        }
    }
}