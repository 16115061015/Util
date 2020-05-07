package com.example.UiDemo.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.UiDemo.R;
import com.example.UiDemo.config.UiRouterConfig;
import com.example.UiDemo.layout.LabelLayout.LabelAdapter;
import com.example.UiDemo.layout.LabelLayout.LabelLayout;

import java.util.ArrayList;
import java.util.List;
@Route(path = UiRouterConfig.LabelLayout)
public class AtyLabelLayout extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_label_layout);

        final List<String> datas = new ArrayList<>();
        datas.add("123");
        datas.add("345");
        datas.add("1232");
        datas.add("421ø");
        datas.add("312");
        datas.add("341");
        LabelAdapter<String> labelAdapter = new LabelAdapter<String>(datas) {
            @Override
            public View getView(ViewGroup parent, int position, String s) {
                View view = View.inflate(parent.getContext(), R.layout.aty_label_layout_item, null);
                TextView textView = view.findViewById(R.id.text);
                textView.setText(s);
                return view;
            }
        };
        LabelLayout labelLayout = findViewById(R.id.label_layout);
        labelLayout.setAdapter(labelAdapter);
    }
}
