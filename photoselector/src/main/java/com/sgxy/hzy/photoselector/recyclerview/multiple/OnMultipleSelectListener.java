package com.sgxy.hzy.photoselector.recyclerview.multiple;

import com.sgxy.hzy.photoselector.album.PhotoInfo;

public interface OnMultipleSelectListener {
    void onItemClick(int position, PhotoInfo data);
    void onSelectClick(int position, int selectCount, int limit);
    void onSelectLimit(int position, int selectCount, int limit);
}
