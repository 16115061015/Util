package com.hzy.cocos2dplay

import com.hzy.cocos2dplay.view.SpinePlayView

/**
 * User: hzy
 * Date: 2020/10/2
 * Time: 6:02 PM
 * Description:
 */
object SpineManager {
    private var spineContainerView: SpinePlayView? = null

    fun setView(view: SpinePlayView) {
        spineContainerView?.onStop()
        spineContainerView = view
    }

    fun removeView(spView: SpinePlayView) {
        if (System.identityHashCode(spView) == System.identityHashCode(spineContainerView)) {
            spineContainerView = null
        }
    }
}