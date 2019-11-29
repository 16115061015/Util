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
import android.util.Log;
import android.view.View;

/**
 * Author: huzeyu
 * Date: 2019/11/29
 * Description:ViewPager圆点指示器
 */
public class BezierDot extends View implements ViewPager.OnPageChangeListener {

    private static final String TAG = "BezierDot";
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

    //贝塞尔曲线绘制点
    private Point[] bezierPoint;
    private Path bezierPath;

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

        bezierPoint = new Point[12];
        for (int i = 0; i < 12; i++) {
            bezierPoint[i] = new Point();
        }
        bezierPath = new Path();

    }

    private void SetData() {

        dotCount = viewPager.getAdapter().getCount();
        dotPosition = new Point[dotCount];
        for (int i = 0; i < dotCount; i++) {
            dotPosition[i] = new Point();
        }
        //延时防止获取宽高为0
        this.post(new Runnable() {
            @Override
            public void run() {
                //计算静态圆的半径
                radius = getHeight() / 2 - 10;
                //计算静态圆间距
                dotPadding = (getWidth() - dotCount * 2 * radius) / (dotCount - 1);
                dotPosition[0].set((int) radius, getHeight() / 2);

                for (int i = 1; i < dotCount; i++) {
                    dotPosition[i].set((int) (dotPosition[i - 1].x + 2 * radius + dotPadding), getHeight() / 2);
                }
                invalidate();
            }
        });

    }

    /***
     * 绘制静态圆形
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        if (viewPager == null || dotPosition == null) {
            return;
        }
        for (int i = 0; i < dotCount; i++) {
            canvas.drawCircle(dotPosition[i].x, dotPosition[i].y, radius, mPaint);
        }
        drawBezier(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        super.onDraw(canvas);
    }


    /***
     * 绘制贝塞尔曲线
     * 在数组保存的点中，其中p0,p3,p6,p9为圆的上下左右四个点
     * @param canvas
     */
    private void drawBezier(Canvas canvas) {
        bezierPath.reset();
        int circleCenterX = dotPosition[currentPosition].x;
        int circleCenterY = dotPosition[currentPosition].y;

        float mDifference = radius * C;
        //顶点
        bezierPoint[0].x = circleCenterX;
        bezierPoint[0].y = (int) (circleCenterY - radius);
        //右点
        bezierPoint[3].x = (int) (circleCenterX + radius);
        bezierPoint[3].y = circleCenterY;
        //底部点
        bezierPoint[6].x = circleCenterX;
        bezierPoint[6].y = (int) (circleCenterY + radius);
        //左边点
        bezierPoint[9].x = (int) (circleCenterX - radius);
        bezierPoint[9].y = circleCenterY;

        //贝塞尔锚点
        bezierPoint[1].x = (int) (bezierPoint[0].x + mDifference);
        bezierPoint[1].y = bezierPoint[0].y;
        bezierPoint[11].x = (int) (bezierPoint[0].x - mDifference);
        bezierPoint[11].y = bezierPoint[0].y;

        bezierPoint[2].x = bezierPoint[3].x;
        bezierPoint[2].y = (int) (bezierPoint[3].y - mDifference);
        bezierPoint[4].x = bezierPoint[3].x;
        bezierPoint[4].y = (int) (bezierPoint[3].y + mDifference);

        bezierPoint[5].x = (int) (bezierPoint[6].x + mDifference);
        bezierPoint[5].y = bezierPoint[6].y;
        bezierPoint[7].x = (int) (bezierPoint[6].x - mDifference);
        bezierPoint[7].y = bezierPoint[6].y;

        bezierPoint[8].x = bezierPoint[9].x;
        bezierPoint[8].y = (int) (bezierPoint[9].y + mDifference);
        bezierPoint[10].x = bezierPoint[9].x;
        bezierPoint[10].y = (int) (bezierPoint[9].y - mDifference);

        bezierPath.moveTo(bezierPoint[0].x, bezierPoint[0].y);

        bezierPath.cubicTo(bezierPoint[1].x, bezierPoint[1].y, bezierPoint[2].x, bezierPoint[2].y, bezierPoint[3].x, bezierPoint[3].y);
        bezierPath.cubicTo(bezierPoint[4].x, bezierPoint[4].y, bezierPoint[5].x, bezierPoint[5].y, bezierPoint[6].x, bezierPoint[6].y);
        bezierPath.cubicTo(bezierPoint[7].x, bezierPoint[7].y, bezierPoint[8].x, bezierPoint[8].y, bezierPoint[9].x, bezierPoint[9].y);
        bezierPath.cubicTo(bezierPoint[10].x, bezierPoint[10].y, bezierPoint[11].x, bezierPoint[11].y, bezierPoint[0].x, bezierPoint[0].y);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(bezierPath, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void bind(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        SetData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i(TAG, "onPageScrolled: " + positionOffset);
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
