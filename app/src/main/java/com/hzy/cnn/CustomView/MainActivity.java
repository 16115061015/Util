package com.hzy.cnn.CustomView;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hzy.cnn.CustomView.Ui.FreshView;
import com.hzy.cnn.waveview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //WavewView 测试实例
//        final WaveView waveView=findViewById(R.id.wv);
//       new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                waveView.startWaveMove();
//                Looper.loop();
//            }
//        }).start();

        //alipaysuccessview 使用实例
//        AlipaySuccessView alipaySuccessView=findViewById(R.id.av);
//        alipaySuccessView.startAnimator();

//        //自带下拉刷新控件测试
//        SwipeRefreshLayout  srl=findViewById(R.id.srl);
//        // 设置手指在屏幕下拉多少距离会触发下拉刷新
//        srl.setDistanceToTriggerSync(300);
//        // 设定下拉圆圈的背景
//        srl.setProgressBackgroundColorSchemeColor(Color.WHITE);
//        // 设置圆圈的大小
//        srl.setSize(SwipeRefreshLayout.LARGE);
//        srl.setOnRefreshListener(this);
        //自定义下拉刷新控件的使用
//        FreshView freshView=findViewById(R.id.fv);
//        ListView lv=findViewById(R.id.lv);
//        SimpleAdapter simpleAdapter=new SimpleAdapter(this,getData(),
//                R.layout.listdataview,
//                new String[]{"title","info"},
//                new int[]{R.id.title,R.id.info});
//        lv.setAdapter(simpleAdapter);
//        freshView.setFreshVieListener(new FreshView.FreshViewListener() {
//            @Override
//            public void FreshFinish() {
//                Log.i("刷新完毕", "FreshFinish: ");
//            }
//
//            @Override
//            public void LoadFinish() {
//                Log.i("加载完毕", "FreshFinish: ");
//            }
//        });

    }

    @Override
    public void onRefresh() {
        Log.i("刷新", "onRefresh: 刷新状态");
        Toast.makeText(this,"刷新成功!",Toast.LENGTH_SHORT).show();
//        SwipeRefreshLayout  srl=findViewById(R.id.srl);
//        srl.setRefreshing(false);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "图片");
        map.put("info", "美辰良景，给你无限的遐思，让人感觉无限温馨……");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "音乐");
        map.put("info", "轻曼音乐，令人如入仙境，如痴如醉……");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "视频");
        map.put("info", "震撼场景，360度的视觉捕获，一览无遗……");
        list.add(map);

        return list;
    }

}
