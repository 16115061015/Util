package com.sgxy.hzy.photoselector.recyclerview.multiple;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgxy.hzy.photoselector.album.PhotoAlbum;
import com.sgxy.hzy.photoselector.album.PhotoInfo;

import java.util.ArrayList;
import java.util.List;

public class MultiplePhotoAdapter extends RecyclerView.Adapter<MultiplePhotoViewHolder> implements OnMultipleSelectListener {

    private List<PhotoInfo> dataList;
    private ArrayList<PhotoInfo> selectDataList = new ArrayList<>();
    private int imageWidth;
    public int mLimit;
    private OnMultipleSelectListener listener;

    public MultiplePhotoAdapter(List<PhotoInfo> dataList, int limit, int imageWidth, OnMultipleSelectListener listener) {
        this.dataList = dataList;
        this.imageWidth = imageWidth;
        this.listener = listener;
        this.mLimit = limit;
    }

    public List<PhotoInfo> getDataList() {
        return dataList;
    }

    public void setDataWithReset(PhotoAlbum album, boolean isReset) {
        this.dataList = album.photoList;
        this.selectDataList = album.selectedList;
        if(isReset){
            for (PhotoInfo info:dataList) {
                info.isSelected = false;
            }
            this.selectDataList.clear();
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @NonNull
    @Override
    public MultiplePhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MultiplePhotoViewHolder.create(parent,imageWidth);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiplePhotoViewHolder holder, int position) {
        holder.onBindViewHolder(dataList.get(position), position, selectDataList, this.mLimit, this);
    }

    @Override
    public void onItemClick(int position, PhotoInfo data) {
        if(listener != null){
            listener.onItemClick(position, data);
        }
    }

    @Override
    public void onSelectClick(int position, int arg1, int arg2) {
        int preSelectedSize = selectDataList.size();
        PhotoInfo data = dataList.get(position);
        int preIndex = selectDataList.indexOf(data);
        if(data.isSelected){//取消选择
            selectDataList.remove(data);
            data.isSelected = false;
            if(preIndex != preSelectedSize -1 || preSelectedSize == mLimit){
                notifyDataSetChanged();
            }else{
                notifyItemChanged(position);
            }
        }else if(selectDataList.size() < mLimit){//可选择
            selectDataList.add(data);
            data.isSelected = true;
            if(selectDataList.size() == mLimit){
                notifyDataSetChanged();
            }else{
                notifyItemChanged(position);
            }
        }else{//达到上限
            if(listener != null){
                listener.onSelectLimit(position, selectDataList.size(), this.mLimit);
            }
        }

        if(listener != null){
            listener.onSelectClick(position, selectDataList.size(), this.mLimit);
        }

    }

    @Override
    public void onSelectLimit(int position, int selectCount, int limit) {}

    public ArrayList<PhotoInfo> getSelectDataList() {
        return selectDataList;
    }

}
