package com.sgxy.hzy.photoselector.recyclerview.single;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgxy.hzy.photoselector.album.PhotoAlbum;
import com.sgxy.hzy.photoselector.album.PhotoInfo;

import java.util.List;

public class SinglePhotoAdapter extends RecyclerView.Adapter<SinglePhotoViewHolder> implements OnSingleSelectListener {

    private List<PhotoInfo> dataList;
    private int imageWidth;
    private OnSingleSelectListener listener;

    public SinglePhotoAdapter(List<PhotoInfo> dataList, int imageWidth, OnSingleSelectListener listener) {
        this.dataList = dataList;
        this.imageWidth = imageWidth;
        this.listener = listener;
    }

    public void setData(PhotoAlbum album) {
        this.dataList = album.photoList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @NonNull
    @Override
    public SinglePhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SinglePhotoViewHolder.create(parent,imageWidth);
    }

    @Override
    public void onBindViewHolder(@NonNull SinglePhotoViewHolder holder, int position) {
        holder.onBindViewHolder(dataList.get(position), position, this);
    }

    @Override
    public void onItemClick(int position, PhotoInfo data) {
        if(listener != null){
            listener.onItemClick(position, data);
        }
    }

}
