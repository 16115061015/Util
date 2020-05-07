package com.example.UiDemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.example.UiDemo.R;
import com.example.UiDemo.config.UiRouterConfig;
import com.example.UiDemo.view.BannerView;
import com.example.UiDemo.view.BannerViewIndicator;

import java.util.ArrayList;
import java.util.List;
@Route(path = UiRouterConfig.BannerViewIndicator)
public class AtyBannerViewIndicator extends AppCompatActivity {
    private BannerViewIndicator bezierDotIndicator;
    private BannerView viewPager;

    //轮播数据集合
    private List<String> sourceDataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_bannerview_indicator);

        bezierDotIndicator = findViewById(R.id.bezierDot);
        viewPager = findViewById(R.id.viewPager);

        ArrayList<View> textViews = new ArrayList<>();
        View view1 = this.getLayoutInflater().inflate(R.layout.aty_bannerview_item, null);
        View view2 = this.getLayoutInflater().inflate(R.layout.aty_bannerview_item, null);
        View view3 = this.getLayoutInflater().inflate(R.layout.aty_bannerview_item, null);
        Glide.with(this)
                .load("https://upload-images.jianshu.io/upload_images/2822163-70ac87aa2d2199d1.jpg")
                .into((ImageView) view1.findViewById(R.id.banner_view_src));
        Glide.with(this)
                .load("http://wx2.sinaimg.cn/mw690/ac38503ely1fesz8m0ov6j20qo140dix.jpg")
                .into((ImageView) view2.findViewById(R.id.banner_view_src));
        Glide.with(this)
                .load("http://pics6.baidu.com/feed/f31fbe096b63f624420c90e64f6551fc1b4ca3ac.jpeg?token=c8628666c96c99d4eb6fd4d310a5e89b&s=B2101CCCAE787A157E129F2C0300B05B")
                .into((ImageView) view3.findViewById(R.id.banner_view_src));
        textViews.add(view1);
        textViews.add(view2);
        textViews.add(view3);
        viewPager.setSourceDataSets(textViews);

        bezierDotIndicator.bind(viewPager);
    }

}