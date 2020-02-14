package com.hzy.cnn.CustomView.Ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * Created by 胡泽宇 on 2018/6/24.
 * 波浪播放效果
 */

public class WavePlay extends View {
    //上升函数画笔
    private Paint UpPaint;
    private Path UpPath;
    //下降函数画笔
    private Path DownPath;
    private Paint DownPaint;
    //中央曲线画笔
    private Path CenterPath;
    private Paint CenterPaint;
    //渲染层画笔
    private Paint RenderingPaint;
    //渲染矩形块
    private RectF rectF;

    //采样点的数目
    private static int PointSize = 128;
    //x轴坐标
    private float[] preXPoint;

    //相位变化数值
    private float offset = 0;

    //View大小
    private float ViewHeight;
    private float ViewWidth;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            WavePlay.this.postInvalidate();
            Log.i("收到刷新指令", "handleMessage: ");
        }
    };

    public WavePlay(Context context) {
        super(context);
        initAnimation();
    }

    private void initAnimation() {
        //产生随机数方法线程启动
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 2f);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setDuration(900);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float) animation.getAnimatedValue();
            }
        });
        valueAnimator.start();
    }

    public WavePlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        ViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initPathAndPaint();
        if (UpPath != null && DownPath != null && CenterPath != null) {

            canvas.drawPath(UpPath, UpPaint);
            canvas.drawPath(DownPath, DownPaint);
            canvas.drawPath(CenterPath, CenterPaint);
//            //通知重绘
            mHandler.sendEmptyMessageAtTime(0, 1600);
        }
    }


    //初始化路径与画笔
    private void initPathAndPaint() {
        Log.i("相位偏移", "onDraw: " + offset);
        LinearGradient linearGradient1 = new LinearGradient(0, ViewWidth, ViewHeight / 2, ViewHeight / 2, new int[]{Color.argb(125, 0, 0, 205), Color.argb(200, 65, 105, 225), Color.argb(225, 0, 255, 127)}, new float[]{0.3f, 0.6f, 1f},
                Shader.TileMode.CLAMP);
        LinearGradient linearGradient2 = new LinearGradient(0, ViewWidth, ViewHeight / 2, ViewHeight / 2, new int[]{Color.argb(125, 255, 250, 250), Color.argb(200, 255, 218, 185), Color.argb(225, 255, 215, 0)}, new float[]{0.3f, 0.6f, 1f},
                Shader.TileMode.CLAMP);
        LinearGradient linearGradient3 = new LinearGradient(0, ViewWidth, ViewHeight / 2, ViewHeight / 2, new int[]{Color.argb(125, 224, 255, 255), Color.argb(200, 205, 205, 180), Color.argb(225, 139, 139, 122)}, new float[]{0.3f, 0.6f, 1f},
                Shader.TileMode.CLAMP);
        if (preXPoint == null) {
            preXPoint = new float[PointSize];
            for (int i = 0; i < PointSize; i++) {
                preXPoint[i] = (ViewWidth / PointSize * i);
            }
        }
        if (UpPaint == null) {
            UpPaint = new Paint();
            UpPaint.setShader(linearGradient1);
            UpPaint.setStyle(Paint.Style.FILL);
            UpPaint.setStrokeWidth(8);

        }

        if (DownPaint == null) {
            DownPaint = new Paint();
            DownPaint.setShader(linearGradient2);
            DownPaint.setStyle(Paint.Style.FILL);
            DownPaint.setStrokeWidth(8);
            DownPaint.setColor(Color.argb(255, 70, 130, 180));
        }
        if (CenterPaint == null) {
            CenterPaint = new Paint();
            CenterPaint.setShader(linearGradient3);
            CenterPaint.setStyle(Paint.Style.FILL);
            CenterPaint.setStrokeWidth(8);
            //argb(255,90,90,90)

        }
        if (UpPath == null) {
            UpPath = new Path();
        }
        if (DownPath == null) {
            DownPath = new Path();
        }
        if (CenterPath == null) {
            CenterPath = new Path();
        }
        CenterPath.reset();
        DownPath.reset();
        UpPath.reset();
        CenterPath.moveTo(0, ViewHeight / 2);
        DownPath.moveTo(0, ViewHeight / 2);
        UpPath.moveTo(0, ViewHeight / 2);
        for (int i = 0; i < PointSize; i++) {
            float x = preXPoint[i] / ViewWidth * 4;
            float y = (float) (0.5 * CaculatorY(x, -offset) / 4 * ViewWidth + ViewHeight / 2);
            UpPath.lineTo(preXPoint[i], y);
        }
        for (int i = 0; i < PointSize; i++) {
            float x = preXPoint[i] / ViewWidth * 4;
            float y = (float) (0.5 * CaculatorY(x, offset) / 4 * ViewWidth - ViewHeight / 2);
            DownPath.lineTo(preXPoint[i], y);
        }
        for (int i = 0; i < PointSize; i++) {
            float x = preXPoint[i] / ViewWidth * 4;
            float y = (float) (0.2 * CaculatorY(x, -(offset + 0.4f)) / 4 * ViewWidth + ViewHeight / 2);
            CenterPath.lineTo(preXPoint[i], y);
        }
    }

    //传入需要求的x坐标和初相
    private float CaculatorY(float x, float offset) {
        Log.i("相位偏转量", "offset " + offset);
        float y;
        y = (float) (Math.sin((0.75 * (x - 2) * Math.PI) + offset * Math.PI) *
                (Math.pow(4 / (4 + Math.pow(x - 2, 4)), 2.5)));
        return y;
    }
}
