package com.example.kotlindemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.kotlindemo.KotlinDemoConfig.KotlinDemoConfig
import com.hzy.rsa.RSA.RSAUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

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
}
