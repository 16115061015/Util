package com.hzy.cnn.CustomView.Utils;

import android.content.Context;
import android.os.Environment;

/**
 * Created by 胡泽宇 on 2018/8/11.
 * 获取外部缓存是否可用
 */

public class UtilsToGetCachePath {
    public static String getCachePath( Context context ){
        String cachePath ;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath() ;
        }else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath() ;
        }
        return cachePath ;
    }
}
