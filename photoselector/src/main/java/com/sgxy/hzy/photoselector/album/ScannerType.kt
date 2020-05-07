package com.sgxy.hzy.photoselector.album

import androidx.annotation.StringDef

/**
 * User: hzy
 * Date: 2020/5/6
 * Time: 2:24 PM
 * Description: 扫描类型
 */
const val IMAGE_PNG = "image/png"
const val IMAGE_JPEG = "image/jpeg"
const val IMAGE_JPG = "image/jpeg"
const val IMAGE_GIF = "image/gif"

@StringDef(IMAGE_PNG, IMAGE_JPEG, IMAGE_JPG, IMAGE_GIF)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class ScannerType