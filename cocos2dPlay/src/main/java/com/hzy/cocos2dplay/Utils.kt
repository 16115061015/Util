package com.hzy.cocos2dplay

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * User: hzy
 * Date: 2020/10/1
 * Time: 7:24 PM
 * Description:
 */

/**
 * 解压 zip 文件
 */
fun zip2File(sZipPathFile: String, sDestPath: String, deleteZip: Boolean = false): Boolean {
    var flag = false
    try {
        FileInputStream(sZipPathFile).use { fis ->
            ZipInputStream(fis).use { zis ->
                val ch = ByteArray(2048)
                var ze: ZipEntry?
                while (zis.nextEntry.also { ze = it } != null) {
                    val zipFile = File(sDestPath + "//" + ze!!.name)
                    val filePath = File(zipFile.parentFile.path)
                    if (ze!!.isDirectory) {
                        if (!zipFile.exists()) {
                            zipFile.mkdirs()
                        }
                        zis.closeEntry()
                    } else {
                        if (!filePath.exists()) {
                            filePath.mkdirs()
                        }
                        FileOutputStream(zipFile).use { fos ->
                            var i: Int
                            val zipFilePath = zipFile.absolutePath
                            while (zis.read(ch).also { i = it } != -1) {
                                fos.write(ch, 0, i)
                            }
                            zis.closeEntry()
                            fos.flush()
                            fos.close()
                            if (zipFilePath.endsWith(".zip")) {
                                zip2File(zipFilePath, zipFilePath.substring(0, zipFilePath.lastIndexOf(".zip")))
                            }
                        }
                    }
                }
            }
            // 如果解压完后删除ZIP文件可以
            if (deleteZip) {
                val file = File(sZipPathFile)
                file.delete()
            }
        }
        flag = true
    } catch (ignore: Exception) {
        ignore.printStackTrace()
    }
    return flag
}

/***
 * 解压 zip 文件 通过流
 */
fun zip2File(stream: InputStream, sDestPath: String): Boolean {
    var flag = false
    try {
        stream.use { fis ->
            ZipInputStream(fis).use { zis ->
                val ch = ByteArray(2048)
                var ze: ZipEntry?
                while (zis.nextEntry.also { ze = it } != null) {
                    val zipFile = File(sDestPath + "//" + ze!!.name)
                    val filePath = File(zipFile.parentFile.path)
                    if (ze!!.isDirectory) {
                        if (!zipFile.exists()) {
                            zipFile.mkdirs()
                        }
                        zis.closeEntry()
                    } else {
                        if (!filePath.exists()) {
                            filePath.mkdirs()
                        }
                        FileOutputStream(zipFile).use { fos ->
                            var i: Int
                            val zipFilePath = zipFile.absolutePath
                            while (zis.read(ch).also { i = it } != -1) {
                                fos.write(ch, 0, i)
                            }
                            zis.closeEntry()
                            fos.flush()
                            fos.close()
                            if (zipFilePath.endsWith(".zip")) {
                                zip2File(zipFilePath, zipFilePath.substring(0, zipFilePath.lastIndexOf(".zip")))
                            }
                        }
                    }
                }
            }
        }
        flag = true
    } catch (ignore: Exception) {
        ignore.printStackTrace()
    }
    return flag
}


/**
 * 获取屏幕高度
 */
fun getScreenHeight(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    //        int height = wm.getDefaultDisplay().getHeight();
    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}


fun getSpinePathFromZip(resource: String, dest: String): String {
    val rs = zip2File(resource, dest, false)
    Log.i("转换结果：", "$resource $rs")
    return dest
}

fun getSpinePathFromZip(inputStream: InputStream, dest: String): String {
    val rs = zip2File(inputStream, dest)
    return if(rs) {
        getBundleFile(File(dest))?.absolutePath ?: ""
    }else{
        ""
    }
}

/**
 * 获取Spine 资源文件下的bundle文件
 */
fun getBundleFile(file: File): File? {
    return if (file.exists()) {
        val sFiles = file.listFiles()
        if (sFiles == null || sFiles.isEmpty()) {
            return null
        }
        for (sFile in sFiles) {
            if (sFile.name.endsWith("bundle")) {
                return sFile
            }
        }
        null
    } else
        null
}