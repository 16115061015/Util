//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.cocos2dx.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener;

public abstract class Cocos2dxView implements Cocos2dxHelperListener {
    private Cocos2dxGLSurfaceView mGLSurfaceView = null;
    private Context mContext;
    protected FrameLayout mViewGroup;
    private int contentHeight;

    public Cocos2dxView(Context context, FrameLayout viewGroup, int contentHeight) {
        this.mContext = context;
        this.mViewGroup = viewGroup;
        this.contentHeight = contentHeight;
        this.onCreate();
    }

    protected void resume() {
        if (this.mGLSurfaceView != null) {
            this.mGLSurfaceView.resume();
        }

    }

    protected void pause() {
        if (this.mGLSurfaceView != null) {
            this.mGLSurfaceView.pause();
        }

    }

    public void doExitView() {
        Cocos2dxHelper.uninit(this);
        if (this.mGLSurfaceView != null) {
            this.mGLSurfaceView.doReleaseSelf();
            this.mGLSurfaceView = null;
        }

        this.mViewGroup = null;
        this.mContext = null;
    }

    private void onCreate() {
        Cocos2dxHelper.init(this.mContext, this);

        try {
            this.init();
        } catch (Exception var2) {
            Log.e("Cocos2dxView", "init: error", var2);
        }

    }

    public void showDialog(String pTitle, String pMessage) {
    }

    @TargetApi(3)
    public void runOnGLThread(Runnable pRunnable) {
        if (this.mGLSurfaceView != null) {
            this.mGLSurfaceView.queueEvent(pRunnable);
        }
    }

    private void init() {
        LayoutParams framelayout_params = new LayoutParams(-1, -1);
        this.mGLSurfaceView = this.onCreateView();
        this.mGLSurfaceView.setOpaque(false);
        this.mGLSurfaceView.setCocos2dxRenderer(new Cocos2dxRenderer());
        this.mViewGroup.addView(this.mGLSurfaceView, framelayout_params);
    }

    private Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this.mContext);
        glSurfaceView.setContentHeight(this.contentHeight);
        return glSurfaceView;
    }

    protected void changeHeight(int height) {
        this.contentHeight = height;
        if (this.mGLSurfaceView != null) {
            this.mGLSurfaceView.changeHeight(height);
        }

    }

    static {
        try {
            System.loadLibrary("cocos2dx");
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }
}
