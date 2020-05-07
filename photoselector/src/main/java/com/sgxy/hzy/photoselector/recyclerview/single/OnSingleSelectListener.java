package com.sgxy.hzy.photoselector.recyclerview.single;

import com.sgxy.hzy.photoselector.album.PhotoInfo;

public interface OnSingleSelectListener {
    void onItemClick(int position, PhotoInfo data);
}
