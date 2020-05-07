package com.example.UiDemo.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.UiDemo.R;
import com.example.UiDemo.config.UiRouterConfig;

/**
 * Author: huzeyu
 * Date: 2019/11/28
 * Description:
 */
@Route(path = UiRouterConfig.WaveBubblesView)
public class AtyWaveBubblesView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_wavebubbleview);
        //WaveBubblesView 使用实例

    }
}