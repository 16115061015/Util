package com.sgxy.hzy.photoselector.crop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.request.ImageRequest;
import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.base.BaseActivity;
import com.sgxy.hzy.photoselector.util.ImageUtil;
import com.sgxy.hzy.photoselector.util.StorageUtils;
import com.sgxy.hzy.photoselector.util.ToastCompat;
import com.sgxy.hzy.photoselector.widget.cropimage.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static com.sgxy.hzy.photoselector.widget.cropimage.CropImageView.DEFAULT_SCALE_SIZE;

public class PhotoCropActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "PhotoCropActivity";
    public static final String URL = "url";
    public static final String SIZE_SCALE = "sizeScale";
    public static final String SAVE_PATH = "save_path";

    public static final int ACTION_BACK = 11;
    public static final int ACTION_COMPLETE = 12;
    private RelativeLayout root_view;
    protected CropImageView image;//裁剪用的ImageView
    protected Button zoomin;//缩小
    protected Button zoomout;//放大
    protected Button left;//左移动
    protected Button right;//右移动
    protected TextView tv_cancel, tv_select;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int screenWidth;
    private int screenHeight;

    private float mSizeScale = DEFAULT_SCALE_SIZE;//裁剪区域的宽高比-默认是1:1
    private String mUrl;
    private String mSavePath;

    public static Intent getStartIntent(Context context, String url, float sizeScale) {
        Intent intent = new Intent(context, PhotoCropActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(SIZE_SCALE, sizeScale);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_crop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(URL)) {
                mUrl = intent.getStringExtra(URL);
            }
            if (intent.hasExtra(SIZE_SCALE)) {
                mSizeScale = intent.getFloatExtra(SIZE_SCALE, DEFAULT_SCALE_SIZE);
            }
        }

        screenWidth = ImageUtil.getScreenWidth(this);
        screenHeight = ImageUtil.getScreenHeight(this);

        root_view = findViewById(R.id.root_view);
        image = (CropImageView) findViewById(R.id.photo_show);
        image.setNeedDash(false);

        zoomin = (Button) findViewById(R.id.zoomin);
        zoomout = (Button) findViewById(R.id.zoomout);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_select = (TextView) findViewById(R.id.tv_select);

        zoomin.setOnClickListener(this);
        zoomout.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_select.setOnClickListener(this);
        setCropImageSizeScale(mSizeScale);
        ImageUtil.getBitmap(mUrl, screenWidth, screenHeight, ImageRequest.CacheChoice.DEFAULT, new BaseBitmapDataSubscriber() {

            @Override
            public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                if (null != bitmap && !bitmap.isRecycled()) {
                    final Bitmap resultBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            image.setImageBitmapResetBase(resultBitmap, true);
                        }
                    });
                } else {
                    new ToastCompat().showToast(PhotoCropActivity.this, "获取图片失败");
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                new ToastCompat().showToast(PhotoCropActivity.this, "获取图片失败");
            }
        });

    }

    /**
     * 设置裁剪的宽高比
     */
    public void setCropImageSizeScale(float sizeScale) {
        this.image.setSizeScale(sizeScale);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            setResult(ACTION_BACK);
            finish();
        } else if (v.getId() == R.id.tv_select) {
            save();
        } else if (v.getId() == R.id.zoomin) {
            image.zoomIn();
        } else if (v.getId() == R.id.zoomout) {
            image.zoomOut();
        } else if (v.getId() == R.id.left) {
            image.setRoate(-90);
            image.postInvalidate();
        } else if (v.getId() == R.id.right) {
            image.setRoate(90);
            image.postInvalidate();
        }
    }

    private void save() {
        try {
            Bitmap b = null;
            root_view.setDrawingCacheEnabled(false);
            root_view.setDrawingCacheEnabled(true);
            b = Bitmap.createBitmap(root_view.getDrawingCache());
            b = Bitmap.createBitmap(b, image.getCropLeft(), image.getCropTop(),
                    image.getCropRight() - image.getCropLeft(), image.getCropBottom() - image.getCropTop());
            if (b == null) {
                new ToastCompat().showToast(PhotoCropActivity.this, "操作失败请稍后重试");
                return;
            }

            String filePath = "crop" + System.currentTimeMillis() + ".jpg";//不加上时间的话 加载本地图片会一直复用以前的图片
            File fileDir = new File(getCropDataCachePath());
            if (!fileDir.exists()) {//如果文件夹不存在
                fileDir.mkdirs();
            }
            File f = new File(getCropDataCachePath(), filePath);
            mSavePath = f.getPath();
            if (f.exists()) {
                f.delete();
            }
            OutputStream stream = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            new ToastCompat().showToast(this, "保存失败 ");
        }

        Intent intent = new Intent();
        intent.putExtra(SAVE_PATH, mSavePath);
        setResult(ACTION_COMPLETE, intent);
        finish();
    }

    public String getCropDataCachePath() {
        return StorageUtils.getCropDataCachePath(this);
    }
}
