package com.sgxy.hzy.photoselector.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.avoidonresult.AvoidOnResult;
import com.sgxy.hzy.photoselector.base.BaseActivity;
import com.sgxy.hzy.photoselector.preview.PhotoPreviewActivity;
import com.sgxy.hzy.photoselector.recyclerview.PhotoItemDecoration;
import com.sgxy.hzy.photoselector.recyclerview.SafeGridLayoutManager;
import com.sgxy.hzy.photoselector.recyclerview.multiple.MultiplePhotoAdapter;
import com.sgxy.hzy.photoselector.recyclerview.multiple.OnMultipleSelectListener;
import com.sgxy.hzy.photoselector.recyclerview.single.OnSingleSelectListener;
import com.sgxy.hzy.photoselector.recyclerview.single.SinglePhotoAdapter;
import com.sgxy.hzy.photoselector.util.ImageUtil;
import com.sgxy.hzy.photoselector.util.PhotoSelectorUtil;
import com.sgxy.hzy.photoselector.util.ToastCompat;

import java.util.ArrayList;
import java.util.List;

import static com.sgxy.hzy.photoselector.widget.cropimage.CropImageView.DEFAULT_SCALE_SIZE;

public class PhotoSelectorActivity extends BaseActivity implements ImageScanner.ScanCompleteCallBack
        , View.OnClickListener, PhotoAlbumChooseDialog.OnPhotoAlbumChosenListener {

    public static final String TAG = "PhotoSelectorActivity";

    public static final String SELECT_DATA = "select_data";
    private static final String TYPE = "type";
    private static final String IS_CROP = "is_crop";
    private static final String SIZE_SCALE = "size_scale";
    private static final String COLUMN = "column";
    private static final String LIMIT = "limit";
    private static final String SUPPORT_GIF = "support_gif";

    public static final String TYPE_SINGLE = "type_single";
    public static final String TYPE_MULTIPLE = "type_multiple";

    private String mType = "";
    private boolean mIsCrop = false;//是否裁剪
    private float mSizeScale = DEFAULT_SCALE_SIZE;//默认的宽高比
    private int mColumn = 4;//列数
    private int mLimit = 9;

    private boolean supportGif = false;


    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvComplete;
    private View v_loading;
    private RecyclerView recyclerView;
    private SafeGridLayoutManager layoutManager;
    private int imageWidth;
    private ImageScanner imageScanner;

    private MultiplePhotoAdapter mMultiplePhotoAdapter;
    private SinglePhotoAdapter mSinglePhotoAdapter;

    public static Intent getSingleIntent(Context context, boolean isCrop, float sizeScale, int column) {
        return getSingleIntent(context, isCrop, sizeScale, column, false);
    }

    public static Intent getSingleIntent(Context context, boolean isCrop, float sizeScale, int column, Boolean supportGif) {
        Intent intent = new Intent(context, PhotoSelectorActivity.class);
        intent.putExtra(TYPE, TYPE_SINGLE);
        intent.putExtra(IS_CROP, isCrop);
        intent.putExtra(SIZE_SCALE, sizeScale);
        intent.putExtra(COLUMN, column);
        intent.putExtra(LIMIT, 1);
        intent.putExtra(SUPPORT_GIF, supportGif);
        return intent;
    }

    public static Intent getMultipleIntent(Context context, int column, int limit) {
        return getMultipleIntent(context, column, limit, false);
    }

    public static Intent getMultipleIntent(Context context, int column, int limit, boolean showGif) {
        Intent intent = new Intent(context, PhotoSelectorActivity.class);
        intent.putExtra(TYPE, TYPE_MULTIPLE);
        intent.putExtra(COLUMN, column);
        if (limit > 0) {
            intent.putExtra(LIMIT, limit);
        }
        intent.putExtra(SUPPORT_GIF, showGif);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selector);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(TYPE)) {
                mType = intent.getStringExtra(TYPE);
            }
            if (intent.hasExtra(IS_CROP)) {
                mIsCrop = intent.getBooleanExtra(IS_CROP, false);
            }
            if (intent.hasExtra(SIZE_SCALE)) {
                mSizeScale = intent.getFloatExtra(SIZE_SCALE, DEFAULT_SCALE_SIZE);
            }
            if (intent.hasExtra(COLUMN)) {
                mColumn = intent.getIntExtra(COLUMN, 4);
            }
            if (intent.hasExtra(LIMIT)) {
                mLimit = intent.getIntExtra(LIMIT, 1);
            }
            if (intent.hasExtra(SUPPORT_GIF)) {
                supportGif = intent.getBooleanExtra(SUPPORT_GIF, false);
            }
        }

        v_loading = findViewById(R.id.v_loading);
        tvTitle = (TextView) findViewById(R.id.title);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvComplete = (TextView) findViewById(R.id.tv_complete);
        recyclerView = findViewById(R.id.recycle_view);

        tvTitle.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvComplete.setOnClickListener(this);

        imageWidth = ImageUtil.getScreenWidth(this) / mColumn / 2;
        layoutManager = new SafeGridLayoutManager(this, mColumn);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);

        initAdapter();

        imageScanner = new ImageScanner(this);
        imageScanner.scanImages(supportGif, this);

    }

    @Override
    protected void onDestroy() {
        imageScanner.onDestroy();
        PhotoAlbumManager.ins().release();
        super.onDestroy();
    }

    private void initAdapter() {
        if (TYPE_SINGLE.equalsIgnoreCase(mType)) {
            initSinglePhotoAdapter();
        } else {
            initMultiplePhotoAdapter();
        }
    }

    private void initSinglePhotoAdapter() {
        tvComplete.setVisibility(View.GONE);
        mSinglePhotoAdapter = new SinglePhotoAdapter(new ArrayList<PhotoInfo>(), imageWidth, new OnSingleSelectListener() {
            @Override
            public void onItemClick(int position, PhotoInfo data) {
                if (mIsCrop) {
                    showCrop(data);
                } else {
                    ArrayList<PhotoInfo> selectedDataList = new ArrayList<>();
                    selectedDataList.add(data);
                    returnResult(selectedDataList);
                }
            }
        });
        recyclerView.addItemDecoration(new PhotoItemDecoration(mColumn, mSinglePhotoAdapter, this));
        recyclerView.setAdapter(mSinglePhotoAdapter);
    }

    private void initMultiplePhotoAdapter() {
        tvComplete.setEnabled(false);
        mMultiplePhotoAdapter = new MultiplePhotoAdapter(new ArrayList<PhotoInfo>(), mLimit, imageWidth, new OnMultipleSelectListener() {
            @Override
            public void onItemClick(int position, PhotoInfo data) {
                showPreview(position);
            }

            @Override
            public void onSelectClick(int position, int selectCount, int limit) {
                updateStatus();
            }

            @Override
            public void onSelectLimit(int position, int selectCount, int limit) {
                new ToastCompat().showToast(PhotoSelectorActivity.this, String.format("最多选择%d张图片", limit));
            }
        });
        recyclerView.addItemDecoration(new PhotoItemDecoration(mColumn, mMultiplePhotoAdapter, this));
        recyclerView.setAdapter(mMultiplePhotoAdapter);
    }

    private void updateStatus() {
        if (mMultiplePhotoAdapter != null) {
            ArrayList<PhotoInfo> selectDataList = mMultiplePhotoAdapter.getSelectDataList();
            int selectCount = selectDataList.size();
            tvComplete.setText(selectCount > 0 ? String.format("完成(%s)", selectCount) : "完成");
            tvComplete.setEnabled(selectCount > 0);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            finish();
        } else if (v.getId() == R.id.tv_complete) {
            if (mMultiplePhotoAdapter != null) {
                returnResult(mMultiplePhotoAdapter.getSelectDataList());
            }
        } else if (v.getId() == R.id.title) {
            PhotoAlbumChooseDialog dialog = new PhotoAlbumChooseDialog(this);
            dialog.setOnPhotoAlbumChosenListener(this);
            dialog.show();
        }
    }

    private void returnResult(ArrayList<PhotoInfo> selectedDataList) {
        Intent intent = new Intent();
        intent.putExtra(SELECT_DATA, selectedDataList);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onPreExecute() {
        v_loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onScanComplete(final List<PhotoAlbum> photoAlbumList) {
        v_loading.setVisibility(View.GONE);
        if (photoAlbumList != null && !photoAlbumList.isEmpty()) {
            PhotoAlbumManager.ins().setPhotoAlbumList(photoAlbumList);
            PhotoAlbum album = photoAlbumList.get(0);
            album.isChosen = true;
            onPhotoAlbumChosen(album);
        }
    }

    @Override
    public void onPhotoAlbumChosen(PhotoAlbum album) {
        PhotoAlbumManager.ins().setCurrentAlbum(album);
        tvTitle.setText(album.name);
        setData(album);
    }

    private void setData(PhotoAlbum album) {
        if (mSinglePhotoAdapter != null) {
            mSinglePhotoAdapter.setData(album);
        }
        if (mMultiplePhotoAdapter != null) {
            mMultiplePhotoAdapter.setDataWithReset(album, true);
            updateStatus();
        }
    }

    private void showPreview(int position) {
        Intent intent = PhotoPreviewActivity.getStartIntent(PhotoSelectorActivity.this, position, mLimit);
        new AvoidOnResult(PhotoSelectorActivity.this).startForResult(intent, new AvoidOnResult.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (mMultiplePhotoAdapter != null) {
                    mMultiplePhotoAdapter.notifyDataSetChanged();
                    updateStatus();
                }
                if (resultCode == PhotoPreviewActivity.ACTION_COMPLETE) {
                    if (mMultiplePhotoAdapter != null) {
                        returnResult(mMultiplePhotoAdapter.getSelectDataList());
                    }
                }
            }
        });
    }

    private void showCrop(final PhotoInfo info) {
        PhotoSelectorUtil.showCrop(PhotoSelectorActivity.this, ImageUtil.FrescoLocalFileURI + info.path, mSizeScale, new PhotoSelectorUtil.SelectedPhotoCallback() {
            @Override
            public void onActivityResult(List<PhotoInfo> list) {
                ArrayList<PhotoInfo> selectedDataList = new ArrayList<>(list);
                returnResult(selectedDataList);
            }
        });
    }
}
