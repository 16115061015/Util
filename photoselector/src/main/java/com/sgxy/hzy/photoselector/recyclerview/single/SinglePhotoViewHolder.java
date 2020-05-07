package com.sgxy.hzy.photoselector.recyclerview.single;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.album.PhotoInfo;
import com.sgxy.hzy.photoselector.util.ImageUtil;

import static com.sgxy.hzy.photoselector.album.ScannerTypeKt.IMAGE_GIF;

public class SinglePhotoViewHolder extends RecyclerView.ViewHolder {

    public static SinglePhotoViewHolder create(ViewGroup parent, int imageWidth) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_single_photo_item, parent, false);
        return new SinglePhotoViewHolder(view, imageWidth);
    }

    private SimpleDraweeView image;
    private TextView tvGif;
    private int imageWidth;

    public SinglePhotoViewHolder(View itemView, int imageWidth) {
        super(itemView);
        this.imageWidth = imageWidth;
        image = (SimpleDraweeView) itemView.findViewById(R.id.image);
        tvGif = itemView.findViewById(R.id.tvGif);
    }

    public void onBindViewHolder(final PhotoInfo data, final int position, final OnSingleSelectListener callback) {
        ImageUtil.updateCompressImageFromLocal(image, ImageUtil.FrescoLocalFileURI + data.path,
                imageWidth, imageWidth, ImageRequest.CacheChoice.SMALL);
        tvGif.setVisibility(data.type.equals(IMAGE_GIF) ? View.VISIBLE : View.GONE);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(position, data);
            }
        });

    }

}
