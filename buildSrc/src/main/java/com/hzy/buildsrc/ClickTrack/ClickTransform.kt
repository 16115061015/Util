package com.hzy.buildsrc.ClickTrack

import com.android.build.api.transform.*
import com.android.utils.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream


/**
 * User: hzy
 * Date: 2020/7/19
 * Time: 3:41 PM
 * Description: 自定义注入流程
 */
class ClickTransform : Transform() {
    //Transform的名称
    override fun getName(): String {
        return this.javaClass.name
    }

    //处理文件类型--> class文件
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return mutableSetOf(QualifiedContent.DefaultContentType.CLASSES)
    }

    //是否增量更新
    // 返回true的话表示支持，这个时候可以根据 com.android.build.api.transform.TransformInput 来获得更改、移除或者添加的文件目录或者jar包。
    // 开启则记得在全量删除之前的生出文件
    override fun isIncremental(): Boolean {
        return true
    }

    //处理输入文件的范围，范围越小速度越快
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(QualifiedContent.Scope.PROJECT)
    }

    //处理过程
    override fun transform(transformInvocation: TransformInvocation) {
        //获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
        if (isIncremental)
            incrementalTransform(transformInvocation)
        else
            fullIncrementalTransform(transformInvocation)
    }

    private fun fullIncrementalTransform(transformInvocation: TransformInvocation) {
        //获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
        val outputProvider = transformInvocation?.outputProvider
        if (!isIncremental) outputProvider.deleteAll()
        //遍历输入的文件
        transformInvocation.inputs?.forEach { inputs ->
            inputs.directoryInputs.forEach { dir ->
                traverseDir(dir, outputProvider)
            }
            //处理jar文件
            inputs.jarInputs.forEach { jar ->
                traverseJar(jar, outputProvider)
            }

        }
    }

    private fun incrementalTransform(transformInvocation: TransformInvocation){
        //遍历输入的文件
        val outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs?.forEach { inputs ->
            inputs.directoryInputs.forEach { dir ->
                val dest = outputProvider?.getContentLocation(
                        dir.name,
                        dir.contentTypes,
                        dir.scopes,
                        Format.DIRECTORY)
                dir.changedFiles.forEach{ (file, status) ->
                    when (status) {
                        Status.ADDED, Status.CHANGED -> {
                            traverseDir(dir, outputProvider, false)
                        }
                        Status.REMOVED -> {
                            //删除 输出目录
                            val outputFile = File(dest, FileUtils.relativePossiblyNonExistingPath(dir.file, file))
                            FileUtils.deleteIfExists(outputFile)
                        }
                    }
                }
                copyFile(dir,outputProvider)

            }
            //处理jar文件
            inputs.jarInputs.forEach { jar ->
                traverseJar(jar, outputProvider)
            }

        }
    }

    private fun traverseJar(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        //传递给下一个Transform处理  jar修改需要先解压为zip
        val outputJar = outputProvider.getContentLocation(
                jarInput.name,
                jarInput.contentTypes,
                jarInput.scopes,
                Format.JAR)
        //复制修改文件
        outputJar?.let { jarInput.file.copyRecursively(File(it.path), true) }

    }

    private fun traverseDir(dirInput: DirectoryInput, outputProvider: TransformOutputProvider?, copy: Boolean = true) {
        if (dirInput.file.isDirectory) {
            dirInput.file.walk().forEach {
                if (it.isFile) transformFile(it, dirInput, outputProvider)
            }
        } else {
            transformFile(dirInput.file, dirInput, outputProvider)
        }
        //传递给下一个Transform处理
        //复制修改文件
        if(copy) copyFile(dirInput,outputProvider)
    }


    private fun copyFile(dirInput: DirectoryInput, outputProvider: TransformOutputProvider?) {
        val dest = outputProvider?.getContentLocation(
                dirInput.name,
                dirInput.contentTypes,
                dirInput.scopes,
                Format.DIRECTORY)
        //复制修改文件
        dest?.let { dirInput.file.copyRecursively(File(it.path), true) }
    }

    /***
     *
     */
    private fun transformFile(file: File, dirInput: DirectoryInput, outputProvider: TransformOutputProvider?) {
        //只拦截具体类名 MainAdapter
        if (file.name != "MainAdapter.class") return
        println("ClickTransform transformFile:${file.path}")
        val reader = ClassReader(file.readBytes())
        val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        val visitor = ClickVisitor(writer)
        reader.accept(visitor, ClassReader.EXPAND_FRAMES)
        //覆盖原来的文件
        val code = writer.toByteArray()
        val outputStream = FileOutputStream(file.path)

        outputStream.use {
            it.write(code)
        }
    }


}