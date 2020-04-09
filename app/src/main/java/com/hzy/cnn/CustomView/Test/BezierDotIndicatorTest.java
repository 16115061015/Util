package com.hzy.cnn.CustomView.Test;

import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hzy.cnn.CustomView.R;
import com.hzy.cnn.CustomView.Ui.view.BezierDotIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: huzeyu
 * Date: 2019/11/29
 * Description:BezierDot实现实例
 */
public class BezierDotIndicatorTest extends AppCompatActivity {
    private BezierDotIndicator bezierDotIndicator;
    private ViewPager viewPager;

    //轮播数据集合
    private List<String> sourceDataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bezierdot_layout);

        bezierDotIndicator = findViewById(R.id.bezierDot);
        viewPager = findViewById(R.id.viewPager);

        initData();
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return sourceDataSets.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(BezierDotIndicatorTest.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
                Glide.with(BezierDotIndicatorTest.this)
                        .load(sourceDataSets.get(position))
                        .into(imageView);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        bezierDotIndicator.bind(viewPager);
    }

    private void initData() {
        sourceDataSets = new ArrayList<>();
        sourceDataSets.add("https://upload-images.jianshu.io/upload_images/2822163-70ac87aa2d2199d1.jpg");
        sourceDataSets.add("http://wx2.sinaimg.cn/mw690/ac38503ely1fesz8m0ov6j20qo140dix.jpg");
        sourceDataSets.add("http://pics6.baidu.com/feed/f31fbe096b63f624420c90e64f6551fc1b4ca3ac.jpeg?token=c8628666c96c99d4eb6fd4d310a5e89b&s=B2101CCCAE787A157E129F2C0300B05B");
    }
}
