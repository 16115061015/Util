package com.hzy.cocos2dplay.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hzy.cocos2dplay.SpineManager
import com.hzy.cocos2dplay.getScreenHeight
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * User: hzy
 * Date: 2020/10/1
 * Time: 6:35 PM
 * Description:
 */
class SpinePlayView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {


    var completeListener: (() -> Unit)? = null
        set(value) {
            field = value
            spineView.completeListener = value
        }

    private val spineEventHandler = Handler(Looper.getMainLooper())

    private val spineView by lazy {
        SpineView(context, this, getScreenHeight(context))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        spineView
        SpineManager.setView(this)
    }

    fun onResume() {
        spineView.onResume()
    }

    fun onPause() {
        alpha = 0f
        spineView.onPause()
    }


    fun onStop() {
        spineView.release()
    }


    fun onDestroy() {
        spineView.release()
        SpineManager.removeView(this)
        completeListener = null
        spineEventHandler.removeCallbacksAndMessages(null)
        MainScope().launch {
            removeAllViews()
        }

    }

    fun playSpine(filePath: String) {
        spineView.runAnim(filePath, null)
    }

}

