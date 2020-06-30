package com.hzy.BVH.helper

import android.content.res.Resources

/**
 * User: hzy
 * Date: 2020/6/30
 * Time: 2:58 PM
 * Description: 全局扩展函数
 */

val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
    ).toInt()