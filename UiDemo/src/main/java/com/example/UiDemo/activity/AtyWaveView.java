package com.example.UiDemo.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.UiDemo.R;
import com.example.UiDemo.config.UiRouterConfig;
import com.example.UiDemo.view.WaveView;

/**
 * Created by joel.
 * Date: 2019/5/17
 * Time: 20:05
 * Description:
 */
@Route(path = UiRouterConfig.WaveView)
public class AtyWaveView extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_waveview);
        //WavewView 测试实例
        final WaveView waveView=findViewById(R.id.wv);

        waveView.startWaveMove();


    }
}
