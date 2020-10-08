package com.hzy.libgdxspine

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
            val path = resources.assets.open("spine2.zip")
            spine.playSpine(getSpinePathFromZip(path, "${filesDir}/gift/spine2"))
        }

        playSpine2.setOnClickListener {
            val path = resources.assets.open("spine.zip")
            spine.playSpine(getSpinePathFromZip(path, "${filesDir}/gift/spine1"))
        }


        playSpine3.setOnClickListener {
            val path = resources.assets.open("spine3.zip")
            spine.playSpine(getSpinePathFromZip(path, "${filesDir}/gift/spine3"))
        }

        spine.completeListener = {
            MainScope().launch {
                Toast.makeText(this@PlaySpineAty, "播放完毕", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        spine.onResume()
        super.onResume()
    }

    override fun onPause() {
        spine.onPause()
        super.onPause()
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