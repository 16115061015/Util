package com.sgxy.hzy.photoselector.widget.cropimage;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;


@SuppressLint("AppCompatCustomView")
public class CropImageView extends ImageView {
    public static final float DEFAULT_SCALE_SIZE = 1.0f;//默认的宽高比
//    public static final float DEFAULT_SCALE_SIZE = 0.625f;//默认的宽高比

    protected Matrix mBaseMatrix = new Matrix();
    protected Matrix mSuppMatrix = new Matrix();
    private final Matrix mDisplayMatrix = new Matrix();
    private final float[] mMatrixValues = new float[9];
    private static final int MAX_WIDTH = 1024; //最大宽度的1024  因为webp的运算量
    private static final float MAX_WIDTH_SCALE = 0.8f; //边框的宽与屏幕宽度的比例

    private float minScale;
    private float maxScale;
    private int mWidth;
    private int mHeight;

    protected final RotateBitmap mBitmapDisplayed = new RotateBitmap(null);

    private Runnable mOnLayoutRunnable = null;

    private static final float SCALE_RATE = 1.25F;

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("NewApi")
    private void init() {

        try {
            if (Build.VERSION.SDK_INT >= 11) {
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        } catch (Error e) {
            e.printStackTrace();
        }
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(lineWidth);

        dashPaint = new Paint();
        dashPaint.setStyle(Style.STROKE);
        PathEffect effect = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        dashPaint.setAntiAlias(true);
        dashPaint.setPathEffect(effect);
        dashPaint.setColor(0xffffffff);
        dashPaint.setStrokeWidth(2.5f);

        setScaleType(ScaleType.MATRIX);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    protected float getScale(Matrix matrix) {
        float scale = Math.abs(getValue(matrix, Matrix.MSCALE_X));
        if (scale == 0.0f) {
            scale = Math.abs(getValue(matrix, Matrix.MSKEW_X));
        }
        return scale;
    }

    private float getScale() {
        return getScale(mSuppMatrix);
    }

    protected Matrix getImageViewMatrix() {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }

    public void setRoate(int degree) {
        if (mBitmapDisplayed.getBitmap() != null) {
            mSuppMatrix.postRotate(degree, getWidth() / 2, getHeight() / 2);
            setImageMatrix(getImageViewMatrix());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = right - left;
        mHeight = bottom - top;
        Runnable r = mOnLayoutRunnable;
        if (r != null) {
            mOnLayoutRunnable = null;
            r.run();
        }
        if (mBitmapDisplayed.getBitmap() != null) {
            getProperBaseMatrix(mBitmapDisplayed, mBaseMatrix);
            setImageMatrix(getImageViewMatrix());
        }
    }

    private void setImageBitmap(Bitmap bitmap, int rotation) {
        super.setImageBitmap(bitmap);
        Drawable d = getDrawable();
        if (d != null) {
            d.setDither(true);
        }

        mBitmapDisplayed.setBitmap(bitmap);
        mBitmapDisplayed.setRotation(rotation);

    }

    public void clear() {
        setImageBitmapResetBase(null, true);
    }

    public void setImageBitmapResetBase(final Bitmap bitmap, final boolean resetSupp) {
        setImageRotateBitmapResetBase(new RotateBitmap(bitmap), resetSupp);
    }

    public void setImageRotateBitmapResetBase(final RotateBitmap bitmap, final boolean resetSupp) {
        final int viewWidth = getWidth();
        if (viewWidth <= 0) {
            mOnLayoutRunnable = new Runnable() {
                @Override
                public void run() {
                    setImageRotateBitmapResetBase(bitmap, resetSupp);
                }
            };
            return;
        }

        if (bitmap.getBitmap() != null) {
            getProperBaseMatrix(bitmap, mBaseMatrix);
            setImageBitmap(bitmap.getBitmap(), bitmap.getRotation());
        } else {
            mBaseMatrix.reset();
            setImageBitmap(null);
        }

        if (resetSupp) {
            mSuppMatrix.reset();
        }
        setImageMatrix(getImageViewMatrix());
    }

    private void getProperBaseMatrix(RotateBitmap bitmap, Matrix matrix) {

        calcHightlight();
        int borderWidth = highlightWidth;
        int borderHeight = highlightHeight;

        int bmWidth = bitmap.getWidth();
        int bmHeight = bitmap.getHeight();

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        float startScale = 1.0f;
        //先尝试缩放到屏幕展示范围之内
        if (bmWidth > viewWidth || bmHeight > viewHeight) {
            float scaleW = viewWidth * 1.0f / bmWidth;
            float scaleH = viewHeight * 1.0f / bmHeight;
            startScale = Math.min(scaleW, scaleH);
            bmWidth = (int) (bmWidth * startScale);
            bmHeight = (int) (bmHeight * startScale);
        }
        float scaleW = borderWidth * 1.0f / bmWidth;
        float scaleH = borderHeight * 1.0f / bmHeight;
        //以最短的边为依据
        float min = Math.min(scaleW, scaleH);
        //确保至少铺满边框范围
        if (bmWidth < borderWidth || bmHeight < borderHeight) {
            startScale = min * startScale;
            minScale = 1.0f;
            bmWidth = (int) (bmWidth * min);
            bmHeight = (int) (bmHeight * min);
        } else {
            minScale = min;
        }
        maxScale = 10 * minScale;
        matrix.reset();

        matrix.postConcat(bitmap.getRotateMatrix());
        matrix.postScale(startScale, startScale);

        float startX = ((viewWidth - bmWidth) / 2f);
        float startY = ((viewHeight - bmHeight) / 2f);
        matrix.postTranslate(startX, startY);
    }

    private void zoom(float rate) {
        if (mBitmapDisplayed.getBitmap() == null) {
            return;
        }

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        mSuppMatrix.postScale(rate, rate, cx, cy);
        setImageMatrix(getImageViewMatrix());
        invalidate();
    }

    protected void postTranslate(float dx, float dy) {
        mSuppMatrix.preTranslate(0, 0);
        mSuppMatrix.postTranslate(dx, dy);
        invalidate();

    }

    /**
     * 放大
     */
    public void zoomIn() {
        float scale = getScale();
        if (scale <= 0) {
            return;
        }
        if (scale >= maxScale) {
            return;
        }
        float dst = maxScale / scale;
        zoom(Math.min(dst, SCALE_RATE));
        checkBounds();
    }

    /**
     * 缩小
     */
    public void zoomOut() {
        float scale = getScale();
        if (scale <= minScale) {
            return;
        }
        float dst = minScale / scale;
        zoom(Math.max(dst, 1.0f / SCALE_RATE));
        checkBounds();
    }

    public void moveBy(float dx, float dy) {
        postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());

    }

    private float preValue;
    private ValueAnimator animatorTrans;

    public void smoothMove(final float dx, final float dy) {
        stopAnim();
        preValue = 0f;
        animatorTrans = ObjectAnimator.ofFloat(0f, 100f);
        animatorTrans.setDuration(200);
        animatorTrans.setInterpolator(new DecelerateInterpolator(3.0f));
        animatorTrans.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float nowValue = (float) animation.getAnimatedValue();
                float dxStep = (nowValue - preValue) / 100f * dx;
                float dyStep = (nowValue - preValue) / 100f * dy;
                preValue = nowValue;
                moveBy(dxStep, dyStep);
            }
        });
        animatorTrans.start();
    }

    float mLastX;
    float mLastY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                stopAnim();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    mode = ZOOM;
                    lastDistance = getMoveDistance(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    moveBy(event.getX() - mLastX, event.getY() - mLastY);
                } else if (mode == ZOOM) {
                    float distance = getMoveDistance(event);
                    float rate = distance / lastDistance;
                    zoom(rate);
                    lastDistance = distance;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (mode == ZOOM) {
                    checkScale();
                }
                checkBounds();
                mode = NONE;
                break;
        }
        mLastX = event.getX();
        mLastY = event.getY();
        return true;
    }

    private void checkScale() {
        float scale = getScale();
        if (scale <= 0) {
            return;
        }
        if (scale > maxScale) {
            zoom(maxScale / scale);
        }
        if (scale < minScale) {
            zoom(minScale / scale);
        }
    }

    int mode;
    float lastDistance;

    final static int NONE = 0;
    final static int DRAG = 1;
    final static int ZOOM = 2;

    private void checkBounds() {
        RectF rectF = getRectF();
        float imgWidth = rectF.right - rectF.left;
        float imgHeight = rectF.bottom - rectF.top;
        float boundWidth = mRight - mLeft;
        float boundHeight = mBottom - mTop;
        float dx = 0.0f, dy = 0.0f;

        //根据宽度和高度的大小分情况处理
        if (imgWidth < imgHeight) {
            //如果高度大于宽度，那么上下两边界一定要保证顶边
            if (rectF.top > mTop) {
                dy = mTop - rectF.top;
            }
            if (rectF.bottom < mBottom) {
                dy = mBottom - rectF.bottom;
            }
            if (imgWidth > boundWidth) {
                //如果缩放后的图片宽度大于框的宽度，则保持框的区域充满图片
                if (rectF.left > mLeft) {
                    dx = mLeft - rectF.left;
                }
                if (rectF.right < mRight) {
                    dx = mRight - rectF.right;
                }
            } else {
                //如果缩放后的图片宽度小于框的宽度，则保持图片完全在框的区域内
                if (rectF.left < mLeft) {
                    dx = mLeft - rectF.left;
                }
                if (rectF.right > mRight) {
                    dx = mRight - rectF.right;
                }
            }
        } else {
            //如果高度小于宽度，那么左右两边界一定要保证顶边
            if (rectF.left > mLeft) {
                dx = mLeft - rectF.left;
            }
            if (rectF.right < mRight) {
                dx = mRight - rectF.right;
            }
            if (imgHeight > boundHeight) {
                //如果缩放后的图片高度度大于框的高度，则保持框的区域充满图片
                if (rectF.top > mTop) {
                    dy = mTop - rectF.top;
                }
                if (rectF.bottom < mBottom) {
                    dy = mBottom - rectF.bottom;
                }
            } else {
                //如果缩放后的图片高度小于框的高度，则保持图片完全在框的区域内
                if (rectF.top < mTop) {
                    dy = mTop - rectF.top;
                }
                if (rectF.bottom > mBottom) {
                    dy = mBottom - rectF.bottom;
                }
            }
        }
        smoothMove(dx, dy);
    }

    @SuppressLint("FloatMath")
    private float getMoveDistance(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private float mSizeScale = DEFAULT_SCALE_SIZE;//裁剪区域的宽高比-默认是1:1
    private int highlightWidth = 0;
    private int highlightHeight = 0;

    private int highlightXOffset = 0;
    private int highlightYOffset = 0;

    /**
     * 设置宽高比
     */
    public void setSizeScale(float sizeScale) {
        this.mSizeScale = sizeScale;
        postInvalidate();
    }

    void calcHightlight() {
        highlightWidth = Math.min((int) (getWidth() * MAX_WIDTH_SCALE), MAX_WIDTH);
        highlightHeight = (int) (highlightWidth * mSizeScale);
        highlightXOffset = (getWidth() - highlightWidth) / 2;
        highlightYOffset = (getHeight() - highlightHeight) / 2;
    }

    int mLeft;
    int mRight;
    int mTop;
    int mBottom;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        super.onDraw(canvas);

        mLeft = highlightXOffset;
        mTop = highlightYOffset;
        mRight = mLeft + highlightWidth;
        mBottom = mTop + highlightHeight;

        canvas.clipRect(mLeft, mTop, mRight, mBottom, Region.Op.DIFFERENCE);
        canvas.clipRect(0, 0, mWidth, mHeight);
        canvas.drawColor(0xcf000000);

        canvas.restore();
        if (needDash) {
            canvas.drawLine(0, (int) (mTop * 1.2), getMeasuredWidth(), (int) (mTop * 1.2), dashPaint);
            canvas.drawLine(0, (int) (mTop * 1.2) + 200, getMeasuredWidth(), (int) (mTop * 1.2) + 200, dashPaint);
        }
        canvas.drawLine(mLeft - lineWidth, mTop - lineWidth, mLeft - lineWidth, mBottom, linePaint);
        canvas.drawLine(mLeft - lineWidth, mTop - lineWidth, mRight, mTop - lineWidth, linePaint);
        canvas.drawLine(mRight, mTop - lineWidth, mRight, mBottom, linePaint);
        canvas.drawLine(mRight, mBottom, mLeft - lineWidth, mBottom, linePaint);
    }

    private Paint dashPaint;
    private Paint linePaint;

    private boolean needDash = true;

    public void setNeedDash(boolean needDash) {
        this.needDash = needDash;
        invalidate();
    }

    public RectF getRectF() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        mDisplayMatrix.mapRect(rectF);
        return rectF;
    }

    public int getCropLeft() {
        return (int) getRectF().left > mLeft ? (int) getRectF().left : mLeft;
    }

    public int getCropRight() {
        return (int) getRectF().right < mRight ? (int) getRectF().right : mRight;
    }

    public int getCropTop() {
        return (int) getRectF().top > mTop ? (int) getRectF().top : mTop;
    }

    public int getCropBottom() {
        return (int) getRectF().bottom < mBottom ? (int) getRectF().bottom : mBottom;
    }

    int lineWidth = 1;

    /*public Bitmap getCropBitmap() {
        setNeedDash(false);
        setDrawingCacheEnabled(false);
        setDrawingCacheEnabled(true);

        Bitmap b = getDrawingCache();
        setNeedDash(true);
        return b;
    }*/

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    private void stopAnim() {
        if (animatorTrans != null) {
            animatorTrans.removeAllUpdateListeners();
            animatorTrans.cancel();
            animatorTrans = null;
        }
    }
}