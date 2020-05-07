package com.sgxy.hzy.photoselector.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Simple Drawee View的安全版本
 */
public class SafetySimpleDraweeView extends SimpleDraweeView {
    /**
     * 构造函数
     *
     * @param context
     * @param hierarchy
     */
    public SafetySimpleDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    /**
     * 构造函数
     *
     * @param context
     */
    public SafetySimpleDraweeView(Context context) {
        super(context);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public SafetySimpleDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SafetySimpleDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public SafetySimpleDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
        }
    }
}
