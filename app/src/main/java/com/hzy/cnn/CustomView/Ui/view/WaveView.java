package com.hzy.cnn.CustomView.Ui.view;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

import com.hzy.cnn.CustomView.R;

/**
 * Created by 胡泽宇 on 2018/5/21.
 * 带浮动View的波浪行View
 */

public class WaveView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    //子线程绘制标记
    private volatile boolean isDrawing;


    //波浪的高度
    private float WaveHeight;

    //波浪的宽度;
    private float WaveWidth;
    //波浪的起始Y位置（相对于底部）
    private float WaveInitY;
    //画出波浪的路径
    private Path path;
    //波浪移动一次的大小
    private float dx = 0;
    //波浪的移动速度（一次的时间）
    private int duration;

    //画笔
    private Paint paint;


    //浮动图片的位置
    private float[] iv_location;
    //浮动图片先对与path起始点的位置
    private float dis;
    //头像的半径
    private float HeadViewRadius;
    private Bitmap ImageViewBitmap;

    //View的大小
    private int WaveViewHeight;
    private int WaveViewWidth;


    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        init(context, attrs);

    }

    public WaveView(Context context) {
        this(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        //转化像素值
//        WaveWidth= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 600f, getResources().getDisplayMetrics());
//        WaveHeight= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 50f, getResources().getDisplayMetrics());

        //获取属性值
        paint = new Paint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        WaveWidth = typedArray.getDimension(R.styleable.WaveView_WaveWidth, 600);
        WaveHeight = typedArray.getDimension(R.styleable.WaveView_WaveHeight, 60);
        WaveInitY = typedArray.getDimension(R.styleable.WaveView_WaveInitY, 200);
        duration = typedArray.getInteger(R.styleable.WaveView_duration, 2000);
        HeadViewRadius = typedArray.getDimension(R.styleable.WaveView_flaotImageViewRadius, 120);
        paint.setColor(typedArray.getColor(R.styleable.WaveView_color, Color.BLUE));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Drawable drawable = typedArray.getDrawable(R.styleable.WaveView_floatImageView);
        typedArray.recycle();

        path = new Path();
        iv_location = new float[2];


        if (drawable == null) {//如果找不到图片资源，则从/res/drawble下面寻找图片资源

            drawable = getContext().getResources().getDrawable(R.mipmap.ic_launcher_round);
        }

        ImageViewBitmap = getCirleBitmap(DrawableToBitamp(drawable));

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
    }

    private Bitmap DrawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    //开启波浪动画 开启动画需要在Looper中启动线程否则会掉fps
    // 实例：
//     new Thread(new Runnable() {
//        @Override
//        public void run() {
//            Looper.prepare();
//            waveView.startWaveMove();
//            Looper.loop();
//        }
//    }).start();
    public void startWaveMove() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //向左位移
                float f = (float) animation.getAnimatedValue();
                dx = WaveWidth * f;
            }
        });
        animator.start();
    }


    //设置路径信息
    private void SetPathData() {
        float halfWaveWidth = WaveWidth / 2;
        path.reset();
        path.moveTo(-WaveWidth + dx, WaveHeight - WaveInitY + WaveViewHeight);
        for (float i = -WaveWidth; i < WaveViewWidth + WaveWidth; i += WaveWidth) {
            //相对位置
            path.rQuadTo(halfWaveWidth / 2, -WaveHeight, halfWaveWidth, 0);
            path.rQuadTo(halfWaveWidth / 2, WaveHeight, halfWaveWidth, 0);
        }
        //画矩形填充区域
        path.lineTo(WaveViewWidth, WaveViewHeight);
        path.lineTo(0, WaveViewHeight);
        path.close();

        //获取wave在屏幕中心点的Y坐标,dis为距离起始点的位置
        dis = WaveViewWidth / 2 + WaveWidth - dx;
        PathMeasure p = new PathMeasure(path, false);
        p.getPosTan(dis, iv_location, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取大小
        WaveViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        WaveViewWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (WaveInitY == 0) {
            WaveInitY = WaveViewHeight;
        }

    }

    private Bitmap getCirleBitmap(Bitmap bitmap) {
        float scale = 1.0f;
        Matrix matrix = new Matrix();
        //依据原有的图片丶创建一个新的图片   格式是：Config.ARGB_4444
        Bitmap bt = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        //创建一个画布
        Canvas canvas = new Canvas(bt);
        //获取缩放比例
        scale = HeadViewRadius * 2 / (Math.min(bitmap.getWidth(), bitmap.getHeight()));
        //设置缩放比例
        matrix.setScale(scale, scale);
        canvas.setMatrix(matrix);
        //创建一个画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //画笔的颜色
        paint.setColor(Color.WHITE);
        //画布的格式默认为  零
        canvas.drawARGB(0, 0, 0, 0);
        //求得圆的半径
        float radius = Math.min(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, radius, paint);
        //重置画笔
        paint.reset();
        //调用截图图层的方法
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //画图片
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bt;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (surfaceHolder) {
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
        while (isDrawing) {
            draw();
        }
    }

    private void draw() {
        while (true) {
            synchronized (surfaceHolder) {
                while (true) {
                    if (!isDrawing) {
                        return;
                    }

                    try {

                        canvas = surfaceHolder.lockCanvas();
                        //执行具体的绘制操
                        SetPathData();
                        if (canvas != null) {
                            canvas.drawColor(Color.TRANSPARENT);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            canvas.drawPaint(paint);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));


                            canvas.drawBitmap(ImageViewBitmap, WaveViewWidth / 2 - HeadViewRadius * 2,
                                    iv_location[1] - HeadViewRadius * 2,
                                    paint);
                            canvas.drawPath(path, paint);
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
