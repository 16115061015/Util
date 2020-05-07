package com.sgxy.hzy.photoselector.build

import android.app.Activity
import android.util.Log
import com.sgxy.hzy.photoselector.util.PhotoSelectorUtil

/**
 * User: hzy
 * Date: 2020/5/6
 * Time: 12:20 PM
 * Description:
 */
class PhotoSelectBuild(context: Activity) {
    private var config: SelectConfig = SelectConfig()
    private var context: Activity? = context

    fun showGif(show: Boolean): PhotoSelectBuild {
        config.showGif = show
        return this
    }

    fun showVideo(show: Boolean): PhotoSelectBuild {
        config.showVideo = show
        return this
    }

    fun maxSelect(num: Int): PhotoSelectBuild {
        config.maxSelect = num
        return this
    }

    fun columnsNum(num: Int): PhotoSelectBuild {
        config.columnsNum = num
        return this
    }

    fun isCrop(crop: Boolean): PhotoSelectBuild {
        config.isCrop = crop
        return this
    }


    fun setCropScale(scale: Float): PhotoSelectBuild {
        config.cropScale = scale
        return this
    }

    fun setCallBack(callback: PhotoSelectorUtil.SelectedPhotoCallback): PhotoSelectBuild {
        config.selectedPhotoCallback = callback
        return this
    }

    fun build() {
        if (context == null) {
            Log.i("PhotoSelectBuild", "context is null")
            return
        }
        if (config.maxSelect == 1) {
            PhotoSelectorUtil.selectSinglePhoto(context, config.isCrop, config.cropScale, config.columnsNum, config.showGif, config.selectedPhotoCallback)
        } else {
            PhotoSelectorUtil.selectMultiplePhoto(context, config.columnsNum, config.maxSelect, config.showGif, config.selectedPhotoCallback)
        }
    }
}