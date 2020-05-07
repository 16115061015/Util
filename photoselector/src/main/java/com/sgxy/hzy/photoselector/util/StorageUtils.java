package com.sgxy.hzy.photoselector.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class StorageUtils {

    public static String getCropDataCachePath(Context context) {
        return getPackageDataPath(context)+ File.separator + "crop" + File.separator;
    }

    public static String getPackageDataPath(Context context) {
        return getSDCardPath(context)+ File.separator + context.getApplicationInfo().packageName + File.separator;
    }

    private static String getSDCardPath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return context.getFilesDir().getAbsolutePath();
        }
    }
}
