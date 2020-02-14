package com.hzy.cnn.CustomView.Test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzy.cnn.CustomView.R;
import com.hzy.cnn.CustomView.Ui.adapter.LabelAdapter;
import com.hzy.cnn.CustomView.Ui.viewgroup.LabelLayout;

import java.util.ArrayList;
import java.util.List;

public class LabelLayoutTest extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.label_layout);

        final List<String> datas = new ArrayList<>();
        datas.add("123");
        datas.add("345");
        datas.add("1232");
        datas.add("421");
        datas.add("312");
        datas.add("341");
        LabelAdapter<String> labelAdapter = new LabelAdapter<String>(datas) {
            @Override
            public View getView(ViewGroup parent, int position, String s) {
                View view = View.inflate(parent.getContext(), R.layout.item_label, null);
                TextView textView = view.findViewById(R.id.text);
                textView.setText(s);
                return view;
            }
        };
        LabelLayout labelLayout = findViewById(R.id.label_layout);
        labelLayout.setAdapter(labelAdapter);
    }
}
