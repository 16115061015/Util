package com.zjh.gamelibgdx

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.hzy.libgdxspine.getScreenHeight
import com.hzy.libgdxspine.getScreenWidth

/**
 * User: hzy
 * Date: 2020/10/6
 * Time: 11:07 PM
 * Description:
 */
class SpineView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LibgdxView(context, attrs, defStyleAttr) {

    var completeListener: (() -> Unit)? = null

    private val config by lazy {
        AndroidApplicationConfiguration().apply {
            a = 8
            b = a
            g = b
            r = g
            useTextureView = true
        }
    }

    private var hasInit = false

    private val spineGiftController by lazy {
        SpineGiftController().apply {
            completeListener = {
                this@SpineView.completeListener?.invoke()
            }
        }
    }

    private val libgdxView by lazy {
        initializeForView(spineGiftController, config)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        spineGiftController.dispose()
        completeListener = null
        spineGiftController.completeListener = null
    }


    fun playSpine(file: String) {
        spineGiftController.playSpine(file)
    }

    private fun initView() {
        if (hasInit) return
        hasInit = true
        removeAllViews()
        libgdxView?.setOnTouchListener { _, _ ->
            performClick()
            false
        }
        this.addView(libgdxView, createLayoutParams())
    }


}