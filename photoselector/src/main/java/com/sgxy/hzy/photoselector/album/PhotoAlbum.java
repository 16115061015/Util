package com.sgxy.hzy.photoselector.album;

import java.io.Serializable;
import java.util.ArrayList;

public class PhotoAlbum implements Serializable {
    public String name;//相册名称
    public int count;//相册照片数量
    public String path;//缩略图图片路径
    public boolean isChosen = false;//是否被选中 默认没有被选中
    public ArrayList<PhotoInfo> photoList = new ArrayList<PhotoInfo>();//相册照片列表
    public ArrayList<PhotoInfo> selectedList = new ArrayList<PhotoInfo>();

    public PhotoAlbum(){}
    public PhotoAlbum(String name){
        this.name=name;
    }
}
