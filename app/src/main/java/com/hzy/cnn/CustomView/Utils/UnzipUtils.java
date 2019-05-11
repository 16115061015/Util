package com.hzy.cnn.CustomView.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.hzy.cnn.waveview.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 胡泽宇 on 2018/8/11.
 */

public class UnzipUtils {
    //解压RAW文件方法

    /***
     *
     * @param context
     * @param ResoureId 需要解压的文件ID
      * @return
     */
    public static String unzipRAWFile(Context context,int ResoureId) {

        String apkFilePath;
        Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(ResoureId);

        File externalCacheDir = context.getExternalCacheDir();

        File file = new File(externalCacheDir, resources.getResourceEntryName(ResoureId) + ".dex");

        apkFilePath = file.getAbsolutePath();
        if (!file.exists()) {
            BufferedOutputStream bufferedOutputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            byte[] buffer = new byte[4 * 1024];
            int size;
            try {
                while ((size = inputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, size);
                    bufferedOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inputStream != null)
                    inputStream.close();
                if (bufferedOutputStream != null)
                    bufferedOutputStream.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("", "文件解压完毕，路径地址为：" + apkFilePath);
        } else {
            Log.i("", "文件已存在，无需解压"+apkFilePath);
        }

        return apkFilePath;
    }
}
