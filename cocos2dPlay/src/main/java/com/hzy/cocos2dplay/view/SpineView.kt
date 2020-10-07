package com.hzy.cocos2dplay.view

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import org.cocos2dx.lib.Cocos2dxHelper
import org.cocos2dx.lib.Cocos2dxView
import org.cocos2dx.lib.SpineEventManager
import org.cocos2dx.lib.SpineHeadEntity

/**
 * User: hzy
 * Date: 2020/10/1
 * Time: 6:28 PM
 * Description:
 */
class SpineView(context: Context? = null, viewGroup: FrameLayout, contentHeight: Int) : Cocos2dxView(context, viewGroup, contentHeight) {

    var completeListener: (() -> Unit)? = null

    fun release() {
        Cocos2dxHelper.uninit(this)
        runOnGL {
            SpineEventManager.ins().postEvent(SpineEventManager.CODE_CLEAR_CURRENT)
        }
        runOnGL(200) {
            SpineEventManager.ins().postEvent(SpineEventManager.CODE_RELEASE)
            doExitView()
        }
        completeListener = null
    }

    fun runAnim(pathName: String, entities: List<SpineHeadEntity>?) {
        runOnGL {
            SpineEventManager.ins().postEvent(SpineEventManager.CODE_PLAY_GIFT, pathName, entities)
            resume()
        }
    }


    fun onPause() {
        runOnGL { SpineEventManager.ins().postEvent(SpineEventManager.CODE_CLEAR_CURRENT) }
        runOnGL(200) { pause() }
    }

    override fun animComplete(filepathName: String) {
        runOnGL {
            pause()
            completeListener?.invoke()
        }
    }


    fun onResume() {
        runOnGL {
            resume()
        }
    }

    private fun runOnGL(delayTime: Long = 0, block: (() -> Unit)? = null) {
        if (mViewGroup == null) return
        mViewGroup.postDelayed({
            runOnGLThread {
                block?.invoke()
            }
        }, delayTime)
    }


    override fun log(logString: String) {
        if (mViewGroup == null) return
        mViewGroup.post {
            Log.i("cocoslog from C++  %s", logString)
        }
    }
}
