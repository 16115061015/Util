package com.sgxy.hzy.photoselector.recyclerview.multiple;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.album.PhotoInfo;
import com.sgxy.hzy.photoselector.util.ImageUtil;

import java.util.ArrayList;

import static com.sgxy.hzy.photoselector.album.ScannerTypeKt.IMAGE_GIF;

public class MultiplePhotoViewHolder extends RecyclerView.ViewHolder {

    public static MultiplePhotoViewHolder create(ViewGroup parent, int imageWidth) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_multiple_photo_item, parent, false);
        return new MultiplePhotoViewHolder(view, imageWidth);
    }
    private SimpleDraweeView image;
    private FrameLayout selectFrame;
    private TextView tvSelect;
    private View cover;
    private int imageWidth;
    private TextView tvGif;

    public MultiplePhotoViewHolder(View itemView, int imageWidth) {
        super(itemView);
        this.imageWidth = imageWidth;
        image = (SimpleDraweeView) itemView.findViewById(R.id.image);
        cover = itemView.findViewById(R.id.cover);
        selectFrame = (FrameLayout) itemView.findViewById(R.id.select_frame);
        tvSelect = (TextView) itemView.findViewById(R.id.tv_select);
        tvGif = itemView.findViewById(R.id.tvGif);
    }

    public void onBindViewHolder(final PhotoInfo data, final int position, final ArrayList<PhotoInfo> selectDataList, final int limit, final OnMultipleSelectListener callback){
        ImageUtil.updateCompressImageFromLocal(image, ImageUtil.FrescoLocalFileURI + data.path,
                imageWidth, imageWidth, ImageRequest.CacheChoice.SMALL);
        final int selectedSize = selectDataList.size();
        tvGif.setVisibility(data.type.equals(IMAGE_GIF) ? View.VISIBLE : View.GONE);
        if(!data.isSelected && (selectedSize == limit)){
            cover.setVisibility(View.VISIBLE);
        }else{
            cover.setVisibility(View.GONE);
        }

        if (data.isSelected) {
            tvSelect.setSelected(true);
            int indexOf = selectDataList.indexOf(data);
            tvSelect.setText(String.valueOf(indexOf + 1));
        } else {
            tvSelect.setSelected(false);
            tvSelect.setText("");
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(position, data);
            }
        });
        selectFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSelectClick(position, selectedSize, limit);
            }
        });

    }

}
