//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.cocos2dx.lib;

import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cocos2dxRenderer implements Renderer {
    private static final long NANOSECONDSPERSECOND = 1000000000L;
    private static final long NANOSECONDSPERMICROSECOND = 1000000L;
    private static long sAnimationInterval = 16666666L;
    private long mLastTickInNanoSeconds;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mNativeInitCompleted = false;

    private boolean release = false;

    public Cocos2dxRenderer() {
    }

    public static void setAnimationInterval(float animationInterval) {
        sAnimationInterval = (long) (animationInterval * 1.0E9F);
    }

    public void setScreenWidthAndHeight(int surfaceWidth, int surfaceHeight) {
        this.mScreenWidth = surfaceWidth;
        this.mScreenHeight = surfaceHeight;
    }

    public void onSurfaceCreated(GL10 GL10, EGLConfig EGLConfig) {
        nativeInit(this.mScreenWidth, this.mScreenHeight);
        this.mLastTickInNanoSeconds = System.nanoTime();
        this.mNativeInitCompleted = true;
    }

    public void onSurfaceChanged(GL10 GL10, int width, int height) {
        nativeOnSurfaceChanged(width, height);
    }


    public void onDrawFrame(GL10 gl) {
        Log.i("forTest","Renderer onDrawFrame onDrawFrame");
        if ((double) sAnimationInterval <= 1.6666666666666666E7D) {
            nativeRender();
        } else {
            long now = System.nanoTime();
            long interval = now - this.mLastTickInNanoSeconds;
            if (interval < sAnimationInterval) {
                try {
                    Thread.sleep((sAnimationInterval - interval) / 1000000L);
                } catch (Exception var7) {
                }
            }

            this.mLastTickInNanoSeconds = System.nanoTime();
            nativeRender();
        }

    }


    private static native void nativeTouchesBegin(int var0, float var1, float var2);

    private static native void nativeTouchesEnd(int var0, float var1, float var2);

    private static native void nativeTouchesMove(int[] var0, float[] var1, float[] var2);

    private static native void nativeTouchesCancel(int[] var0, float[] var1, float[] var2);

    private static native boolean nativeKeyEvent(int var0, boolean var1);

    private static native void nativeRender();

    private static native void nativeInit(int var0, int var1);

    private static native void nativeOnSurfaceChanged(int var0, int var1);

    private static native void nativeOnPause();

    private static native void nativeOnResume();

    public void handleActionDown(int id, float x, float y) {
        nativeTouchesBegin(id, x, y);
    }

    public void handleActionUp(int id, float x, float y) {
        nativeTouchesEnd(id, x, y);
    }

    public void handleActionCancel(int[] ids, float[] xs, float[] ys) {
        nativeTouchesCancel(ids, xs, ys);
    }

    public void handleActionMove(int[] ids, float[] xs, float[] ys) {
        nativeTouchesMove(ids, xs, ys);
    }

    public void handleKeyDown(int keyCode) {
        nativeKeyEvent(keyCode, true);
    }

    public void handleKeyUp(int keyCode) {
        nativeKeyEvent(keyCode, false);
    }

    public void handleOnPause() {
        if (this.mNativeInitCompleted) {
            Cocos2dxHelper.onEnterBackground();
            nativeOnPause();
        }
    }

    public void handleOnResume() {
        Cocos2dxHelper.onEnterForeground();
        nativeOnResume();
    }

    private static native void nativeInsertText(String var0);

    private static native void nativeDeleteBackward();

    private static native String nativeGetContentText();

    public void handleInsertText(String text) {
        nativeInsertText(text);
    }

    public void handleDeleteBackward() {
        nativeDeleteBackward();
    }

    public String getContentText() {
        return nativeGetContentText();
    }
}
