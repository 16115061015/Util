package com.hzy.cnn.CustomView.Ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hzy.cnn.CustomView.R;

/**
 * Author: huzeyu
 * Date: 2019/11/25
 * Description:波浪水球动画--》可用于等级显示
 */
public class WaveBubblesView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Path wavePath;
    private Path circlePath;

    private Paint wavePaint;

    private int width;
    private int height;

    private Paint clearPaint;

    //y=Asin(ωx+φ)+k;
    //波浪开始x轴初始位置，通过改变这个值可以使波浪移动
    private float φ = 0;
    //波浪的宽度
    private double ω = 0.003f;
    //波浪存在高度位置
    private int k = 300;
    //波峰
    private float A = 200;

    //波浪移动的速率--->本质是φ每次减小的大小
    private float speed = 0.05f;


    private int radius;

    private int circleStartY;

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    //子线程绘制标记
    private volatile boolean isDrawing;

    public WaveBubblesView(Context context) {
        super(context);
        initView(context, null);
    }

    public WaveBubblesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public WaveBubblesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WaveBubblesView);
            speed = array.getFloat(R.styleable.WaveBubblesView_wave_speed, 5f) / 100;
            ω = array.getFloat(R.styleable.WaveBubblesView_wave_width, 3f) / 1000;
            k = (int) array.getFloat(R.styleable.WaveBubblesView_wave_start_y, 300);
            A = array.getFloat(R.styleable.WaveBubblesView_wave_height, 200);
            array.recycle();
        }
        wavePath = new Path();
        circlePath = new Path();

        wavePaint = new Paint();
        wavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        wavePaint.setAntiAlias(true);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);

        clearPaint = new Paint();
        clearPaint.setColor(Color.parseColor("#ffffff"));

        //设置背景为透明
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //获得宽高
        width = getWidth();
        height = getHeight();


        if (width > height) {
            radius = height / 2;
            circleStartY = 0;
        } else {
            radius = width / 2;
            circleStartY = height / 2 - radius;
        }

        Shader shader = new LinearGradient(0, circleStartY, radius * 2, circleStartY + radius * 2, new int[]{Color.parseColor("#87DFFF")
                , Color.parseColor("#87DFFF"), Color.parseColor("#37BEF0"), Color.parseColor("#6A72FF")}, null, Shader.TileMode.REPEAT);
        wavePaint.setShader(shader);
    }


    private void DrawWave(Canvas canvas) {

        wavePath.reset();
        φ -= speed;
        wavePath.moveTo(0, getHeight());
        //转换为距离底部k的y轴坐标
        for (int x = 0; x < width; x += 5) {
            float y = circleStartY + radius * 2 - (float) (A * Math.sin(ω * x + φ) + k);
            wavePath.lineTo(x, y);
        }
        wavePath.lineTo(getWidth(), getHeight());
        wavePath.lineTo(0, getHeight());
        wavePath.close();
        canvas.drawPath(wavePath, wavePaint);
    }

    private void DrawCircle(Canvas canvas) {
        circlePath.reset();
        circlePath.addCircle(getWidth() / 2, getHeight() / 2, radius, Path.Direction.CW);
        canvas.clipPath(circlePath);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (holder) {
            isDrawing = true;
            new Thread(this).start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (surfaceHolder) {
            isDrawing = false;
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (surfaceHolder) {
                while (true) {
                    if (!isDrawing) {
                        return;
                    }
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        if (canvas != null) {
                            //清屏
                            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            canvas.drawPaint(clearPaint);
                            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

                            DrawCircle(canvas);
                            DrawWave(canvas);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
