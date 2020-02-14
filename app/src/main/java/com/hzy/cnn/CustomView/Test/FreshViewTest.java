package com.hzy.cnn.CustomView.Test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hzy.cnn.CustomView.Ui.view.FreshView;
import com.hzy.cnn.CustomView.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreshViewTest extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freshview_layout);


       // 自定义下拉刷新控件的使用
        FreshView freshView=findViewById(R.id.fv);
//        ListView lv=findViewById(R.id.lv);
//        SimpleAdapter simpleAdapter=new SimpleAdapter(this,getData(),
//                R.layout.listdataview,
//                new String[]{"title","info"},
//                new int[]{R.id.title,R.id.info});
//        lv.setAdapter(simpleAdapter);
        freshView.setFreshVieListener(new FreshView.FreshViewListener() {
            @Override
            public void FreshFinish() {
                Log.i("刷新完毕", "FreshFinish: ");
            }

            @Override
            public void LoadFinish() {
                Log.i("加载完毕", "FreshFinish: ");
            }
        });









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
