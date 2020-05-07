package com.sgxy.hzy.photoselector.build

import com.sgxy.hzy.photoselector.util.PhotoSelectorUtil

/**
 * User: hzy
 * Date: 2020/5/6
 * Time: 11:55 AM
 * Description: 选择配置
 */
class SelectConfig {
    //是否显示gif图
    var showGif: Boolean = false

    //是否显示视频
    var showVideo: Boolean = false

    var maxSelect: Int = Int.MAX_VALUE

    //列数
    var columnsNum: Int = 4

    //是否裁剪
    var isCrop: Boolean = false

    var cropScale:Float = 1F
    var selectedPhotoCallback: PhotoSelectorUtil.SelectedPhotoCallback? = null
}