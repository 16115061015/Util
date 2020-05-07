package com.example.UiDemo.layout.LabelLayout;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: huzeyu
 * Date: 2019/2/14
 * Description:标签布局
 */
public class LabelLayout<T> extends ViewGroup {
    private LabelAdapter labelAdapter;

    public LabelLayout(Context context) {
        super(context);
    }

    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int usedWidth = 0;
        int usedHeight = 0;
        int maxWidth = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;
            if (childWidth + usedWidth > widthSize) {
                //摆放到下一行
                usedHeight += childHeight;
                usedWidth = 0;
            } else {
                //当前行大小
                usedHeight = Math.max(usedHeight, childHeight);
            }
            usedWidth += childWidth;
            maxWidth = Math.max(usedWidth, maxWidth);
        }
        widthSize = widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.UNSPECIFIED ? widthSize : maxWidth;
        heightSize = heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.UNSPECIFIED ? heightSize : usedHeight;
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, widthMode)
                , MeasureSpec.makeMeasureSpec(heightSize, heightMode));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startWidth = 0;
        int startHeight = 0;
        int width = getWidth();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;
            if (childWidth + startWidth > width) {
                //摆放到下一行
                startHeight += childHeight;
                startWidth = 0;
            }
            child.layout(startWidth, startHeight,
                    startWidth + child.getMeasuredWidth() + lp.leftMargin,
                    startHeight + child.getMeasuredHeight() + lp.topMargin);
            startWidth += childWidth;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }


    public void setAdapter(LabelAdapter<T> labelAdapter) {
        removeAllViews();
        for (int i = 0; i < labelAdapter.getCount(); i++) {
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            View tagView = labelAdapter.getView(this, i, labelAdapter.getItem(i));
            tagView.setLayoutParams(lp);
            addView(tagView);
        }

    }
}
