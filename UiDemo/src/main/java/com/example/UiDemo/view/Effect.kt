package com.example.UiDemo.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.Display
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide


/**
 * User: hzy
 * Date: 2020/4/14
 * Time: 7:35 PM
 * Description:
 */
class Effect : AppCompatImageView {
    var animatorSet = AnimatorSet()
    var isStop: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        initView(800, 800)
    }


    private fun initView(width: Int, height: Int) {
        val lp = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams = lp
    }

    private fun playAnimator() {
//        setImageResource(R.drawable.ic_launcher_background)
        val ac = context as Activity
        val defaultDisplay: Display = ac.windowManager.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        //Y方向平移
        val translateYAnimator = ObjectAnimator.ofFloat(this, "translationY", point.y.toFloat() - measuredHeight, (point.y / 2).toFloat())
        val scaleTransYAnimator = ObjectAnimator.ofFloat(this, "scaleX", 0f, 1f)
        val scaleTransXAnimator = ObjectAnimator.ofFloat(this, "scaleY", 0f, 1f)
        //中间缩放动画
        val scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1f, 2f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1f, 2f, 1f)
        //Y方向平移
        val releaseTranslateYAnimator = ObjectAnimator.ofFloat(this, "translationY", (point.y / 2 - measuredHeight).toFloat(), -100f)
        val releaseScaleTransYAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f)
        val releaseScaleTransXAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0f)
        animatorSet.play(translateYAnimator).with(scaleTransYAnimator).with(scaleTransXAnimator).before(scaleXAnimator)
        animatorSet.play(scaleXAnimator).with(scaleYAnimator).before(releaseTranslateYAnimator)
        animatorSet.play(releaseTranslateYAnimator).with(releaseTranslateYAnimator)
                .with(releaseScaleTransYAnimator).with(releaseScaleTransXAnimator)
        //设置动画时间
        animatorSet.duration = 4000
        //开始动画
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                removeEffect()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }

    override fun onDetachedFromWindow() {
        if (animatorSet.isRunning) animatorSet.cancel()
        super.onDetachedFromWindow()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        playAnimator()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    private fun removeEffect() {
        if (isStop) return
        isStop = true
        val vp = parent as ViewGroup
        vp.removeView(this)
    }

    companion object {
        private val tag: String? = "Effect"
        fun playEffect(activity: Activity, url: String) {
            val decorView = activity.window.decorView as FrameLayout
            if (decorView.findViewWithTag<Effect>(tag) != null) return
            val e = Effect(activity)
//            e.tag = tag
            decorView.addView(e)
            Glide.with(activity).load(url).into(e)

        }
    }

}