package com.sgxy.hzy.photoselector.preview;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.album.PhotoAlbum;
import com.sgxy.hzy.photoselector.album.PhotoAlbumManager;
import com.sgxy.hzy.photoselector.album.PhotoInfo;
import com.sgxy.hzy.photoselector.util.ImageUtil;
import com.sgxy.hzy.photoselector.util.ToastCompat;

import java.util.ArrayList;

public class PhotoPreviewActivity extends BasePreviewActivity implements View.OnClickListener, PhotoPreviewAdapter.OnItemClickListener {

    public static final String TAG = "PhotoPreviewActivity";
    public static final String POSITION = "position";
    public static final String LIMIT = "limit";
    public static final int ACTION_BACK = 11;
    public static final int ACTION_COMPLETE = 12;

    private View photo_preview;
    private View top_container;
    private View bottom_container;
    private TextView txt_complete;
    private TextView tv_select;

    private boolean isShow = true;
    private int mPosition = 0;
    private int mLimit = 9;
    private PhotoAlbum album;

    public static Intent getStartIntent(Context context, int position, int limit) {
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        intent.putExtra(POSITION, position);
        intent.putExtra(LIMIT, limit);
        return intent;
    }

    @Override
    protected void initCustomView(View rootView) {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(POSITION)) {
                mPosition = intent.getIntExtra(POSITION, 0);
            }
            if (intent.hasExtra(LIMIT)) {
                mLimit = intent.getIntExtra(LIMIT, 9);
            }
        }
        photo_preview = LayoutInflater.from(this).inflate(R.layout.activity_photo_preview, root_view, false);
        root_view.addView(photo_preview);
        top_container = findViewById(R.id.top_container);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setImmerseHeight(top_container);
            top_container.setFitsSystemWindows(true);
        }

        bottom_container = findViewById(R.id.bottom_container);
        txt_complete = findViewById(R.id.txt_complete);
        tv_select = findViewById(R.id.tv_select);

        findViewById(R.id.iv_back).setOnClickListener(this);
        txt_complete.setOnClickListener(this);
        tv_select.setOnClickListener(this);

        album = PhotoAlbumManager.ins().getCurrentAlbum();
        if (album != null) {
            ArrayList<String> pathList = new ArrayList<>();
            if (album.photoList != null) {
                for (PhotoInfo photoinfo : album.photoList) {
                    pathList.add(ImageUtil.FrescoLocalFileURI + photoinfo.path);
                }
            }
            mPhotoPreviewAdapter.setData(pathList);
            viewPager.setCurrentItem(mPosition);
            updateStatus();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            setResult(ACTION_BACK);
            finish();
        } else if (v.getId() == R.id.tv_select) {
            onItemSelect();
        } else if (v.getId() == R.id.txt_complete) {
            setResult(ACTION_COMPLETE);
            finish();
        }
    }

    @Override
    public void onItemClick(int position, String data) {
        isShow = !isShow;
        if (isShow) {
            top_container.setVisibility(View.VISIBLE);
            bottom_container.setVisibility(View.VISIBLE);
        } else {
            top_container.setVisibility(View.GONE);
            bottom_container.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        mPosition = position;
        updateStatus();
    }

    private void onItemSelect() {
        if (album != null) {
            PhotoInfo info = album.photoList.get(mPosition);
            int selectedSize = album.selectedList.size();
            if (info.isSelected) {//可取消选择
                info.isSelected = false;
                album.selectedList.remove(info);
            } else if (selectedSize < mLimit) {//可添加
                info.isSelected = true;
                album.selectedList.add(info);
            } else {//达到上限
                new ToastCompat().showToast(this, String.format("最多选择%d张图片", mLimit));
            }
            updateStatus();
        }
    }

    private void updateStatus() {
        if (album != null) {
            PhotoInfo info = album.photoList.get(mPosition);
            int selectedSize = album.selectedList.size();
            if (info.isSelected) {
                tv_select.setSelected(true);
                int indexOf = album.selectedList.indexOf(info);
                tv_select.setText(String.valueOf(indexOf + 1));
            } else {
                tv_select.setSelected(false);
                tv_select.setText("");
            }

            if (selectedSize == 0) {
                txt_complete.setText("完成");
                txt_complete.setEnabled(false);
            } else {
                txt_complete.setText("完成(" + selectedSize + ")");
                txt_complete.setEnabled(true);
            }
        }
    }
}
