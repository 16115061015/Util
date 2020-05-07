package com.example.UiDemo.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.UiDemo.R;
import com.example.UiDemo.config.UiRouterConfig;
import com.example.UiDemo.view.AlipaySuccessView;

/**
 * Created by joel.
 * Date: 2019/5/17
 * Time: 19:53
 * Description: 支付效果演示
 */
@Route(path = UiRouterConfig.AlipaySuccessView)
public class AtyAlipaySuccessView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_alpipaysuccessview);
        //alipaysuccessview 使用实例
        AlipaySuccessView alipaySuccessView = findViewById(R.id.alpv);
        if (alipaySuccessView != null) {
            alipaySuccessView.startAnimator();
        }
    }
}
