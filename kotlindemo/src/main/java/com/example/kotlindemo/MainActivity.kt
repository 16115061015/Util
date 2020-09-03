package com.example.kotlindemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.kotlindemo.KotlinDemoConfig.KotlinDemoConfig
import com.hzy.rsa.RSA.RSAUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import rx.Single

@Route(path = KotlinDemoConfig.Demo)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetSo.setOnClickListener {
            Toast.makeText(this, RSAUtils.rsaLock(), Toast.LENGTH_SHORT).show()
        }
        btnGetResult.setOnClickListener {
            //挂起函数示例
            lifecycleScope.launch(Dispatchers.Main) {
                launch { }

            }
//            封装异步请求示例
            var job = req(
                    { delaySomeTime() },
                    { tvResult.text = it },
                    lifecycleScope
            )
        }

    }


    private suspend fun delaySomeTime(): String {
        delay(100)
        return "suspend return"
    }


    /***
     * @param doSomeThing 耗时IO操作
     * @param Success 成功回调
     * @param scope 协程范围控制器
     */
    fun <T> req(method: suspend () -> T, Success: (T) -> Unit, scope: CoroutineScope): Job {
        return scope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                method()
            }
            Success(result)
        }
    }


    fun call(): Single<Boolean> {
        return Single.create {
            GlobalScope.launch {
                val fun1Rs = async {
                    fun1()
                }
                val fun2Rs = async(this.coroutineContext, CoroutineStart.LAZY) {
                    fun2()
                }
                val fun3Rs = async(this.coroutineContext, CoroutineStart.LAZY) {
                    fun3()
                }
                it.onSuccess(if (fun1Rs.await()) fun2Rs.await() else fun3Rs.await())
            }
        }
    }
}

fun fun1(): Boolean {
    return true
}

fun fun2(): Boolean {
    return true
}

suspend fun fun3(): Boolean {
    return true
}

