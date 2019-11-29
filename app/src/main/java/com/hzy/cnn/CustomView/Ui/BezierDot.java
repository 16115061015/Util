package com.hzy.cnn.CustomView.Ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: huzeyu
 * Date: 2019/11/29
 * Description:ViewPager圆点指示器
 */
public class BezierDot extends View implements ViewPager.OnPageChangeListener {

    private Paint mPaint;
    private Path path = new Path();
    private static final float C = 0.552284749831f;     // 用来计算绘制圆形贝塞尔曲线控制点的位置的常数


    //每个圆点间距
    private float dotPadding = 20;

    //圆点数量
    private int dotCount;

    //圆心位置
    private Point[] dotPosition;
    //圆的半径
    private float radius;

    private ViewPager viewPager;

    private int currentPosition = 0;

    public BezierDot(Context context) {
        this(context, null, 0);
    }

    public BezierDot(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierDot(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

    }

    private void SetData(Canvas canvas) {
        if (viewPager == null || dotPosition == null) {
            return;
        }
        //计算静态圆的半径
        radius = getHeight() / 2 - 10;
        //计算静态圆间距
        dotPadding = (getWidth() - dotCount * 2 * radius) / (dotCount - 1);
        dotPosition[0].set((int) radius, getHeight() / 2);

        for (int i = 1; i < dotCount; i++) {
            dotPosition[i].set((int) (dotPosition[i - 1].x + 2 * radius + dotPadding), getHeight() / 2);
        }
        for (int i = 0; i < dotCount; i++) {
            if(i == currentPosition) {
                mPaint.setStyle(Paint.Style.FILL);
            }else{
                mPaint.setStyle(Paint.Style.STROKE);
            }
            canvas.drawCircle(dotPosition[i].x, dotPosition[i].y, radius, mPaint);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

//        canvas.translate(getWidth() / 2, getHeight() / 2);   // 将坐标系移动到画布中央

        drawPath(canvas);

        super.onDraw(canvas);
    }


    private void drawPath(Canvas canvas) {

        SetData(canvas);
        // 绘制贝塞尔曲线
//        mPaint.setColor(Color.RED);
//        mPaint.setStrokeWidth(6);
//        path.moveTo(mData[0], mData[1]);
//
//        path.cubicTo(mCtrl[0], mCtrl[1], mCtrl[2], mCtrl[3], mData[2], mData[3]);
//        path.cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5]);
//        path.cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7]);
//        path.cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15], mData[0], mData[1]);


//        canvas.drawPath(path, mPaint);


    }

    public void bind(ViewPager viewPager) {
        this.viewPager = viewPager;
        dotCount = viewPager.getAdapter().getCount();
        dotPosition = new Point[dotCount];
        for (int i = 0; i < dotCount; i++) {
            dotPosition[i] = new Point();
        }
        viewPager.addOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
