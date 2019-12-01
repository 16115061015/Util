package com.hzy.cnn.CustomView.Ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Author: huzeyu
 * Date: 2019/11/29
 * Description:ViewPager圆点指示器--->适配BannerView
 */
public class BannerViewIndicator extends BezierDotIndicator {
    private static final String TAG = "BezierDotIndicator";

    public BannerViewIndicator(Context context) {
        super(context);
    }

    public BannerViewIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerViewIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        //关闭贝塞尔滑动效果
        setBezierChange(false);
    }

    @Override
    public void bind(ViewPager viewPager) {
        if (!(viewPager instanceof BannerView)) {
            Log.i(TAG, "bind: error viewpager type");
            return;
        }
        super.bind(viewPager);
    }

    @Override
    protected int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    @Override
    protected int getItemCount() {
        return ((BannerView) viewPager).getItemCount();
    }
}
