package com.hzy.cnn.CustomView.Ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: huzeyu
 * Date: 2019/11/28
 * Description:广告轮播控件
 */
public class BannerView extends ViewPager {
    String TAG = "BannerView";

    private BannerAdapter bannerAdapter;
    //轮播数据集合
    private List<View> sourceDataSets;

    //ViewPager滑动速度
    private int INTERVAL = 1000;


    //轮播速度
    private int AUTO_SCROLL_INTERVAL = 3000;

    //当前播放的页面
    private int currentPosition = 0;

    //表示刷新Banner
    private final int FRESH_BANNER = 99;

    //监听
    private ClickListener clickListener;

    private Handler refreshHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case FRESH_BANNER:
                    refreshHandler.removeMessages(FRESH_BANNER);
                    BannerView.this.setCurrentItem(currentPosition + 1, true);
                    refreshHandler.sendEmptyMessageDelayed(FRESH_BANNER, AUTO_SCROLL_INTERVAL);
                    break;
            }
            return true;
        }
    });

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        sourceDataSets = new ArrayList<>();
        this.setAdapter(initAdapter());
        this.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        addScrollerVelocity();
        refreshHandler.sendEmptyMessageDelayed(FRESH_BANNER, AUTO_SCROLL_INTERVAL);
    }

    /***
     * 设置Adapter
     * @return
     */
    protected BannerAdapter initAdapter() {
        bannerAdapter = new BannerAdapter();
        return bannerAdapter;
    }


    /**
     * 反射控制viewpager滑动的速度
     */
    private void addScrollerVelocity() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new SlowScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class SlowScroller extends Scroller {

        public SlowScroller(Context context) {
            super(context);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, INTERVAL);
        }
    }

    //
    private class BannerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % sourceDataSets.size();
            Log.i(TAG, "instantiateItem: " + position);
            container.removeView(sourceDataSets.get(position));
            container.addView(sourceDataSets.get(position));
            final int finalPosition = position;
            sourceDataSets.get(position).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.Click(finalPosition);
                    }
                }
            });

            return sourceDataSets.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //这里不用销毁View，因为Viewpager中的View都是外部传入复用的。
        }

        @Override
        public int getCount() {
            if (sourceDataSets.size() == 1) {
                return 1;
            } else {
                return Integer.MAX_VALUE;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /*****------------可设置方法----------------------***/
    /***
     *设置ViewPager切换速度
     * @param interval
     */
    public void setInterval(int interval) {
        this.INTERVAL = interval;
    }


    /***
     * 设置Banner切换速度
     * @param AUTO_SCROLL_INTERVAL
     */
    public void setAutoScrollInterbal(int AUTO_SCROLL_INTERVAL) {
        this.AUTO_SCROLL_INTERVAL = AUTO_SCROLL_INTERVAL;
    }

    /***
     * 设置数据集合
     * @param sourceDataSets
     */
    public void setSourceDataSets(List<View> sourceDataSets) {
        if (sourceDataSets != null) {
            //设置过渡界面
            this.sourceDataSets.addAll(sourceDataSets);
            this.setCurrentItem(sourceDataSets.size() * 50, true);
        }
    }

    /***
     * 设置监听
     * @param clickListener
     */
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getCurrentItem() {
        return super.getCurrentItem() % sourceDataSets.size();
    }

    /***
     * 返回真实的数据集合数量
     * @return
     */
    public int getItemCount() {
        return sourceDataSets.size();
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(listener);
    }

    interface ClickListener {
        void Click(int position);
    }
}
