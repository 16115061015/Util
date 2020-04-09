package com.hzy.cnn.CustomView.Test;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hzy.cnn.CustomView.Ui.view.WaveView;
import com.hzy.cnn.CustomView.R;

/**
 * Created by joel.
 * Date: 2019/5/17
 * Time: 20:05
 * Description:
 */
public class WaveViewTest extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waveview_layout);
        //WavewView 测试实例
        final WaveView waveView=findViewById(R.id.wv);

        waveView.startWaveMove();


    }
}
