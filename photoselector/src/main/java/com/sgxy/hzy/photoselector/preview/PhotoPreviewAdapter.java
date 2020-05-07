package com.sgxy.hzy.photoselector.preview;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.util.ImageUtil;
import com.sgxy.hzy.photoselector.widget.photodraweeview.OnViewTapListener;
import com.sgxy.hzy.photoselector.widget.photodraweeview.PhotoDraweeView;

import java.util.List;

public class PhotoPreviewAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mData;
    private OnItemClickListener mListener;
    private int screenWidth;
    private int screenHeight;
    private float screenRatio;

    public PhotoPreviewAdapter(Context context, List<String> mData) {
        this.mContext = context;
        this.mData = mData;
        screenWidth = ImageUtil.getScreenWidth(mContext);
        screenHeight = ImageUtil.getScreenHeight(mContext);

        screenRatio = screenWidth * 1.0f / screenHeight;
    }

    public List<String> getData() {
        return mData;
    }

    public void setData(List<String> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public void deleteItem(int index) {
        mData.remove(index);
        notifyDataSetChanged();
    }

    public void updateItem(int index, String path) {
        mData.set(index, path);
        notifyDataSetChanged();
    }


    public void setListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.layout_photo_preview_item, container, false);
        final PhotoDraweeView photoDraweeView = view.findViewById(R.id.photo_view);
//        final PhotoView photoView = view.findViewById(R.id.photo_pager_item_iv);
        String url = mData.get(position);
        ImageUtil.updateImageFromNetwork(photoDraweeView, url, screenWidth, screenHeight,
                ImageRequest.CacheChoice.DEFAULT, new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    return;
                }
                if (imageInfo.getWidth() < screenWidth) {
                    final float scale = screenWidth * 1.0f / imageInfo.getWidth();
                    final float scaleY = imageInfo.getHeight() * 1.0f / screenHeight;

                    photoDraweeView.setMaximumScale(scale * 3);
                    photoDraweeView.setMediumScale(scale);
                    photoDraweeView.post(new Runnable() {
                        @Override
                        public void run() {
                            photoDraweeView.setScale(scale * scaleY, 0, 0, false);
                        }
                    });
                }
                photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        photoDraweeView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if(mListener != null){
                    mListener.onItemClick(position, mData.get(position));
                }
            }
        });
        // 网络图片
//        ImageUtil.getBitmap(0, url, screenWidth, screenHeight, new ImageUtil.BitmapLoadListener() {
//            @Override
//            public void onBitmapLoaded(int id, Bitmap bitmap) {
//                if (ImageUtil.isBitmapAvailable(bitmap)) {
//                    int bitmapW = bitmap.getWidth();
//                    int bitmapH = bitmap.getHeight();
//                    float bitmapRatio = bitmapW * 1.0f / bitmapH;
//                    float scale;
//                    if (bitmapRatio > screenRatio) {
//                        // 齐宽
//                        scale = screenWidth * 1.0f / bitmapW;
//                    } else {
//                        // 齐高
//                        scale = screenHeight * 1.0f / bitmapH;
//                    }
//
//                    // 取得想要缩放的matrix参数
//                    Matrix matrix = new Matrix();
//                    matrix.postScale(scale, scale);
//                    // 得到新的图片
//                    Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                    if (photoView != null) {
//                        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                        photoView.setImageBitmap(finalBitmap);
//                    }
//                }
//            }
//        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    public interface OnItemClickListener{
        void onItemClick(int position, String data);
    }


}
