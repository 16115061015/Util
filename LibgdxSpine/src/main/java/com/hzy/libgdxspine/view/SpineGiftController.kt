package com.zjh.gamelibgdx

import android.util.Log
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.JsonReader
import com.esotericsoftware.spine.*
import com.hzy.libgdxspine.MApplication
import com.hzy.libgdxspine.getScreenHeight
import com.hzy.libgdxspine.getScreenWidth
import java.io.File
import kotlin.math.max
import kotlin.math.min


/**
 * Created by ZhangJinghao on 2018/4/3.
 */
class SpineGiftController : ApplicationListener, AnimationState.AnimationStateListener {
    private var render: SkeletonRenderer? = null
    private var polygonBatch: PolygonSpriteBatch? = null

    var completeListener: (() -> Unit)? = null

    var sizeChange: ((Float, Float) -> Unit)? = null

    @Volatile
    private var hasDispose = false
        @Synchronized get
        @Synchronized set

    @Volatile
    private var beginToStart = false
        @Synchronized get() {
            return if (field) {
                field = false
                true
            } else {
                false
            }
        }
        @Synchronized set

    private var spinePath = ""

    private val atlasCache = HashMap<String, Pair<TextureAtlas, SkeletonData>>()

    @Volatile
    private var isPlaying: Boolean = false
        @Synchronized get
        @Synchronized set

    private var currentState: AnimationState? = null
    private var currentSkeleton: Skeleton? = null

    override fun create() {
        render = SkeletonRenderer()
        polygonBatch = PolygonSpriteBatch()
    }

    fun playSpine(filePath: String) {
        beginToStart = true
        spinePath = filePath
    }

    private fun starPlaySpine(path: String) {
        if (spinePath.isBlank()) {
            completeListener?.invoke() //播放失败直接回调完成
            return
        }
        val dir = File(spinePath)
        if (!dir.isDirectory) {
            completeListener?.invoke()
            return
        }
        var spine: Pair<TextureAtlas, SkeletonData>? = null
        var scale = 0f
        Log.i("forTest", "屏幕 宽${getScreenWidth(MApplication.globalContext)}  高 ${getScreenHeight(MApplication.globalContext)}")
        try {
            if (atlasCache.containsKey(spinePath)) {
                spine = atlasCache[spinePath]
            } else {
                //找到atlas文件
                val atlas = dir.listFiles()?.find {
                    it.name.endsWith("atlas")
                }
                //找到json文件
                val json = dir.listFiles()?.find {
                    it.name.endsWith("json") && it.name != "config.json"
                }
                if (atlas != null && json != null) {
                    // 获取纹理集合
                    val atlas = TextureAtlas(Gdx.files.absolute(atlas.absolutePath))
                    // 读取json信息
                    val sJson = SkeletonJson(atlas)
                    val paintSize = readSkeletonSize(Gdx.files.absolute(json.absolutePath))
                    Log.i("forTest", "缩放前的宽 ${paintSize.first}  缩放后的高 ${paintSize.second}")
                    sJson.scale = (max(getScreenWidth(MApplication.globalContext) / paintSize.first, getScreenHeight(MApplication.globalContext) / paintSize.second))
                    scale = sJson.scale
                    val sData = sJson.readSkeletonData(Gdx.files.absolute(json.absolutePath))
                    Log.i("forTest", "缩放后的宽 ${sData.width * sJson.scale}  缩放后的高 ${sData.height * sJson.scale}")
                    spine = Pair(atlas, sData)
                    // 初始化动画信息
                    atlasCache[spinePath] = spine
                }
            }
            if (spine == null) {
                completeListener?.invoke()
                return
            }
            currentState?.removeListener(this)
            // 初始化动画信息
            val animData = AnimationStateData(spine.second)
            currentState = AnimationState(animData)
            // 初始化骨骼信息
            currentSkeleton = Skeleton(spine.second)
            // 播放动画
            currentState?.setAnimation(0, "animation", false)
            // 设置位置
            currentSkeleton?.setPosition(
                    getScreenWidth(MApplication.globalContext) / 2.toFloat(),
                    0f)
            currentState?.addListener(this)
            isPlaying = true
        } catch (e: Exception) {
            completeListener?.invoke()
        }
    }


    /***
     * 只读骨架信息
     */
    private fun readSkeletonSize(file: FileHandle?): android.util.Pair<Float, Float> {
        var result = android.util.Pair(1f, 1f)
        requireNotNull(file) { "file cannot be null." }
        val root = JsonReader().parse(file)
        val skeletonMap = root["skeleton"]
        if (skeletonMap != null) {
            result = android.util.Pair(skeletonMap.getFloat("width", 1f), skeletonMap.getFloat("height", 1f))
        }
        return result
    }

    override fun render() {
        if (beginToStart) starPlaySpine(spinePath)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        if (hasDispose || !isPlaying) return
        // 动画控制器更新时间步
        currentState?.update(Gdx.graphics.deltaTime)
        // 动画控制器控制骨骼动画
        currentState?.apply(currentSkeleton)
        // 骨骼逐级进行矩阵变换
        currentSkeleton?.updateWorldTransform()
        // 绘制
        polygonBatch?.begin()
        currentSkeleton?.let {
            render?.draw(polygonBatch, currentSkeleton)
        }
        polygonBatch?.end()
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {}
    override fun resume() {}

    override fun dispose() {
        atlasCache.forEach { (_, cache) ->
            cache.first.dispose()
        }
        atlasCache.clear()
        hasDispose = true
    }

    override fun event(entry: AnimationState.TrackEntry?, event: Event) {
        Log.i("event", event.data.name)
    }

    override fun start(entry: AnimationState.TrackEntry?) {
        isPlaying = true
    }

    override fun interrupt(entry: AnimationState.TrackEntry?) {
        isPlaying = false
    }


    override fun complete(entry: AnimationState.TrackEntry?) {
        isPlaying = false
        completeListener?.invoke()
    }

    override fun end(entry: AnimationState.TrackEntry?) {
        isPlaying = false
    }

    override fun dispose(entry: AnimationState.TrackEntry?) {
        isPlaying = false
    }


}