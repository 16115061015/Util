package com.sgxy.hzy.photoselector.recyclerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SafeGridLayoutManager extends GridLayoutManager {

    //滑动速率设定  默认为1  【一般情况下不需要改变】
    private double speedRatio = 1.0f;

    public SafeGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SafeGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public SafeGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        override this method and implement code as below
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            int distance = super.scrollVerticallyBy(dy, recycler, state);
            if (distance == (int) (speedRatio * dy)) {
                return dy;
            } else {
                return distance;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setSpeedRatio(double speedRatio) {
        this.speedRatio = speedRatio;
    }

}
