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
public class BezierDotIndicator extends View implements ViewPager.OnPageChangeListener {

    private static final String TAG = "BezierDot";
    private Paint mPaint;
    private static final float C = 0.552284749831f;     // 用来计算绘制圆形贝塞尔曲线控制点的位置的常数


    //每个圆点间距
    private float dotPadding = 20;

    //圆点数量
    private int dotCount;

    //圆心位置
    private Point[] dotPosition;
    //圆的半径
    private float radius;

    protected ViewPager viewPager;

    private int currentPosition = 0;

    //贝塞尔曲线绘制点
    private Point[] bezierPoint;
    private Path bezierPath;


    //贝塞尔曲线右边坐标增量
    private float rightIncremental = 0;
    //贝塞尔曲线左边左边增量
    private float leftIncremental = 0;
    //贝塞尔曲线中心偏移量
    private float centerIncremental = 0;
    //贝塞尔上下点减量
    private float incrementalOfUpAndDown = 0;


    private final int DIRECTION_LEFT = -1;
    private final int DIRECTION_RIGHT = 1;

    //当前viewpager的index
    private int currentViewPagerIndex;


    public void setBezierChange(boolean bezierChange) {
        this.bezierChange = bezierChange;
    }

    //是否开启贝塞尔变化
    private boolean bezierChange = true;

    public BezierDotIndicator(Context context) {
        this(context, null, 0);
    }

    public BezierDotIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierDotIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        dotCount = getItemCount();
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
        int circleCenterX = (int) (dotPosition[currentPosition].x + centerIncremental);
        int circleCenterY = dotPosition[currentPosition].y;

        float mDifference = radius * C;
        //顶点
        bezierPoint[0].x = circleCenterX;
        bezierPoint[0].y = (int) (circleCenterY - radius + incrementalOfUpAndDown);
        //右点
        bezierPoint[3].x = (int) (circleCenterX + radius + rightIncremental);
        bezierPoint[3].y = circleCenterY;
        //底部点
        bezierPoint[6].x = circleCenterX;
        bezierPoint[6].y = (int) (circleCenterY + radius - incrementalOfUpAndDown);
        //左边点
        bezierPoint[9].x = (int) (circleCenterX - radius - leftIncremental);
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
        this.viewPager.addOnPageChangeListener(this);
        SetData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0 || positionOffset == 1 || !bezierChange) return;
        if (currentViewPagerIndex == position) {
            //右
            FirstStageOfMovement(positionOffset, DIRECTION_RIGHT);
            if (positionOffset >= 0.2) {
                SecondStageOfMovement(positionOffset, DIRECTION_RIGHT);
            }
            if (positionOffset >= 0.5) {
                ThirdStageOfMovement(positionOffset, DIRECTION_RIGHT);
            }
        } else {
            //左
            FirstStageOfMovement(positionOffset, DIRECTION_LEFT);
            if (positionOffset <= 0.8) {
                SecondStageOfMovement(positionOffset, DIRECTION_LEFT);
            }
            if (positionOffset <= 0.5) {
                ThirdStageOfMovement(positionOffset, DIRECTION_LEFT);
            }
        }
        postInvalidate();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 1) {
            currentViewPagerIndex = getCurrentItem();
        } else if (state == 0) {
            resetPosition();
            postInvalidate();
        }


    }

    /***
     * 0~1阶段贝塞尔右顶点坐标移动
     * 0~0.5增长 0.5~1减小
     * @param positionOffset
     */
    private void FirstStageOfMovement(float positionOffset, int Direction) {
        if (Direction == DIRECTION_RIGHT) {
            if (positionOffset >= 0.5) {
                rightIncremental = ((radius * 2 + dotPadding) / 2) * ((1 - positionOffset) / 0.5f);
            } else {
                rightIncremental = ((radius * 2 + dotPadding) / 2) * (positionOffset / 0.5f);
            }
        } else {
            positionOffset = 1 - positionOffset;
            if (positionOffset >= 0.5) {
                leftIncremental = ((radius * 2 + dotPadding) / 2) * ((1 - positionOffset) / 0.5f);
            } else {
                leftIncremental = ((radius * 2 + dotPadding) / 2) * (positionOffset / 0.5f);
            }
        }
    }

    /***
     * 0.2~1阶段压缩上下两点坐标
     * 0.2~0.6压缩
     * 0.6~1恢复
     * @param positionOffset
     */
    private void SecondStageOfMovement(float positionOffset, int Direction) {
        if (Direction == DIRECTION_LEFT) positionOffset = 1 - positionOffset;
        if (positionOffset >= 0.6) {
            incrementalOfUpAndDown = radius / 2 * ((0.8f - (positionOffset - 0.2f)) / 0.4f);
        } else {
            incrementalOfUpAndDown = radius / 2 * ((positionOffset - 0.2f) / 0.4f);
        }
    }

    /***
     * 0.5~1阶段 贝塞尔中心坐标向右移动-->
     * @param positionOffset
     */
    private void ThirdStageOfMovement(float positionOffset, int Direction) {
        if (Direction == DIRECTION_LEFT) positionOffset = 1 - positionOffset;
        centerIncremental = Direction * (radius * 2 + dotPadding) * ((positionOffset - 0.5f) / 0.5f);
    }


    private void resetPosition() {
        //贝塞尔曲线右边坐标增量
        rightIncremental = 0;
        //贝塞尔曲线左边左边增量
        leftIncremental = 0;
        //贝塞尔曲线中心偏移量
        centerIncremental = 0;
        //贝塞尔上下点减量
        incrementalOfUpAndDown = 0;
        currentPosition = getCurrentItem();
    }

    /***
     * 获取viewpager当前显示页面的Item，子类可重写改变真实值
     * @return
     */
    protected int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    /***
     * 获取ViewPager所有Item数量
     * @return
     */
    protected int getItemCount() {
        return viewPager.getAdapter().getCount();
    }

}
