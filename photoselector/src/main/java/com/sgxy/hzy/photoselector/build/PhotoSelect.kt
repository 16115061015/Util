package com.sgxy.hzy.photoselector.build

import android.app.Activity

/**
 * User: hzy
 * Date: 2020/5/6
 * Time: 1:10 PM
 * Description:
 */
class PhotoSelect {
    companion object {
        fun with(context: Activity): PhotoSelectBuild {
            return PhotoSelectBuild(context)
        }
    }
}