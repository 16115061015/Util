//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.cocos2dx.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class Cocos2dxGLSurfaceView extends GLTextureView {
    private static final String TAG = Cocos2dxGLSurfaceView.class.getSimpleName();
    private static Cocos2dxGLSurfaceView mCocos2dxGLSurfaceView;
    private Cocos2dxRenderer mCocos2dxRenderer;
    private boolean mSoftKeyboardShown = false;

    public Cocos2dxGLSurfaceView(Context context) {
        super(context);
        this.initView();
    }

    public Cocos2dxGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    protected void initView() {
        this.setFocusableInTouchMode(true);
        mCocos2dxGLSurfaceView = this;
    }

    public boolean isSoftKeyboardShown() {
        return this.mSoftKeyboardShown;
    }

    public void setSoftKeyboardShown(boolean softKeyboardShown) {
        this.mSoftKeyboardShown = softKeyboardShown;
    }

    public static Cocos2dxGLSurfaceView getInstance() {
        return mCocos2dxGLSurfaceView;
    }

    public static void queueAccelerometer(float x, float y, float z, long timestamp) {
    }

    public void setCocos2dxRenderer(Cocos2dxRenderer renderer) {
        this.mCocos2dxRenderer = renderer;
        this.setRenderer(this.mCocos2dxRenderer);
    }

    private String getContentText() {
        return this.mCocos2dxRenderer.getContentText();
    }

    protected void onSizeChanged(int pNewSurfaceWidth, int pNewSurfaceHeight, int pOldSurfaceWidth, int pOldSurfaceHeight) {
        super.onSizeChanged(pNewSurfaceWidth, pNewSurfaceHeight, pOldSurfaceWidth, pOldSurfaceHeight);
        if (!this.isInEditMode()) {
            this.mCocos2dxRenderer.setScreenWidthAndHeight(pNewSurfaceWidth, pNewSurfaceHeight);
        }

    }

    public static void openIMEKeyboard() {
    }

    public static void closeIMEKeyboard() {
    }

    public void insertText(final String pText) {
        this.queueEvent(new Runnable() {
            public void run() {
                Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleInsertText(pText);
            }
        });
    }

    public void deleteBackward() {
        this.queueEvent(new Runnable() {
            public void run() {
                Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleDeleteBackward();
            }
        });
    }

    private static void dumpMotionEvent(MotionEvent event) {
        String[] names = new String[]{"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & 255;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == 5 || actionCode == 6) {
            sb.append("(pid ").append(action >> 8);
            sb.append(")");
        }

        sb.append("[");

        for (int i = 0; i < event.getPointerCount(); ++i) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount()) {
                sb.append(";");
            }
        }

        sb.append("]");
        Log.d(TAG, sb.toString());
    }

    public void doReleaseSelf() {
        mCocos2dxGLSurfaceView = null;
    }
}
