package com.hzy.cocos2dplay.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hzy.cocos2dplay.getScreenHeight

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


    private val spineView by lazy {
        SpineView(context, this, getScreenHeight(context))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        spineView
    }

    fun onResume() {
        spineView.onResume()
    }

    fun onPause() {
        alpha = 0f
        spineView.onPause()
    }


    fun onDestroy() {
        spineView.release()
        completeListener = null
        removeAllViews()
    }

    fun playSpine(filePath: String) {
        spineView.runAnim(filePath, null)
    }

}

