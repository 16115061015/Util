package com.example.UiDemo.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.UiDemo.R;
import com.example.UiDemo.config.UiRouterConfig;

/**
 * Created by joel.
 * Date: 2019/5/17
 * Time: 20:04
 * Description: 音乐波浪动画
 */
@Route(path = UiRouterConfig.WavePlay)
public class AtyWavePlay extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_waveplay);
    }
}
