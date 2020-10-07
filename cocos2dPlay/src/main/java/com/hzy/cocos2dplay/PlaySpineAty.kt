package com.hzy.cocos2dplay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.aty_playspine.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * User: hzy
 * Date: 2020/10/1
 * Time: 10:18 PM
 * Description:
 */
class PlaySpineAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_playspine)
        playSpine.setOnClickListener {
            //跳转礼物界面
            try {
                val path = resources.assets.open("spine.zip")
                spine.playSpine(getSpinePathFromZip(path, "${filesDir}/gift"))
            } catch (e: Exception) {
                Toast.makeText(this@PlaySpineAty, "${e.message}", Toast.LENGTH_SHORT).show()
            }

        }

        spine.completeListener = {
            MainScope().launch {
                Toast.makeText(this@PlaySpineAty, "播放完毕", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        spine.onResume()
    }

    override fun onPause() {
        super.onPause()
        spine.onPause()
    }

    override fun onStop() {
        super.onStop()
        spine.onStop()
    }


    override fun onDestroy() {
        spine.onDestroy()
        super.onDestroy()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, PlaySpineAty::class.java))
        }
    }
}