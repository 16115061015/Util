package com.sgxy.hzy.photoselector.preview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.base.BaseActivity;

import java.util.ArrayList;

public class BasePreviewActivity extends BaseActivity implements PhotoPreviewAdapter.OnItemClickListener, ViewPager.OnPageChangeListener{
    private static final String IMGURLS = "imgUrls";
    public static final String TAG = "BasePreviewActivity";

    protected RelativeLayout root_view;
    protected ViewPager viewPager;
    protected PhotoPreviewAdapter mPhotoPreviewAdapter;
    private ArrayList<String> mImgUrls;

    public static void startActivity(Context context, ArrayList<String> imgUrls){
        Intent intent = new Intent(context, BasePreviewActivity.class);
        intent.putExtra(IMGURLS, imgUrls);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_preview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        root_view = findViewById(R.id.root_view);
        viewPager = findViewById(R.id.viewpager);
        mPhotoPreviewAdapter = new PhotoPreviewAdapter(this, new ArrayList<String>());
        viewPager.setAdapter(mPhotoPreviewAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
        mPhotoPreviewAdapter.setListener(this);
        initCustomView(root_view);
    }

    protected void initCustomView(View rootView){
        Intent intent = getIntent();
        if(intent != null ){
            if(intent.hasExtra(IMGURLS)){
                mImgUrls = intent.getStringArrayListExtra(IMGURLS);
            }
        }
        View actionView = LayoutInflater.from(this).inflate(R.layout.activity_base_preview_action, null, false);
        root_view.addView(actionView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setImmerseHeight(actionView);
            actionView.setFitsSystemWindows(true);
        }

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(mImgUrls != null && !mImgUrls.isEmpty()){
            mPhotoPreviewAdapter.setData(mImgUrls);
        }
    }

    protected void setImmerseHeight(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setPadding(0, getStatusBarHeight(this), 0, 0);
        }
    }

    public int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    @Override
    public void onItemClick(int position, String data) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
