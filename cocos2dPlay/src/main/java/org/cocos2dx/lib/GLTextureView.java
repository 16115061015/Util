//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.cocos2dx.lib;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.ViewGroup.LayoutParams;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class GLTextureView extends TextureView implements SurfaceTextureListener {
    private Renderer mRenderer;
    private SurfaceTexture mSurface;
    private GLTextureView.GLThread glThread;
    private static final String TAG = "GLTextureView";
    private final WeakReference<GLTextureView> mThisWeakRef;
    private static final Object lock = new Object();
    private int contentHeight;

    public GLTextureView(Context context) {
        this(context, (AttributeSet)null);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mThisWeakRef = new WeakReference(this);
        this.setSurfaceTextureListener(this);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.setAlpha(1.0F);
        this.initHeight(height);
        this.mSurface = surface;
        this.glThread = new GLTextureView.GLThread(this.mThisWeakRef);
        this.glThread.start();
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        this.glThread.onWindowResize(width, height);
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        this.setAlpha(0.0F);
        this.glThread.finish();
        this.glThread = null;
        if (this.mSurface != null) {
            this.mSurface.release();
            this.mSurface = null;
        }

        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
    }

    public void setRenderer(Renderer renderer) {
        this.mRenderer = renderer;
    }

    public void resume() {
        if (this.glThread != null) {
            this.glThread.onPause(false);
        }

    }

    public void pause() {
        if (this.glThread != null) {
            this.glThread.onPause(true);
        }

    }

    public void queueEvent(Runnable r) {
        if (this.glThread != null) {
            this.glThread.queueEvent(r);
        }

    }

    private void initHeight(int height) {
        int maxHeight = Math.max(height, this.contentHeight);
        LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = maxHeight;
        this.setLayoutParams(layoutParams);
    }

    public void changeHeight(int height) {
        if (this.contentHeight != height) {
            this.contentHeight = height;
            LayoutParams layoutParams = this.getLayoutParams();
            layoutParams.height = this.contentHeight;
            this.setLayoutParams(layoutParams);
        }
    }

    private class GLThread extends Thread {
        static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        static final int EGL_OPENGL_ES2_BIT = 4;
        private volatile boolean finished;
        private volatile boolean pause = true;
        private EGL10 egl;
        private EGLDisplay eglDisplay;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLSurface eglSurface;
        private GL gl;
        private int width = GLTextureView.this.getWidth();
        private int height = GLTextureView.this.getHeight();
        private volatile boolean sizeChanged = true;
        private List<Runnable> mEventQueue = Collections.synchronizedList(new ArrayList());
        private WeakReference<GLTextureView> mGLTextureView;
        private final Object waiting = new Object();

        GLThread(WeakReference<GLTextureView> gLTextureView) {
            this.mGLTextureView = gLTextureView;
        }

        public void run() {
            synchronized(GLTextureView.lock) {
                try {
                    this.guardedRun();
                } catch (Exception var8) {
                    Log.e("GLTextureView", "run: Exception", var8);
                } finally {
                    SpineEventManager.ins().postEvent(2);
                }

            }
        }

        private void guardedRun() {
            boolean hasInitSize = false;

            while(!hasInitSize && !this.finished) {
                this.width = GLTextureView.this.getWidth();
                this.height = GLTextureView.this.getHeight();
                if (this.width != 0 && this.height != 0) {
                    hasInitSize = true;
                }
            }

            if (!this.finished) {
                this.initGL();
                GL10 gl10 = (GL10)this.gl;
                GLTextureView viewCreate = (GLTextureView)this.mGLTextureView.get();
                if (viewCreate != null) {
                    viewCreate.mRenderer.onSurfaceCreated(gl10, this.eglConfig);
                }

                Runnable event = null;
                boolean needReRender = false;

                while(true) {
                    while(!this.finished) {
                        this.checkCurrent();
                        GLTextureView viewFrame;
                        if (this.sizeChanged) {
                            needReRender = true;
                            viewFrame = (GLTextureView)this.mGLTextureView.get();
                            if (viewFrame != null) {
                                viewFrame.mRenderer.onSurfaceChanged(gl10, this.width, this.height);
                            }

                            this.sizeChanged = false;
                        }

                        if (!this.mEventQueue.isEmpty()) {
                            event = (Runnable)this.mEventQueue.remove(0);
                        }

                        if (event != null) {
                            event.run();
                            event = null;
                        } else if (!needReRender && this.pause) {
                            synchronized(this.waiting) {
                                try {
                                    this.waiting.wait();
                                } catch (InterruptedException var9) {
                                    Log.e("GLTextureView", "wait: Exception", var9);
                                }
                            }
                        } else {
                            viewFrame = (GLTextureView)this.mGLTextureView.get();
                            if (viewFrame != null) {
                                viewFrame.mRenderer.onDrawFrame(gl10);
                            }

                            if (!this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface)) {
                            }

                            needReRender = false;
                        }
                    }
                    this.finishGL();
                    return;
                }
            }
        }

        public void queueEvent(Runnable r) {
            if (r == null) {
                throw new IllegalArgumentException("r must not be null");
            } else {
                this.mEventQueue.add(r);
                synchronized(this.waiting) {
                    this.waiting.notifyAll();
                }
            }
        }

        void onPause(boolean pause) {
            this.pause = pause;
            synchronized(this.waiting) {
                this.waiting.notifyAll();
            }
        }

        void finish() {
            this.finished = true;
            synchronized(this.waiting) {
                this.waiting.notifyAll();
            }
        }

        public synchronized void onWindowResize(int w, int h) {
            this.width = w;
            this.height = h;
            this.sizeChanged = true;
            synchronized(this.waiting) {
                this.waiting.notifyAll();
            }
        }

        private void destroySurface() {
            if (this.eglSurface != null && this.eglSurface != EGL10.EGL_NO_SURFACE) {
                this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }

        }

        public boolean createSurface() {
            if (this.egl == null) {
                throw new RuntimeException("egl not initialized");
            } else if (this.eglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            } else if (this.eglConfig == null) {
                throw new RuntimeException("eglConfig not initialized");
            } else {
                this.destroySurface();
                GLTextureView textureView = (GLTextureView)this.mGLTextureView.get();
                if (textureView != null) {
                    try {
                        this.eglSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, textureView.mSurface, (int[])null);
                    } catch (IllegalArgumentException var3) {
                        Log.e("GLTextureView", "eglCreateWindowSurface", var3);
                        return false;
                    }
                }

                if (this.eglSurface != null && this.eglSurface != EGL10.EGL_NO_SURFACE) {
                    if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                        Log.e("GLTextureView", "eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl.eglGetError()));
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    int error = this.egl.eglGetError();
                    if (error == 12299) {
                        Log.e("GLTextureView", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                    }

                    return false;
                }
            }
        }

        private void checkCurrent() {
            if (!this.eglContext.equals(this.egl.eglGetCurrentContext()) || !this.eglSurface.equals(this.egl.eglGetCurrentSurface(12377))) {
                this.checkEglError();
                if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                    throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl.eglGetError()));
                }

                this.checkEglError();
            }

        }

        private void checkEglError() {
            int error = this.egl.eglGetError();
            if (error != 12288) {
                Log.e("PanTextureView", "EGL error = 0x" + Integer.toHexString(error));
            }

        }

        private void finishGL() {
            this.destroySurface();
            this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
            this.eglContext = null;
            this.egl.eglTerminate(this.eglDisplay);
            this.eglDisplay = null;
        }

        private void initGL() {
            this.egl = (EGL10)EGLContext.getEGL();
            this.eglDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl.eglGetError()));
            } else {
                int[] version = new int[2];
                if (!this.egl.eglInitialize(this.eglDisplay, version)) {
                    throw new RuntimeException("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl.eglGetError()));
                } else {
                    this.eglConfig = this.chooseEglConfig();
                    if (this.eglConfig == null) {
                        throw new RuntimeException("eglConfig not initialized");
                    } else {
                        this.eglContext = this.createContext(this.egl, this.eglDisplay, this.eglConfig);
                        this.createSurface();
                        if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                            throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl.eglGetError()));
                        } else {
                            this.gl = this.eglContext.getGL();
                        }
                    }
                }
            }
        }

        EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
            int[] attrib_list = new int[]{12440, 2, 12344};
            return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
        }

        private EGLConfig chooseEglConfig() {
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            int[] configSpec = this.getConfig();
            if (!this.egl.eglChooseConfig(this.eglDisplay, configSpec, configs, 1, configsCount)) {
                throw new IllegalArgumentException("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl.eglGetError()));
            } else {
                return configsCount[0] > 0 ? configs[0] : null;
            }
        }

        private int[] getConfig() {
            return new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344};
        }
    }
}
