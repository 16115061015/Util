package com.sgxy.hzy.photoselector.album;

import java.io.Serializable;

public class PhotoInfo implements Serializable {
    public String path;//相片路径
    public String date;//文件创建时间，用于排序
    public @ScannerType
    String type;//媒体类型
    public boolean isSelected = false;

    public PhotoInfo() {
    }

    public PhotoInfo(String path, String date, String type) {
        this.path = path;
        this.date = date;
        this.type = type;
    }
}
