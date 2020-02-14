package com.hzy.cnn.CustomView.Ui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by 胡泽宇 on 2018/5/28.
 * <p>
 * 仿支付成功界面
 */

public class AlipaySuccessView extends View {
    //画圆的画笔
    private Paint CirclePaint;
    //画直线的画笔
    private Paint LinePaint;
    //矩形
    private RectF rectF;
    //画圆动画
    private ValueAnimator drawcircleanimatior;
    //画直线的动画
    private ValueAnimator mlineanimator;


    //View 的大小
    private float ViewHeight;
    private float ViewWidth;

    //圆的大小
    private float CircleRadius;
    //角度
    private float SweepAngle = 0;
    //勾的路径
    private Path Linepath;
    //截取路径
    private Path ofPath;
    //
    private PathMeasure pathMeasure;


    //动画集
    private AnimatorSet animatiorSet;

    public AlipaySuccessView(Context context) {
        super(context);
        initobject();
    }

    public void startAnimator() {

        drawcircleanimatior = ValueAnimator.ofFloat(0, 360);
        drawcircleanimatior.setDuration(3000);
        drawcircleanimatior.setInterpolator(new LinearInterpolator());
        drawcircleanimatior.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SweepAngle = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        mlineanimator = ValueAnimator.ofFloat(0, 1);
        mlineanimator.setInterpolator(new LinearInterpolator());
        mlineanimator.setDuration(3000);
        mlineanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                ofPath.reset();
                ofPath.moveTo(ViewWidth / 2 - CircleRadius / 2, ViewHeight / 2 + CircleRadius / 4);
                //获取部分的path路径
                pathMeasure.getSegment(0, pathMeasure.getLength() * f, ofPath, false);
                postInvalidate();
                if (f == 1f) {
                    CirclePaint.setColor(Color.GREEN);
                    LinePaint.setColor(Color.GREEN);
                    startScaleAnimatorSet();
                }
            }
        });


        animatiorSet = new AnimatorSet();
        animatiorSet.play(drawcircleanimatior).before(mlineanimator);
        animatiorSet.start();


    }

    private void startScaleAnimatorSet() {
        //抖动
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 1.1f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(3000);
        set.setInterpolator(new BounceInterpolator());
        set.playTogether(scaleXAnim, scaleYAnim);
        set.start();
    }

    private void initobject() {
        CirclePaint = new Paint();
        LinePaint = new Paint();
        rectF = new RectF();

        CirclePaint.setColor(Color.WHITE);
        CirclePaint.setStrokeWidth(10);
        CirclePaint.setStyle(Paint.Style.STROKE);

        LinePaint.setColor(Color.WHITE);
        LinePaint.setStrokeWidth(15);
        LinePaint.setStyle(Paint.Style.STROKE);

        Linepath = new Path();
        ofPath = new Path();
        pathMeasure = new PathMeasure();
    }

    public AlipaySuccessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initobject();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DrawCircle(canvas);
        DrawLine(canvas);
        super.onDraw(canvas);
    }

    private void DrawLine(Canvas canvas) {

        canvas.drawPath(ofPath, LinePaint);
    }

    private void DrawCircle(Canvas canvas) {
        rectF.left = ViewWidth / 2 - CircleRadius;
        rectF.top = ViewHeight / 2 - CircleRadius;
        rectF.right = ViewWidth / 2 + CircleRadius;
        rectF.bottom = ViewHeight / 2 + CircleRadius;
        canvas.drawArc(rectF, 0, SweepAngle, false, CirclePaint);
        Log.i("设置角度", "DrawCircle: " + SweepAngle);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        ViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        CircleRadius = Math.min(ViewHeight, ViewWidth) / 2 - Math.min(ViewHeight, ViewWidth) / 7;
        Linepath.reset();
        Linepath.moveTo(ViewWidth / 2 - CircleRadius / 2, ViewHeight / 2 + CircleRadius / 4);
        Linepath.rLineTo(CircleRadius / 2, CircleRadius / 3);
        Linepath.lineTo(ViewWidth / 2 + CircleRadius / 3 * 2, ViewHeight / 2 - CircleRadius / 3);

        pathMeasure.setPath(Linepath, false);
    }
}