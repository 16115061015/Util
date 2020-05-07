package com.sgxy.hzy.photoselector.album;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.util.ImageUtil;

import java.util.List;


public class PhotoAlbumChooseDialog extends Dialog {
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private List<PhotoAlbum> dataList;

    private OnPhotoAlbumChosenListener mOnPhotoAlbumChosenListener;//相册选择监听

    public PhotoAlbumChooseDialog(Activity context) {
        super(context, R.style.BottomShowDialog);

        setContentView(R.layout.layout_choose_photo_album);

        WindowManager.LayoutParams attributes;
        Window window = getWindow();
        if (window != null) {
            attributes = window.getAttributes();
            if (attributes != null) {
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
                attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            window.setGravity(Gravity.BOTTOM);
        }
        setCanceledOnTouchOutside(true);
        dataList = PhotoAlbumManager.ins().getPhotoAlbumList();
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        albumAdapter = new AlbumAdapter();
        recyclerView.setAdapter(albumAdapter);
    }

    public void setOnPhotoAlbumChosenListener(OnPhotoAlbumChosenListener onPhotoAlbumChosenListener){
        this.mOnPhotoAlbumChosenListener = onPhotoAlbumChosenListener;
    }
    public interface OnPhotoAlbumChosenListener{
        void onPhotoAlbumChosen(PhotoAlbum album);
    }

    public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder>{

        @NonNull
        @Override
        public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_album_item, parent, false);
            return new AlbumViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlbumViewHolder holder, final int position) {
            holder.onBindViewHolder(dataList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView img_album;//相册缩略图
        private TextView txt_album_name;//相册名称
        private TextView txt_album_size;//相册大小
        private ImageView img_chosen;//是否被选中
        public AlbumViewHolder(View itemView) {
            super(itemView);
            img_album=(SimpleDraweeView) itemView.findViewById(R.id.img_album);//相册缩略图
            txt_album_name=(TextView) itemView.findViewById(R.id.txt_album_name);//相册名称
            txt_album_size=(TextView) itemView.findViewById(R.id.txt_album_size);//相册大小
            img_chosen=(ImageView) itemView.findViewById(R.id.img_chosen);//是否被选中
        }

        public void onBindViewHolder(final PhotoAlbum album, final int position){
            ImageUtil.updateImageFromLocal(img_album, ImageUtil.FrescoLocalFileURI + album.path, ImageRequest.CacheChoice.DEFAULT);
            txt_album_name.setText(album.name);
            txt_album_size.setText(album.count + "张");
            img_chosen.setVisibility(album.isChosen ? View.VISIBLE : View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChosenPosition(dataList, position);
                    if(null!=mOnPhotoAlbumChosenListener){
                        mOnPhotoAlbumChosenListener.onPhotoAlbumChosen(album);
                    }
                    dismiss();
                }
            });

        }


    }

    /**
     * 设置选中位置
     */
    private void setChosenPosition(List<PhotoAlbum> photoAlbumList, int position){
        PhotoAlbum album = null;
        for (int i = 0; i < photoAlbumList.size(); i++) {
            album = photoAlbumList.get(i);
            if(i == position){
                album.isChosen=true;
            }else{
                album.isChosen=false;
            }
        }
    }


}
