package com.zjh.gamelibgdx

import android.app.Activity
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.os.Build.VERSION
import android.os.Debug
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.badlogic.gdx.*
import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.backends.android.*
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.hzy.libgdxspine.MApplication
import com.hzy.libgdxspine.getScreenHeight
import com.hzy.libgdxspine.getScreenWidth

/**
 * User: hzy
 * Date: 2020/10/6
 * Time: 10:31 PM
 * Description: 集成libdgx渲染接口
 */
open class LibgdxView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), AndroidApplicationBase {
    //渲染引擎
    private var graphics: AndroidGraphics? = null

    //Android 平台相关操作工具
    private var input: AndroidInput? = null
    private var files: AndroidFiles? = null
    private var audio: AndroidAudio? = null
    private var net: AndroidNet? = null
    private var clipboard: AndroidClipboard? = null


    private var handler: Handler? = null

    //首次初始化
    private var firstResume = true

    //运行线程
    private val runnables = Array<Runnable>()
    private val executedRunnables = Array<Runnable>()

    private var listener: ApplicationListener? = null
    private val lifecycleListeners: SnapshotArray<LifecycleListener> = SnapshotArray<LifecycleListener>(LifecycleListener::class.java)

    //日志相关设置
    private var applicationLogger: ApplicationLogger? = null
    private var logLevel = 2

    private var hasDestroy = false

    override fun getFiles(): Files? {
        return files
    }



    fun initializeForView(listener: ApplicationListener?, config: AndroidApplicationConfiguration?): View? {
        this.init(listener, config, true)
        //修改层级
        if (graphics?.view is SurfaceView) {
            (graphics?.view as SurfaceView).holder.setFormat(PixelFormat.TRANSPARENT)
            (graphics?.view as SurfaceView).setZOrderOnTop(true)
        }else if (graphics?.view is TextureView) {
            (graphics?.view as TextureView).isOpaque = false
        }
        return graphics?.view
    }


    private fun init(listener: ApplicationListener?, config: AndroidApplicationConfiguration?, isForView: Boolean) {
        if (this.version < 9) {
            throw GdxRuntimeException("LibGDX requires Android API Level 9 or later.")
        } else {
            setApplicationLogger(AndroidApplicationLogger())
            this.listener = listener

            graphics = AndroidGraphics(this, config, (if (config?.resolutionStrategy == null) FillResolutionStrategy() else config.resolutionStrategy) as ResolutionStrategy)
            handler = Handler()

            input = AndroidInputFactory.newAndroidInput(this, context.applicationContext, graphics?.view, config)
            audio = AndroidAudio(context.applicationContext, config)
            net = AndroidNet(this)
            clipboard = AndroidClipboard(context.applicationContext)
            files = AndroidFiles(this.resources.assets, context.filesDir.absolutePath)

            //初始化工具路径
            Gdx.app = this
            Gdx.input = getInput()
            Gdx.audio = this.audio
            Gdx.files = getFiles()
            Gdx.graphics = getGraphics()
            Gdx.net = getNet()

            //音效
            addLifecycleListener(object : LifecycleListener {
                override fun resume() {
                }

                override fun pause() {
                    invokeMethod(audio, "pause")
                }

                override fun dispose() {
                    audio?.dispose()
                }
            })
        }
    }


    open fun onPause() {
        val isContinuous = graphics!!.isContinuousRendering
        val isContinuousEnforced = getOrSetEnforceContinuousRenderingField()
        getOrSetEnforceContinuousRenderingField(true)
        graphics?.isContinuousRendering = true
        invokeMethod(graphics, "pause")
        input?.onPause()
        if ((context is Activity) && (context as Activity).isFinishing) {
            onDestroy()
            hasDestroy = true
        }
        getOrSetEnforceContinuousRenderingField(isContinuousEnforced)
        graphics?.isContinuousRendering = isContinuous
        graphics?.onPauseGLSurfaceView()
    }

    fun onResume() {
        Gdx.app = this
        Gdx.input = getInput()
        Gdx.audio = getAudio()
        Gdx.files = getFiles()
        Gdx.graphics = getGraphics()
        Gdx.net = getNet()
        input?.onResume()
        graphics?.onResumeGLSurfaceView()
        if (!firstResume) {
            invokeMethod(graphics, "resume")
        } else {
            firstResume = false
        }
    }


    fun onDestroy() {
        if (hasDestroy) return

        graphics?.clearManagedCaches()
        invokeMethod(graphics, "destroy")

        getHandler()?.removeCallbacksAndMessages(null)
        lifecycleListeners.clear()

        clipboard = null
        net = null
        input = null
        audio = null
        graphics = null
        files = null

        hasDestroy = true
    }

    private fun getOrSetEnforceContinuousRenderingField(enforce: Boolean? = null): Boolean {
        if (graphics == null) return false
        return try {
            val cl = graphics!!::class.java
            val field = cl.getDeclaredField("enforceContinuousRendering")
            field.isAccessible = true
            enforce?.let {
                field.setBoolean(graphics, enforce)
            }
            field.getBoolean(graphics)
        } catch (e: Exception) {
            Log.e("LibgdxView", "invoke field enforceContinuousRendering  ${e.message}")
            false
        }
    }


    private inline fun <reified T> invokeMethod(obj: T, methodName: String) {
        try {
            val method = T::class.java.getDeclaredMethod(methodName)
            method.isAccessible = true
            method.invoke(obj)
        } catch (e: Exception) {
            Log.e("LibgdxView", "invoke ${T::class.java.simpleName} $methodName ${e.message}")
        }
    }


    protected fun createLayoutParams(): LayoutParams? {
        val layoutParams = LayoutParams(-1, -1)
        layoutParams.gravity = Gravity.CENTER
        return layoutParams
    }


    override fun getClipboard(): Clipboard? {
        return clipboard
    }

    override fun setApplicationLogger(applicationLogger: ApplicationLogger?) {
        this.applicationLogger = applicationLogger
    }

    override fun setLogLevel(logLevel: Int) {
        this.logLevel = logLevel
    }

    override fun getRunnables(): Array<Runnable> {
        return runnables
    }

    override fun getApplicationWindow(): Window? {
        return (this.context as AppCompatActivity).window
    }

    override fun getApplicationListener(): ApplicationListener? {
        return this.listener
    }

    override fun removeLifecycleListener(listener: LifecycleListener?) {
        synchronized(lifecycleListeners) { lifecycleListeners.removeValue(listener, true) }
    }

    override fun getPreferences(name: String?): Preferences? {
        return AndroidPreferences(this.context.getSharedPreferences(name, 0))
    }

    override fun addLifecycleListener(listener: LifecycleListener?) {
        synchronized(lifecycleListeners) { lifecycleListeners.add(listener) }
    }

    override fun getWindowManager(): WindowManager? {
        return this.context.applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun log(tag: String, message: String) {
        if (logLevel <= 2) return
        applicationLogger?.debug(tag, message)
    }

    override fun log(tag: String, message: String, exception: Throwable) {
        if (logLevel <= 2) return
        applicationLogger?.debug(tag, message, exception)
    }

    override fun getHandler(): Handler? {
        return handler
    }

    override fun getVersion(): Int {
        return VERSION.SDK_INT
    }

    override fun postRunnable(runable: Runnable?) {
        synchronized(runnables) {
            runnables.add(runable)
            Gdx.graphics.requestRendering()
        }
    }

    override fun startActivity(intent: Intent?) {

    }

    override fun getGraphics(): Graphics? {
        return graphics
    }

    override fun getAudio(): Audio? {
        return audio
    }

    override fun getExecutedRunnables(): Array<Runnable> {
        return executedRunnables
    }

    override fun getApplicationLogger(): ApplicationLogger? {
        return applicationLogger
    }

    override fun exit() {

    }

    override fun getType(): Application.ApplicationType {
        return ApplicationType.Android
    }

    override fun getInput(): AndroidInput? {
        return input
    }

    override fun getNativeHeap(): Long {
        return Debug.getNativeHeapAllocatedSize()
    }

    override fun error(tag: String?, message: String?) {
        if (logLevel >= 1) {
            getApplicationLogger()?.error(tag, message)
        }
    }

    override fun error(tag: String?, message: String?, throwable: Throwable?) {
        if (logLevel >= 1) {
            getApplicationLogger()?.error(tag, message, throwable)
        }
    }

    override fun getLogLevel(): Int {
        return logLevel
    }

    override fun getLifecycleListeners(): SnapshotArray<LifecycleListener> {
        return lifecycleListeners
    }


    override fun debug(tag: String?, message: String?) {
        if (this.logLevel >= 3) {
            this.getApplicationLogger()?.debug(tag, message)
        }

    }


    override fun debug(tag: String?, message: String?, exception: Throwable?) {
        if (this.logLevel >= 3) {
            this.getApplicationLogger()?.debug(tag, message, exception);
        }
    }

    override fun runOnUiThread(runable: Runnable?) {
        post(runable)
    }

    override fun getNet(): Net? {
        return net
    }

    override fun getJavaHeap(): Long {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }

    override fun useImmersiveMode(p0: Boolean) {
        //沉浸模式
    }


    companion object {
        init {
            GdxNativesLoader.load()
        }
    }
}