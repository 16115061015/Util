package com.hzy.cnn.CustomView.Test

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import androidx.appcompat.app.AppCompatActivity
import com.hzy.cnn.CustomView.R
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAParser.ParseCompletion
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.android.synthetic.main.aty_animation.*
import java.net.URL


/**
 * User: hzy
 * Date: 2020/4/28
 * Time: 4:00 PM
 * Description:
 */
class AniamtionTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_animation)

        SVGAParser.shareParser().init(this)
        btn.setOnClickListener {
            val parser = SVGAParser.shareParser()
            parser.decodeFromURL(URL("http://medtx.ikstatic.cn/MTU4MzkxNzc5NzEyMCM4NDMjc3ZnYQ==.svga"), object : ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    svga.setVideoItem(videoItem)
                    svga.startAnimation()
                }

                override fun onError() {
                }

            })
        }

        btn2.setOnClickListener{
//            lav.setComposition(LottieComposition())
            lav.playAnimation()
        }
    }
}